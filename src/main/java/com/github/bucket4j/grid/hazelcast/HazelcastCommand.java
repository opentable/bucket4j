/*
 * Copyright 2015 Vladimir Bukhtoyarov
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.bucket4j.grid.hazelcast;

import com.github.bucket4j.common.BucketState;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;

import java.io.Serializable;
import java.util.Map;

public class HazelcastCommand<T extends Serializable> implements EntryProcessor<Object, BucketState>, Serializable {

    private final GridCommand<T> targetCommand;
    private BucketState snapshotToBackup;

    public HazelcastCommand(GridCommand<T> targetCommand) {
        this.targetCommand = targetCommand;
    }

    @Override
    public T process(Map.Entry<Object, BucketState> entry) {
        BucketState gridState = entry.getValue();
        T result = targetCommand.execute(gridState);
        if (targetCommand.isBucketStateModified()) {
            entry.setValue(gridState);
            snapshotToBackup = gridState.getBucketState().clone();
        }
        return result;
    }

    @Override
    public EntryBackupProcessor getBackupProcessor() {
        if (snapshotToBackup == null) {
            return null;
        }
        return new HazelcastReplicant(snapshotToBackup);
    }
}
