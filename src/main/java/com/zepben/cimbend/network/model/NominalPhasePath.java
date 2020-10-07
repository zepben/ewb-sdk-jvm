/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
