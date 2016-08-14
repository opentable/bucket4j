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

package com.github.bucket4j.grid.coherence;

import com.github.bucket4j.common.BucketConfiguration;
import com.github.bucket4j.common.BucketState;
import com.github.bucket4j.grid.GridCommand;
import com.tangosol.util.InvocableMap;
import com.tangosol.util.processor.AbstractProcessor;

import java.io.Serializable;

public class CoherenceCommand<T extends Serializable> extends AbstractProcessor {

    private final GridCommand<T> command;
    private final BucketConfiguration configuration;

    public CoherenceCommand(GridCommand<T> command, BucketConfiguration configuration) {
        this.command = command;
        this.configuration = configuration;
    }

    @Override
    public Object process(InvocableMap.Entry entry) {
        boolean restoredFromCrash = false;
        BucketState state = (BucketState) entry.getValue();
        if (state == null) {
            // The state is missed, looks like outage happen in the GRID, so lets recreate initial state
            state = BucketState.createInitialState(configuration);
            restoredFromCrash = true;
        }

        T result = command.execute(state, configuration);
        if (command.isBucketStateModified() || restoredFromCrash) {
            entry.setValue(state);
        }
        return result;
    }

}
