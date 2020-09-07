/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.traversals;

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
