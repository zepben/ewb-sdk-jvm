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
import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject;
import com.zepben.traversals.BasicTraversal;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Convenience class that provides methods for finding conducting equipment with attached usage points.
 * This class is backed by a {@link BasicTraversal}.
 */
@SuppressWarnings("WeakerAccess")
@EverythingIsNonnullByDefault
public class FindWithUsagePoints {

    public Result runNormal(ConductingEquipment from, @Nullable ConductingEquipment to) {
        return runNormal(Collections.singletonList(from), Collections.singletonList(to)).get(0);
    }

    public List<Result> runNormal(List<ConductingEquipment> froms, List<ConductingEquipment> tos) {
        return run(PhaseTrace::newNormalDownstreamTrace, froms, tos);
    }

    public Result runCurrent(ConductingEquipment from, @Nullable ConductingEquipment to) {
        return runCurrent(Collections.singletonList(from), Collections.singletonList(to)).get(0);
    }

    public List<Result> runCurrent(List<ConductingEquipment> froms, List<ConductingEquipment> tos) {
        return run(PhaseTrace::newCurrentDownstreamTrace, froms, tos);
    }

    private List<Result> run(Supplier<BasicTraversal<PhaseStep>> traversalSupplier, List<ConductingEquipment> froms, List<ConductingEquipment> tos) {
        if (froms.size() != tos.size())
            return Collections.nCopies(Math.max(froms.size(), tos.size()), result().withStatus(Result.Status.MISMATCHED_FROM_TO));

        List<Result> results = new ArrayList<>();
        for (int index = 0; index < froms.size(); ++index) {
            ConductingEquipment from = froms.get(index);
            @Nullable ConductingEquipment to = tos.get(index);

            if ((to != null) && from.getMRID().equals(to.getMRID())) {
                Map<String, ConductingEquipment> withUsagePoints = new HashMap<>();
                if (from.numUsagePoints() != 0)
                    withUsagePoints.put(from.getMRID(), from);
                results.add(result().withConductingEquipment(withUsagePoints));
            } else
                results.add(runTrace(traversalSupplier, from, to));
        }

        return results;
    }

    private Result runTrace(Supplier<BasicTraversal<PhaseStep>> traversalSupplier, ConductingEquipment from, @Nullable ConductingEquipment to) {
        if (from.numTerminals() == 0) {
            if (to != null)
                return result().withStatus(Result.Status.NO_PATH);
            else if (from.numUsagePoints() != 0)
                return result().withConductingEquipment(Collections.singletonMap(from.getMRID(), from));
            else
                return result().withStatus(Result.Status.NO_ERROR);
        }

        Set<String> extentIds = Stream.of(from, to).filter(Objects::nonNull).map(IdentifiedObject::getMRID).collect(Collectors.toSet());
        AtomicBoolean pathFound = new AtomicBoolean(to == null);
        Map<String, ConductingEquipment> withUsagePoints = new HashMap<>();

        BasicTraversal<PhaseStep> traversal = traversalSupplier.get();
        traversal.addStopCondition(ce2c -> extentIds.contains(ce2c.conductingEquipment().getMRID()));
        traversal.addStepAction((ce2c, isStopping) -> {
            if (isStopping)
                pathFound.set(true);

            if (ce2c.conductingEquipment().numUsagePoints() != 0)
                withUsagePoints.put(ce2c.conductingEquipment().getMRID(), ce2c.conductingEquipment());
        });

        traversal.reset().run(PhaseStep.startAt(from, Objects.requireNonNull(from.getTerminal(1)).getPhases()), false);

        if ((to != null) && !pathFound.get()) {
            if (to.numTerminals() == 0)
                return result().withStatus(Result.Status.NO_PATH);

            withUsagePoints.clear();
            traversal.reset().run(PhaseStep.startAt(to, Objects.requireNonNull(to.getTerminal(1)).getPhases()), false);
        }

        if (pathFound.get())
            return result().withConductingEquipment(withUsagePoints);
        else
            return result().withStatus(Result.Status.NO_PATH);
    }

    private Result result() {
        return new Result();
    }

    public static class Result {

        public enum Status {
            NO_ERROR, NO_PATH, MISMATCHED_FROM_TO
        }

        private Status status = Status.NO_ERROR;
        private Map<String, ConductingEquipment> conductingEquipment = Collections.emptyMap();

        public Status status() {
            return status;
        }

        public Map<String, ConductingEquipment> conductingEquipment() {
            return Collections.unmodifiableMap(conductingEquipment);
        }

        private Result withStatus(Status status) {
            this.status = status;
            return this;
        }

        private Result withConductingEquipment(Map<String, ConductingEquipment> conductingEquipment) {
            this.conductingEquipment = conductingEquipment;
            return this;
        }

    }

}
