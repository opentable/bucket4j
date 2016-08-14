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
import com.github.bucket4j.grid.GridProxy;
import org.apache.ignite.IgniteCache;

import java.io.Serializable;

public class IgniteProxy implements GridProxy {

    private final IgniteCache<Object, BucketState> cache;
    private final Object key;
    private final BucketConfiguration configuration;

    public IgniteProxy(IgniteCache<Object, BucketState> cache, Object key, BucketConfiguration configuration) {
        this.cache = cache;
        this.key = key;
        this.configuration = configuration;
    }

    @Override
    public <T extends Serializable> T execute(GridCommand<T> command) {
        return cache.invoke(key, new IgniteCommand<>(command, configuration));
    }

    @Override
    public void setInitialState(BucketState initialState) {
        cache.putIfAbsent(key, initialState);
    }

}
