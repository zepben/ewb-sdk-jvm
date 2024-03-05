/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class TapChangerTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : TapChanger() {}.mRID, not(equalTo("")))
        assertThat(object : TapChanger("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val tapChanger = object : TapChanger() {}

        assertThat(tapChanger.controlEnabled, equalTo(true))
        assertThat(tapChanger.highStep, nullValue())
        assertThat(tapChanger.lowStep, nullValue())
        assertThat(tapChanger.neutralStep, nullValue())
        assertThat(tapChanger.neutralU, nullValue())
        assertThat(tapChanger.normalStep, nullValue())
        assertThat(tapChanger.step, nullValue())
        assertThat(tapChanger.tapChangerControl, nullValue())

        tapChanger.fillFields(NetworkService())

        assertThat(tapChanger.controlEnabled, equalTo(false))
        assertThat(tapChanger.highStep, equalTo(22))
        assertThat(tapChanger.lowStep, equalTo(1))
        assertThat(tapChanger.neutralStep, equalTo(2))
        assertThat(tapChanger.neutralU, equalTo(3))
        assertThat(tapChanger.normalStep, equalTo(4))
        assertThat(tapChanger.step, equalTo(5.5))
        assertThat(tapChanger.tapChangerControl, notNullValue())
    }

    @Test
    internal fun validateSteps() {
        val tapChanger = object : TapChanger() {}.apply {
            highStep = 3
            lowStep = -3
            neutralStep = 1
            neutralU = 1
            normalStep = 2
            step = -1.0
        }

        expect { tapChanger.highStep = -4 }
            .toThrow<IllegalStateException>()
            .withMessage("high step [-4] must be greater than low step [-3].")

        expect { tapChanger.lowStep = 6 }
            .toThrow<IllegalStateException>()
            .withMessage("low step [6] must be lower than high step [3].")

        expect { tapChanger.neutralStep = -4 }
            .toThrow<IllegalStateException>()
            .withMessage("neutral step [-4] must be between high step [3] and low step [-3].")

        expect { tapChanger.neutralStep = 4 }
            .toThrow<IllegalStateException>()
            .withMessage("neutral step [4] must be between high step [3] and low step [-3].")

        expect { tapChanger.normalStep = -5 }
            .toThrow<IllegalStateException>()
            .withMessage("normal step [-5] must be between high step [3] and low step [-3].")

        expect { tapChanger.normalStep = 5 }
            .toThrow<IllegalStateException>()
            .withMessage("normal step [5] must be between high step [3] and low step [-3].")

        expect { tapChanger.step = -4.0 }
            .toThrow<IllegalStateException>()
            .withMessage("step [-4.0] must be between high step [3] and low step [-3].")

        expect { tapChanger.step = 4.0 }
            .toThrow<IllegalStateException>()
            .withMessage("step [4.0] must be between high step [3] and low step [-3].")
    }
}
