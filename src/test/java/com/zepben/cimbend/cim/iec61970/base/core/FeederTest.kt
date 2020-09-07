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
package com.zepben.cimbend.cim.iec61970.base.core

import com.zepben.cimbend.utils.PrivateCollectionValidator
import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class FeederTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(Feeder().mRID, not(equalTo("")))
        assertThat(Feeder("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val feeder = Feeder()
        val terminal = Terminal()
        val substation = Substation()

        assertThat(feeder.normalHeadTerminal, nullValue())
        assertThat(feeder.normalEnergizingSubstation, nullValue())

        feeder.apply {
            normalHeadTerminal = terminal
            normalEnergizingSubstation = substation
        }

        assertThat(feeder.normalHeadTerminal, equalTo(terminal))
        assertThat(feeder.normalEnergizingSubstation, equalTo(substation))
    }

    @Test
    internal fun currentEquipment() {
        PrivateCollectionValidator.validate(
            { Feeder() },
            { id, _ -> object : Equipment(id) {} },
            Feeder::numCurrentEquipment,
            Feeder::getCurrentEquipment,
            Feeder::currentEquipment,
            Feeder::addCurrentEquipment,
            Feeder::removeCurrentEquipment,
            Feeder::clearCurrentEquipment
        )
    }
}
