/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.common;

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment;
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject;
import com.zepben.evolve.cim.iec61970.base.wires.AcLineSegment;
import com.zepben.evolve.cim.iec61970.base.wires.Breaker;
import com.zepben.testutils.junit.SystemLogExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class BaseServiceJavaTest {

    @RegisterExtension
    SystemLogExtension systemErr = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess();

    private final BaseService service = new BaseServiceTest.TestBaseService();
    private final Breaker breaker1 = create(service, Breaker::new);
    private final Breaker breaker2 = create(service, Breaker::new);
    private final AcLineSegment acLineSegment1 = create(service, AcLineSegment::new);
    private final AcLineSegment acLineSegment2 = create(service, AcLineSegment::new);

    @BeforeEach
    void beforeEach() {
        breaker2.setName("breaker");
        acLineSegment1.setName("acLineSegment2");
    }

    @Test
    void getJavaInterop() {
        assertThat(service.get(IdentifiedObject.class, breaker1.getMRID()), equalTo(breaker1));
        assertThat(service.get(IdentifiedObject.class, breaker2.getMRID()), equalTo(breaker2));
        assertThat(service.get(IdentifiedObject.class, acLineSegment1.getMRID()), equalTo(acLineSegment1));
        assertThat(service.get(IdentifiedObject.class, acLineSegment2.getMRID()), equalTo(acLineSegment2));

        assertThat(service.get(Breaker.class, breaker2.getMRID()), equalTo(breaker2));
        assertThat(service.get(Breaker.class, breaker1.getMRID()), equalTo(breaker1));
        assertThat(service.get(Breaker.class, acLineSegment1.getMRID()), nullValue());
        assertThat(service.get(Breaker.class, acLineSegment2.getMRID()), nullValue());

        assertThat(service.get(AcLineSegment.class, breaker2.getMRID()), nullValue());
        assertThat(service.get(AcLineSegment.class, breaker1.getMRID()), nullValue());
        assertThat(service.get(AcLineSegment.class, acLineSegment1.getMRID()), equalTo(acLineSegment1));
        assertThat(service.get(AcLineSegment.class, acLineSegment2.getMRID()), equalTo(acLineSegment2));
    }

    @Test
    void countJavaInterop() {
        assertThat(service.num(IdentifiedObject.class), equalTo(4));
        assertThat(service.num(Breaker.class), equalTo(2));
        assertThat(service.num(AcLineSegment.class), equalTo(2));
    }

    @Test
    void forEachJavaInterop() {
        validateForEach(IdentifiedObject.class, Arrays.asList(breaker1, breaker2, acLineSegment1, acLineSegment2));
        validateForEach(Breaker.class, Arrays.asList(breaker1, breaker2));
        validateForEach(AcLineSegment.class, Arrays.asList(acLineSegment1, acLineSegment2));

        validateForEachFiltered(IdentifiedObject.class, it -> it.getName().isEmpty(), Arrays.asList(breaker1, acLineSegment2));
        validateForEachFiltered(Breaker.class, it -> it.getName().isEmpty(), Collections.singletonList(breaker1));
        validateForEachFiltered(AcLineSegment.class, it -> it.getName().isEmpty(), Collections.singletonList(acLineSegment2));
    }

    @Test
    void asStreamJavaInterop() {
        assertThat(service.streamOf(IdentifiedObject.class).collect(toList()), containsInAnyOrder(breaker1, breaker2, acLineSegment1, acLineSegment2));
        assertThat(service.streamOf(Breaker.class).collect(toList()), containsInAnyOrder(breaker1, breaker2));
        assertThat(service.streamOf(AcLineSegment.class).collect(toList()), containsInAnyOrder(acLineSegment1, acLineSegment2));
    }

    @Test
    void toListJavaInterop() {
        assertThat(service.listOf(IdentifiedObject.class), containsInAnyOrder(breaker1, breaker2, acLineSegment1, acLineSegment2));
        assertThat(service.listOf(Breaker.class), containsInAnyOrder(breaker1, breaker2));
        assertThat(service.listOf(AcLineSegment.class), containsInAnyOrder(acLineSegment1, acLineSegment2));

        assertThat(service.listOf(IdentifiedObject.class, it -> it.getName().isEmpty()), containsInAnyOrder(breaker1, acLineSegment2));
        assertThat(service.listOf(Breaker.class, it -> it.getName().isEmpty()), containsInAnyOrder(breaker1));
        assertThat(service.listOf(AcLineSegment.class, it -> it.getName().isEmpty()), containsInAnyOrder(acLineSegment2));
    }

    @Test
    void toSetJavaInterop() {
        assertThat(service.setOf(IdentifiedObject.class), containsInAnyOrder(breaker1, breaker2, acLineSegment1, acLineSegment2));
        assertThat(service.setOf(Breaker.class), containsInAnyOrder(breaker1, breaker2));
        assertThat(service.setOf(AcLineSegment.class), containsInAnyOrder(acLineSegment1, acLineSegment2));

        assertThat(service.setOf(IdentifiedObject.class, it -> it.getName().isEmpty()), containsInAnyOrder(breaker1, acLineSegment2));
        assertThat(service.setOf(Breaker.class, it -> it.getName().isEmpty()), containsInAnyOrder(breaker1));
        assertThat(service.setOf(AcLineSegment.class, it -> it.getName().isEmpty()), containsInAnyOrder(acLineSegment2));
    }

    @Test
    void toMapJavaInterop() {
        assertThat(service.mapOf(IdentifiedObject.class).keySet(), containsInAnyOrder(breaker1.getMRID(), breaker2.getMRID(), acLineSegment1.getMRID(), acLineSegment2.getMRID()));
        assertThat(service.mapOf(Breaker.class).keySet(), containsInAnyOrder(breaker1.getMRID(), breaker2.getMRID()));
        assertThat(service.mapOf(AcLineSegment.class).keySet(), containsInAnyOrder(acLineSegment1.getMRID(), acLineSegment2.getMRID()));
        assertThat(service.mapOf(IdentifiedObject.class).values(), containsInAnyOrder(breaker1, breaker2, acLineSegment1, acLineSegment2));
        assertThat(service.mapOf(Breaker.class).values(), containsInAnyOrder(breaker1, breaker2));
        assertThat(service.mapOf(AcLineSegment.class).values(), containsInAnyOrder(acLineSegment1, acLineSegment2));

        assertThat(service.mapOf(IdentifiedObject.class, it -> it.getName().isEmpty()).keySet(), containsInAnyOrder(breaker1.getMRID(), acLineSegment2.getMRID()));
        assertThat(service.mapOf(Breaker.class, it -> it.getName().isEmpty()).keySet(), containsInAnyOrder(breaker1.getMRID()));
        assertThat(service.mapOf(AcLineSegment.class, it -> it.getName().isEmpty()).keySet(), containsInAnyOrder(acLineSegment2.getMRID()));
        assertThat(service.mapOf(IdentifiedObject.class, it -> it.getName().isEmpty()).values(), containsInAnyOrder(breaker1, acLineSegment2));
        assertThat(service.mapOf(Breaker.class, it -> it.getName().isEmpty()).values(), containsInAnyOrder(breaker1));
        assertThat(service.mapOf(AcLineSegment.class, it -> it.getName().isEmpty()).values(), containsInAnyOrder(acLineSegment2));
    }

    private <T extends IdentifiedObject> T create(BaseService baseService, Supplier<T> supplier) {
        T it = supplier.get();
        baseService.add(it);
        return it;
    }

    private <T extends IdentifiedObject> void validateForEach(Class<T> clazz, List<ConductingEquipment> expected) {
        List<IdentifiedObject> visited = new ArrayList<>();
        service.streamOf(clazz).forEach(visited::add);
        assertThat(visited, containsInAnyOrder(expected.toArray()));
    }

    private <T extends IdentifiedObject> void validateForEachFiltered(Class<T> clazz, Predicate<T> filter, List<ConductingEquipment> expected) {
        List<IdentifiedObject> visited = new ArrayList<>();
        service.streamOf(clazz).filter(filter).forEach(visited::add);
        assertThat(visited, containsInAnyOrder(expected.toArray()));
    }

}
