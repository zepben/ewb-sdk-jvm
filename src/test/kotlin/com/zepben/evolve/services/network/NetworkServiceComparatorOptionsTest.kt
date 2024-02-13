package com.zepben.evolve.services.network

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class NetworkServiceComparatorOptionsTest {

    @Test
    internal fun all() {
        val options = NetworkServiceComparatorOptions.all()
        assertThat(options.compareTracedPhases, equalTo(true))
        assertThat(options.compareTerminals, equalTo(true))
        assertThat(options.compareFeederEquipment, equalTo(true))
        assertThat(options.compareEquipmentContainers, equalTo(true))
        assertThat(options.compareLvSimplification, equalTo(true))
    }

    @Test
    internal fun none() {
        val options = NetworkServiceComparatorOptions.none()
        assertThat(options.compareTracedPhases, equalTo(false))
        assertThat(options.compareTerminals, equalTo(false))
        assertThat(options.compareFeederEquipment, equalTo(false))
        assertThat(options.compareEquipmentContainers, equalTo(false))
        assertThat(options.compareLvSimplification, equalTo(false))
    }

    @Test
    internal fun builderAll() {
        val options = NetworkServiceComparatorOptions.of()
            .comparePhases()
            .compareTerminals()
            .compareFeederEquipment()
            .compareEquipmentContainers()
            .compareLvSimplification()
            .build()

        assertThat(options.compareTracedPhases, equalTo(true))
        assertThat(options.compareTerminals, equalTo(true))
        assertThat(options.compareFeederEquipment, equalTo(true))
        assertThat(options.compareEquipmentContainers, equalTo(true))
        assertThat(options.compareLvSimplification, equalTo(true))
    }

    @Test
    internal fun builderNone() {
        val options = NetworkServiceComparatorOptions.of().build()

        assertThat(options.compareTracedPhases, equalTo(false))
        assertThat(options.compareTerminals, equalTo(false))
        assertThat(options.compareFeederEquipment, equalTo(false))
        assertThat(options.compareEquipmentContainers, equalTo(false))
        assertThat(options.compareLvSimplification, equalTo(false))
    }
}
