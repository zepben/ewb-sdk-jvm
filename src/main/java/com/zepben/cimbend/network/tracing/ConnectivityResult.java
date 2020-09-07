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
package com.zepben.cimbend.network.tracing;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment;
import com.zepben.cimbend.cim.iec61970.base.core.Terminal;
import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind;
import com.zepben.cimbend.network.model.NominalPhasePath;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Stores the connectivity between two terminals, including the mapping between the nominal phases.
 */
@EverythingIsNonnullByDefault
@SuppressWarnings("WeakerAccess")
public class ConnectivityResult {

    private final Terminal fromTerminal;
    private final Terminal toTerminal;
    private final List<SinglePhaseKind> fromNominalPhases = new ArrayList<>();
    private final List<SinglePhaseKind> toNominalPhases = new ArrayList<>();
    @Nullable private List<NominalPhasePath> nominalPhasePaths;
    private boolean isSorted = false;

    /**
     * @param fromTerminal The terminal for which the connectivity was requested.
     * @param toTerminal   The terminal which is connected to the requested terminal.
     * @return The ConnectivityResult Builder to use to construct the result.
     */
    public static ConnectivityResult between(Terminal fromTerminal, Terminal toTerminal) {
        return new ConnectivityResult(fromTerminal, toTerminal);
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
        return fromNominalPhases;
    }

    /**
     * @return The nominal phases that are connected in the toTerminal.
     */
    public List<SinglePhaseKind> toNominalPhases() {
        return toNominalPhases;
    }

    /**
     * @return The mapping of nominal phase paths between the from and to terminals.
     */
    public List<NominalPhasePath> nominalPhasePaths() {
        if (nominalPhasePaths == null)
            populateNominalPhasePaths();

        return nominalPhasePaths;
    }

    /**
     * @param from The nominal phase on the fromTerminal.
     * @param to   The connected nominal phase on the toTerminal.
     * @return a reference to this class to allow for fluent usage.
     */
    public ConnectivityResult addNominalPhasePath(SinglePhaseKind from, SinglePhaseKind to) {
        if (nominalPhasePaths != null)
            throw new IllegalStateException("You can not add paths after the result has been used.");

        fromNominalPhases.add(from);
        toNominalPhases.add(to);

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectivityResult)) return false;
        ConnectivityResult that = (ConnectivityResult) o;
        return fromTerminal.equals(that.fromTerminal) &&
            toTerminal.equals(that.toTerminal) &&
            sortedNominalPhasePaths().equals(that.sortedNominalPhasePaths());
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromTerminal, toTerminal, sortedNominalPhasePaths());
    }

    @Override
    public String toString() {
        return "ConnectivityResult{" +
            "fromTerminal=" + fromTerminal.getMRID() +
            ", toTerminal=" + toTerminal.getMRID() +
            ", nominalPhasePaths=" + sortedNominalPhasePaths() +
            '}';
    }

    private List<NominalPhasePath> sortedNominalPhasePaths() {
        if (nominalPhasePaths == null)
            populateNominalPhasePaths();

        if (!isSorted) {
            nominalPhasePaths.sort(Comparator.comparing(NominalPhasePath::from).thenComparing(NominalPhasePath::to));
            isSorted = true;
        }

        return nominalPhasePaths;
    }

    private void populateNominalPhasePaths() {
        nominalPhasePaths = new ArrayList<>(fromNominalPhases.size());

        Iterator<SinglePhaseKind> fromIterator = fromNominalPhases.iterator();
        Iterator<SinglePhaseKind> toIterator = toNominalPhases.iterator();
        while (fromIterator.hasNext())
            nominalPhasePaths.add(NominalPhasePath.between(fromIterator.next(), toIterator.next()));
    }

    private ConnectivityResult(Terminal fromTerminal, Terminal toTerminal) {
        this.fromTerminal = fromTerminal;
        this.toTerminal = toTerminal;
    }

}
