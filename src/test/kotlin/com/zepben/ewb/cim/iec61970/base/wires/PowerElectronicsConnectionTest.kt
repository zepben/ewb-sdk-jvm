/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.iec61970.base.generation.production.PowerElectronicsUnit
import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.ewb.utils.PrivateCollectionValidator
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
        assertThat(powerElectronicsConnection.invVoltVarRespMode, nullValue())
        assertThat(powerElectronicsConnection.invVarRespV1, nullValue())
        assertThat(powerElectronicsConnection.invVarRespV2, nullValue())
        assertThat(powerElectronicsConnection.invVarRespV3, nullValue())
        assertThat(powerElectronicsConnection.invVarRespV4, nullValue())
        assertThat(powerElectronicsConnection.invVarRespQAtV1, nullValue())
        assertThat(powerElectronicsConnection.invVarRespQAtV2, nullValue())
        assertThat(powerElectronicsConnection.invVarRespQAtV3, nullValue())
        assertThat(powerElectronicsConnection.invVarRespQAtV4, nullValue())
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
        assertThat(powerElectronicsConnection.invVoltVarRespMode, equalTo(false))
        assertThat(powerElectronicsConnection.invVarRespV1, equalTo(200))
        assertThat(powerElectronicsConnection.invVarRespV2, equalTo(200))
        assertThat(powerElectronicsConnection.invVarRespV3, equalTo(300))
        assertThat(powerElectronicsConnection.invVarRespV4, equalTo(300))
        assertThat(powerElectronicsConnection.invVarRespQAtV1, equalTo(0.6f))
        assertThat(powerElectronicsConnection.invVarRespQAtV2, equalTo(-1.0f))
        assertThat(powerElectronicsConnection.invVarRespQAtV3, equalTo(1.0f))
        assertThat(powerElectronicsConnection.invVarRespQAtV4, equalTo(-0.6f))
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

        expect { powerElectronicsConnection.invVarRespV1 = 199 }
            .toThrow<IllegalStateException>()
            .withMessage("invVarRespV1 [199] must be between 200 and 300.")
        expect { powerElectronicsConnection.invVarRespV1 = 301 }
            .toThrow<IllegalStateException>()
            .withMessage("invVarRespV1 [301] must be between 200 and 300.")
        expect { powerElectronicsConnection.invVarRespV2 = 199 }
            .toThrow<IllegalStateException>()
            .withMessage("invVarRespV2 [199] must be between 200 and 300.")
        expect { powerElectronicsConnection.invVarRespV2 = 301 }
            .toThrow<IllegalStateException>()
            .withMessage("invVarRespV2 [301] must be between 200 and 300.")
        expect { powerElectronicsConnection.invVarRespV3 = 199 }
            .toThrow<IllegalStateException>()
            .withMessage("invVarRespV3 [199] must be between 200 and 300.")
        expect { powerElectronicsConnection.invVarRespV3 = 301 }
            .toThrow<IllegalStateException>()
            .withMessage("invVarRespV3 [301] must be between 200 and 300.")
        expect { powerElectronicsConnection.invVarRespV4 = 199 }
            .toThrow<IllegalStateException>()
            .withMessage("invVarRespV4 [199] must be between 200 and 300.")
        expect { powerElectronicsConnection.invVarRespV4 = 301 }
            .toThrow<IllegalStateException>()
            .withMessage("invVarRespV4 [301] must be between 200 and 300.")

        expect { powerElectronicsConnection.invVarRespQAtV1 = -0.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invVarRespQAtV1 [-0.1] must be between 0.0 and 0.6.")
        expect { powerElectronicsConnection.invVarRespQAtV1 = 0.7f }
            .toThrow<IllegalStateException>()
            .withMessage("invVarRespQAtV1 [0.7] must be between 0.0 and 0.6.")
        expect { powerElectronicsConnection.invVarRespQAtV2 = -1.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invVarRespQAtV2 [-1.1] must be between -1.0 and 1.0.")
        expect { powerElectronicsConnection.invVarRespQAtV2 = 1.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invVarRespQAtV2 [1.1] must be between -1.0 and 1.0.")
        expect { powerElectronicsConnection.invVarRespQAtV3 = -1.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invVarRespQAtV3 [-1.1] must be between -1.0 and 1.0.")
        expect { powerElectronicsConnection.invVarRespQAtV3 = 1.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invVarRespQAtV3 [1.1] must be between -1.0 and 1.0.")
        expect { powerElectronicsConnection.invVarRespQAtV4 = -0.7f }
            .toThrow<IllegalStateException>()
            .withMessage("invVarRespQAtV4 [-0.7] must be between -0.6 and 0.0.")
        expect { powerElectronicsConnection.invVarRespQAtV4 = 0.1f }
            .toThrow<IllegalStateException>()
            .withMessage("invVarRespQAtV4 [0.1] must be between -0.6 and 0.0.")

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

        expect { powerElectronicsConnection1.addPhase(phase) }
            .toThrow<IllegalArgumentException>()
            .withMessage("${phase.typeNameAndMRID()} `powerElectronicsConnection` property references ${powerElectronicsConnection2.typeNameAndMRID()}, expected ${powerElectronicsConnection1.typeNameAndMRID()}.")
    }

    @Test
    internal fun powerElectronicsConnectionUnits() {
        PrivateCollectionValidator.validateUnordered(
            ::PowerElectronicsConnection,
            { id -> object : PowerElectronicsUnit(id) {} },
            PowerElectronicsConnection::units,
            PowerElectronicsConnection::numUnits,
            PowerElectronicsConnection::getUnit,
            PowerElectronicsConnection::addUnit,
            PowerElectronicsConnection::removeUnit,
            PowerElectronicsConnection::clearUnits
        )
    }

    @Test
    internal fun powerElectronicsConnectionPhases() {
        PrivateCollectionValidator.validateUnordered(
            ::PowerElectronicsConnection,
            ::PowerElectronicsConnectionPhase,
            PowerElectronicsConnection::phases,
            PowerElectronicsConnection::numPhases,
            PowerElectronicsConnection::getPhase,
            PowerElectronicsConnection::addPhase,
            PowerElectronicsConnection::removePhase,
            PowerElectronicsConnection::clearPhases
        )
    }
}
