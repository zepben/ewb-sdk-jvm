/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.testdata

import com.zepben.cimbend.cim.iec61968.assets.Pole
import com.zepben.cimbend.cim.iec61968.assets.Streetlight
import com.zepben.cimbend.cim.iec61970.base.wires.PowerTransformer
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.cimbend.common.meta.DataSource
import com.zepben.cimbend.common.meta.MetadataCollection
import com.zepben.cimbend.customer.CustomerService
import com.zepben.cimbend.diagram.DiagramService
import com.zepben.cimbend.measurement.MeasurementService
import com.zepben.cimbend.network.NetworkModelTestUtil
import com.zepben.cimbend.network.NetworkService
import com.zepben.cimbend.network.fillFields
import java.time.Instant

@Suppress("SameParameterValue", "BooleanLiteralArgument")
object SchemaTestNetwork {

    fun createPoleTestServices(): NetworkModelTestUtil.Services {
        val networkService = NetworkService()

        networkService.add(Pole("pole1"))
        networkService.add(Pole("pole2").fillFields(networkService))

        return NetworkModelTestUtil.Services(MetadataCollection(), networkService, DiagramService(), CustomerService(), MeasurementService())
    }

    fun createPowerTransformerTestServices(): NetworkModelTestUtil.Services {
        val networkService = NetworkService()

        networkService.add(PowerTransformer())
        networkService.add(PowerTransformer().fillFields(networkService))

        return NetworkModelTestUtil.Services(MetadataCollection(), networkService, DiagramService(), CustomerService(), MeasurementService())
    }

    fun createStreetlightTestServices(): NetworkModelTestUtil.Services {
        val networkService = NetworkService()

        networkService.add(Streetlight("streetlight1"))
        networkService.add(Streetlight("streetlight2").fillFields(networkService))

        return NetworkModelTestUtil.Services(MetadataCollection(), networkService, DiagramService(), CustomerService(), MeasurementService())
    }

    fun createCircuitTestServices(): NetworkModelTestUtil.Services {
        val networkService = NetworkService()

        networkService.add(Circuit("circuit1"))
        networkService.add(Circuit("circuit2").fillFields(networkService))

        return NetworkModelTestUtil.Services(MetadataCollection(), networkService, DiagramService(), CustomerService(), MeasurementService())
    }

    fun createLoopTestServices(): NetworkModelTestUtil.Services {
        val networkService = NetworkService()

        networkService.add(Loop("loop1"))
        networkService.add(Loop("loop2").fillFields(networkService))

        return NetworkModelTestUtil.Services(MetadataCollection(), networkService, DiagramService(), CustomerService(), MeasurementService())
    }

    fun createDataSourceTestServices(): NetworkModelTestUtil.Services {
        val metadataCollection = MetadataCollection()

        metadataCollection.add(DataSource("source1", "v1", Instant.EPOCH))
        metadataCollection.add(DataSource("source2", "v2", Instant.now()))

        return NetworkModelTestUtil.Services(metadataCollection, NetworkService(), DiagramService(), CustomerService(), MeasurementService())
    }

}
