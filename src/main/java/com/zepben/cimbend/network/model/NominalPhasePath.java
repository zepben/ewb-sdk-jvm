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
package com.zepben.cimbend.network.model;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind;

import java.util.Objects;

/**
 * Defines how a nominal phase is wired through a connectivity node between two terminals
 */
@EverythingIsNonnullByDefault
public class NominalPhasePath {

    private final SinglePhaseKind from;
    private final SinglePhaseKind to;

    public static NominalPhasePath between(SinglePhaseKind from, SinglePhaseKind to) {
        return new NominalPhasePath(from, to);
    }

    /**
     * @return The nominal phase where the path comes from.
     */
    public SinglePhaseKind from() {
        return from;
    }

    /**
     * @return The nominal phase where the path goes to.
     */
    public SinglePhaseKind to() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NominalPhasePath)) return false;
        NominalPhasePath nominalPhasePath = (NominalPhasePath) o;
        return from == nominalPhasePath.from &&
            to == nominalPhasePath.to;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return "NominalPhasePath{" +
            "from=" + from +
            ", to=" + to +
            '}';
    }

    private NominalPhasePath(SinglePhaseKind from, SinglePhaseKind to) {
        this.from = from;
        this.to = to;
    }

}
