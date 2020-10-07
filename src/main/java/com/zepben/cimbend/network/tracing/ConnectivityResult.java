/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.network.tracing;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment;
import com.zepben.cimbend.cim.iec61970.base.core.Terminal;
import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind;
import com.zepben.cimbend.network.model.NominalPhasePath;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Stores the connectivity between two terminals, including the mapping between the nominal phases.
 */
@EverythingIsNonnullByDefault
@SuppressWarnings("WeakerAccess")
public class ConnectivityResult {

    private final Terminal fromTerminal;
    private final Terminal toTerminal;
    private final List<NominalPhasePath> nominalPhasePaths;

    /**
     * @param fromTerminal The terminal for which the connectivity was requested.
     * @param toTerminal   The terminal which is connected to the requested terminal.
     * @return The ConnectivityResult Builder to use to construct the result.
     */
    public static ConnectivityResult between(Terminal fromTerminal, Terminal toTerminal, Collection<NominalPhasePath> nominalPhasePaths) {
        return new ConnectivityResult(fromTerminal, toTerminal, nominalPhasePaths);
    }

    /**
     * @return Convenience method for getting the conducting equipment that owns the fromTerminal.
     */
    public ConductingEquipment from() {
        return fromTerminal.getConductingEquipment();
    }

    /**
     * @return The terminal from which the connectivity was requested.
     */
    public Terminal fromTerminal() {
        return fromTerminal;
    }

    /**
     * @return Convenience method for getting the conducting equipment that owns the toTerminal.
     */
    public ConductingEquipment to() {
        return toTerminal.getConductingEquipment();
    }

    /**
     * @return The terminal which is connected to the requested terminal.
     */
    public Terminal toTerminal() {
        return toTerminal;
    }

    /**
     * @return The nominal phases that are connected in the fromTerminal.
     */
    public List<SinglePhaseKind> fromNominalPhases() {
        return nominalPhasePaths.stream().map(NominalPhasePath::from).collect(Collectors.toList());
    }

    /**
     * @return The nominal phases that are connected in the toTerminal.
     */
    public List<SinglePhaseKind> toNominalPhases() {
        return nominalPhasePaths.stream().map(NominalPhasePath::to).collect(Collectors.toList());
    }

    /**
     * @return The mapping of nominal phase paths between the from and to terminals.
     */
    public List<NominalPhasePath> nominalPhasePaths() {
        return nominalPhasePaths;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectivityResult)) return false;
        ConnectivityResult that = (ConnectivityResult) o;
        return fromTerminal.equals(that.fromTerminal) &&
            toTerminal.equals(that.toTerminal) &&
            nominalPhasePaths.equals(that.nominalPhasePaths);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromTerminal, toTerminal, nominalPhasePaths);
    }

    @Override
    public String toString() {
        return "ConnectivityResult{" +
            "fromTerminal=" + fromTerminal.getMRID() +
            ", toTerminal=" + toTerminal.getMRID() +
            ", nominalPhasePaths=" + nominalPhasePaths +
            '}';
    }

    private ConnectivityResult(Terminal fromTerminal, Terminal toTerminal, Collection<NominalPhasePath> nominalPhasePaths) {
        this.fromTerminal = fromTerminal;
        this.toTerminal = toTerminal;
        this.nominalPhasePaths = new ArrayList<>(nominalPhasePaths);

        this.nominalPhasePaths.sort(Comparator.comparing(NominalPhasePath::from).thenComparing(NominalPhasePath::to));
    }

}
