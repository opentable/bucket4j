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

package com.github.bucket4j.grid.ignite;

import com.github.bucket4j.common.BucketConfiguration;
import com.github.bucket4j.common.BucketState;
import com.github.bucket4j.grid.GridCommand;

import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;
import java.io.Serializable;

public class IgniteCommand<T extends Serializable> implements EntryProcessor<Object, BucketState, T> {

    private final GridCommand<T> targetCommand;
    private final BucketConfiguration configuration;

    public IgniteCommand(GridCommand<T> targetCommand, BucketConfiguration configuration) {
        this.targetCommand = targetCommand;
        this.configuration = configuration;
    }

    @Override
    public T process(MutableEntry<Object, BucketState> mutableEntry, Object... arguments) throws EntryProcessorException {
        BucketState state = mutableEntry.getValue();
        boolean restoredFromCrash = false;
        if (state == null) {
            // The state is missed, looks like outage happen in the GRID, so lets recreate initial state
            state = BucketState.createInitialState(configuration);
            restoredFromCrash = true;
        }

        T result = targetCommand.execute(state, configuration);
        if (targetCommand.isBucketStateModified() || restoredFromCrash) {
            mutableEntry.setValue(state);
        }
        return result;
    }

}
