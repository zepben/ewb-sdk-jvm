/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsUnit
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.testutils.exception.ExpectException
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PowerElectronicsConnectionTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(PowerElectronicsConnection().mRID, not(equalTo("")))
        assertThat(PowerElectronicsConnection("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val powerElectronicsConnection = PowerElectronicsConnection()

        assertThat(powerElectronicsConnection.maxIFault, nullValue())
        assertThat(powerElectronicsConnection.maxQ, nullValue())
        assertThat(powerElectronicsConnection.minQ, nullValue())
        assertThat(powerElectronicsConnection.p, nullValue())
        assertThat(powerElectronicsConnection.q, nullValue())
        assertThat(powerElectronicsConnection.ratedS, nullValue())
        assertThat(powerElectronicsConnection.ratedU, nullValue())
        assertThat(powerElectronicsConnection.inverterStandard, nullValue())
        assertThat(powerElectronicsConnection.sustainOpOvervoltLimit, nullValue())
        assertThat(powerElectronicsConnection.stopAtOverFreq, nullValue())
        assertThat(powerElectronicsConnection.stopAtUnderFreq, nullValue())
        assertThat(powerElectronicsConnection.invVoltWattRespMode, nullValue())
        assertThat(powerElectronicsConnection.invWattRespV1, nullValue())
        assertThat(powerElectronicsConnection.invWattRespV2, nullValue())
        assertThat(powerElectronicsConnection.invWattRespV3, nullValue())
        assertThat(powerElectronicsConnection.invWattRespV4, nullValue())
        assertThat(powerElectronicsConnection.invWattRespPAtV1, nullValue())
        assertThat(powerElectronicsConnection.invWattRespPAtV2, nullValue())
        assertThat(powerElectronicsConnection.invWattRespPAtV3, nullValue())
        assertThat(powerElectronicsConnection.invWattRespPAtV4, nullValue())
        assertThat(powerElectronicsConnection.invVoltVArRespMode, nullValue())
        assertThat(powerElectronicsConnection.invVArRespV1, nullValue())
        assertThat(powerElectronicsConnection.invVArRespV2, nullValue())
        assertThat(powerElectronicsConnection.invVArRespV3, nullValue())
        assertThat(powerElectronicsConnection.invVArRespV4, nullValue())
        assertThat(powerElectronicsConnection.invVArRespQAtV1, nullValue())
        assertThat(powerElectronicsConnection.invVArRespQAtV2, nullValue())
        assertThat(powerElectronicsConnection.invVArRespQAtV3, nullValue())
        assertThat(powerElectronicsConnection.invVArRespQAtV4, nullValue())
        assertThat(powerElectronicsConnection.invReactivePowerMode, nullValue())
        assertThat(powerElectronicsConnection.invFixReactivePower, nullValue())

        powerElectronicsConnection.fillFields(NetworkService())

        assertThat(powerElectronicsConnection.maxIFault, equalTo(1))
        assertThat(powerElectronicsConnection.maxQ, equalTo(2.0))
        assertThat(powerElectronicsConnection.minQ, equalTo(3.0))
        assertThat(powerElectronicsConnection.p, equalTo(4.0))
        assertThat(powerElectronicsConnection.q, equalTo(5.0))
        assertThat(powerElectronicsConnection.ratedS, equalTo(6))
        assertThat(powerElectronicsConnection.ratedU, equalTo(7))
        assertThat(powerElectronicsConnection.inverterStandard, equalTo("TEST"))
        assertThat(powerElectronicsConnection.sustainOpOvervoltLimit, equalTo(8))
        assertThat(powerElectronicsConnection.stopAtOverFreq, equalTo(10.0f))
        assertThat(powerElectronicsConnection.stopAtUnderFreq, equalTo(5.0f))
        assertThat(powerElectronicsConnection.invVoltWattRespMode, equalTo(false))
        assertThat(powerElectronicsConnection.invWattRespV1, equalTo(200))
        assertThat(powerElectronicsConnection.invWattRespV2, equalTo(216))
        assertThat(powerElectronicsConnection.invWattRespV3, equalTo(235))
        assertThat(powerElectronicsConnection.invWattRespV4, equalTo(244))
        assertThat(powerElectronicsConnection.invWattRespPAtV1, equalTo(0.1f))
        assertThat(powerElectronicsConnection.invWattRespPAtV2, equalTo(0.2f))
        assertThat(powerElectronicsConnection.invWattRespPAtV3, equalTo(0.3f))
        assertThat(powerElectronicsConnection.invWattRespPAtV4, equalTo(0.1f))
        assertThat(powerElectronicsConnection.invVoltVArRespMode, equalTo(false))
        assertThat(powerElectronicsConnection.invVArRespV1, equalTo(200))
        assertThat(powerElectronicsConnection.invVArRespV2, equalTo(200))
        assertThat(powerElectronicsConnection.invVArRespV3, equalTo(300))
        assertThat(powerElectronicsConnection.invVArRespV4, equalTo(300))
        assertThat(powerElectronicsConnection.invVArRespQAtV1, equalTo(0.6f))
        assertThat(powerElectronicsConnection.invVArRespQAtV2, equalTo(-1.0f))
        assertThat(powerElectronicsConnection.invVArRespQAtV3, equalTo(1.0f))
        assertThat(powerElectronicsConnection.invVArRespQAtV4, equalTo(-0.6f))
        assertThat(powerElectronicsConnection.invReactivePowerMode, equalTo(false))
        assertThat(powerElectronicsConnection.invFixReactivePower, equalTo(-1.0f))

    }

    @Test
    internal fun `test bounds on properties`() {
        val powerElectronicsConnection = PowerElectronicsConnection()

        expect { powerElectronicsConnection.invWattRespV1 = 199 }
            .toThrow<IllegalStateException>()
            .withMessage("invWattRespV1 [199] must be between 200 and 300.")
        expect { powerElectronicsConnection.invWattRespV1 = 301 }
            .toThrow<IllegalStateException>()
            .withMessage("invWattRespV1 [301] must be between 200 and 300.")
        expect { powerElectronicsConnection.invWattRespV2 = 215 }
            .toThrow<IllegalStateException>()
            .withMessage("invWattRespV2 [215] must be between 216 and 230.")
        expect { powerElectronicsConnection.invWattRespV2 = 231 }
            .toThrow<IllegalStateException>()
            .withMessage("invWattRespV2 [231] must be between 216 and 230.")
        expect { powerElectronicsConnection.invWattRespV3 = 234 }
            .toThrow<IllegalStateException>()
            .withMessage("invWattRespV3 [234] must be between 235 and 255.")
        expect { powerElectronicsConnection.invWattRespV3 = 256 }
            .toThrow<IllegalStateException>()
            .withMessage("invWattRespV3 [256] must be between 235 and 255.")
        expect { powerElectronicsConnection.invWattRespV4 = 243 }
            .toThrow<IllegalStateException>()
            .withMessage("invWattRespV4 [243] must be between 244 and 265.")
        expect { powerElectronicsConnection.invWattRespV4 = 266 }
            .toThrow<IllegalStateException>()
            .withMessage("invWattRespV4 [266] must be between 244 and 265.")

        expect { powerElectronicsConnection.invWattRespPAtV1 = -0.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invWattRespPAtV1 [-0.1] must be between 0.0 and 1.0.")
        expect { powerElectronicsConnection.invWattRespPAtV1 = 1.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invWattRespPAtV1 [1.1] must be between 0.0 and 1.0.")
        expect { powerElectronicsConnection.invWattRespPAtV2 = -0.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invWattRespPAtV2 [-0.1] must be between 0.0 and 1.0.")
        expect { powerElectronicsConnection.invWattRespPAtV2 = 1.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invWattRespPAtV2 [1.1] must be between 0.0 and 1.0.")
        expect { powerElectronicsConnection.invWattRespPAtV3 = -0.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invWattRespPAtV3 [-0.1] must be between 0.0 and 1.0.")
        expect { powerElectronicsConnection.invWattRespPAtV3 = 1.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invWattRespPAtV3 [1.1] must be between 0.0 and 1.0.")
        expect { powerElectronicsConnection.invWattRespPAtV4 = -0.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invWattRespPAtV4 [-0.1] must be between 0.0 and 0.2.")
        expect { powerElectronicsConnection.invWattRespPAtV4 = 0.3f }
            .toThrow<IllegalStateException>()
            .withMessage("invWattRespPAtV4 [0.3] must be between 0.0 and 0.2.")

        expect { powerElectronicsConnection.invVArRespV1 = 199 }
            .toThrow<IllegalStateException>()
            .withMessage("invVArRespV1 [199] must be between 200 and 300.")
        expect { powerElectronicsConnection.invVArRespV1 = 301 }
            .toThrow<IllegalStateException>()
            .withMessage("invVArRespV1 [301] must be between 200 and 300.")
        expect { powerElectronicsConnection.invVArRespV2 = 199 }
            .toThrow<IllegalStateException>()
            .withMessage("invVArRespV2 [199] must be between 200 and 300.")
        expect { powerElectronicsConnection.invVArRespV2 = 301 }
            .toThrow<IllegalStateException>()
            .withMessage("invVArRespV2 [301] must be between 200 and 300.")
        expect { powerElectronicsConnection.invVArRespV3 = 199 }
            .toThrow<IllegalStateException>()
            .withMessage("invVArRespV3 [199] must be between 200 and 300.")
        expect { powerElectronicsConnection.invVArRespV3 = 301 }
            .toThrow<IllegalStateException>()
            .withMessage("invVArRespV3 [301] must be between 200 and 300.")
        expect { powerElectronicsConnection.invVArRespV4 = 199 }
            .toThrow<IllegalStateException>()
            .withMessage("invVArRespV4 [199] must be between 200 and 300.")
        expect { powerElectronicsConnection.invVArRespV4 = 301 }
            .toThrow<IllegalStateException>()
            .withMessage("invVArRespV4 [301] must be between 200 and 300.")

        expect { powerElectronicsConnection.invVArRespQAtV1 = -0.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invVArRespQAtV1 [-0.1] must be between 0.0 and 0.6.")
        expect { powerElectronicsConnection.invVArRespQAtV1 = 0.7f }
            .toThrow<IllegalStateException>()
            .withMessage("invVArRespQAtV1 [0.7] must be between 0.0 and 0.6.")
        expect { powerElectronicsConnection.invVArRespQAtV2 = -1.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invVArRespQAtV2 [-1.1] must be between -1.0 and 1.0.")
        expect { powerElectronicsConnection.invVArRespQAtV2 = 1.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invVArRespQAtV2 [1.1] must be between -1.0 and 1.0.")
        expect { powerElectronicsConnection.invVArRespQAtV3 = -1.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invVArRespQAtV3 [-1.1] must be between -1.0 and 1.0.")
        expect { powerElectronicsConnection.invVArRespQAtV3 = 1.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invVArRespQAtV3 [1.1] must be between -1.0 and 1.0.")
        expect { powerElectronicsConnection.invVArRespQAtV4 = -0.7f }
            .toThrow<IllegalStateException>()
            .withMessage("invVArRespQAtV4 [-0.7] must be between -0.6 and 0.0.")
        expect { powerElectronicsConnection.invVArRespQAtV4 = 0.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invVArRespQAtV4 [0.1] must be between -0.6 and 0.0.")

    }

    @Test
    internal fun assignsPowerElectronicsConnectionToPowerElectronicsConnectionPhaseIfMissing() {
        val powerElectronicsConnection = PowerElectronicsConnection()
        val phase = PowerElectronicsConnectionPhase()

        powerElectronicsConnection.addPhase(phase)
        assertThat(phase.powerElectronicsConnection, equalTo(powerElectronicsConnection))
    }

    @Test
    internal fun rejectsPowerElectronicsConnectionPhaseWithWrongPowerElectronicsConnection() {
        val powerElectronicsConnection1 = PowerElectronicsConnection()
        val powerElectronicsConnection2 = PowerElectronicsConnection()
        val phase = PowerElectronicsConnectionPhase().apply { powerElectronicsConnection = powerElectronicsConnection2 }

        ExpectException.expect { powerElectronicsConnection1.addPhase(phase) }
            .toThrow<IllegalArgumentException>()
            .withMessage("${phase.typeNameAndMRID()} `powerElectronicsConnection` property references ${powerElectronicsConnection2.typeNameAndMRID()}, expected ${powerElectronicsConnection1.typeNameAndMRID()}.")
    }

    @Test
    internal fun powerElectronicsConnectionUnits() {
        PrivateCollectionValidator.validate(
            { PowerElectronicsConnection() },
            { id, _ -> object : PowerElectronicsUnit(id) {} },
            PowerElectronicsConnection::numUnits,
            PowerElectronicsConnection::getUnit,
            PowerElectronicsConnection::units,
            PowerElectronicsConnection::addUnit,
            PowerElectronicsConnection::removeUnit,
            PowerElectronicsConnection::clearUnits
        )
    }

    @Test
    internal fun powerElectronicsConnectionPhases() {
        PrivateCollectionValidator.validate(
            { PowerElectronicsConnection() },
            { id, _ -> PowerElectronicsConnectionPhase(id) },
            PowerElectronicsConnection::numPhases,
            PowerElectronicsConnection::getPhase,
            PowerElectronicsConnection::phases,
            PowerElectronicsConnection::addPhase,
            PowerElectronicsConnection::removePhase,
            PowerElectronicsConnection::clearPhases
        )
    }
}
