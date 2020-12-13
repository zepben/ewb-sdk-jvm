/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.traversals;

import com.zepben.annotations.EverythingIsNonnullByDefault;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple tracker for traversals that just tracks the items visited and the order visited.
 */
@EverythingIsNonnullByDefault
public class BasicTracker<T> implements Tracker<T> {

    private Set<T> visited;

    public BasicTracker() {
        visited = new HashSet<>();
    }

    @Override
    public boolean hasVisited(@Nullable T item) {
        return visited.contains(item);
    }

    @Override
    public boolean visit(@Nullable T item) {
        return visited.add(item);
    }

    @Override
    public void clear() {
        visited.clear();
    }

}
