/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming

import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.streaming.get.NetworkConsumerClient
import com.zepben.ewb.streaming.get.getEquipmentContainer
import com.zepben.ewb.streaming.grpc.Connect.connectInsecure
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("main")
private var indentLevel = 0
private var indent = 3

fun main() {
    connectInsecure(host = "ewb.local", rpcPort = 9001).use { channel ->
        NetworkConsumerClient(channel).use { client ->
            time("streaming") {
                time("get feeder") { runFeeder(client) }
                time("retrieve network hierarchy") { runNetworkHierarchy(client) }
                time("retrieve network") { runRetrieve(client) }
            }
        }
    }
    log("done")
}

private fun runRetrieve(client: NetworkConsumerClient) {
    val hierarchy = client.getNetworkHierarchy().throwOnError().value

    val failed = mutableSetOf<String>()
    hierarchy.feeders.keys.firstOrNull()?.let {
        val result = client.getEquipmentContainer(it)
        if (result.wasSuccessful)
            failed.addAll(result.value.failed)
    }

    val result =
        client.getEquipmentContainers(hierarchy.substations.keys.asSequence() + hierarchy.feeders.keys.asSequence() + hierarchy.circuits.keys.asSequence())
    if (result.wasSuccessful)
        failed.addAll(result.value.failed)

    log("Num unresolved: ${client.service.numUnresolvedReferences()}")
    log("Num objects: ${client.service.num<IdentifiedObject>()}")
}

private fun runFeeder(client: NetworkConsumerClient) {
    val result = client.getEquipmentContainer<Feeder>("_LATHAM_8TB_LWMLNGLOW")
    result.throwOnError()
    log("Num unresolved: ${client.service.numUnresolvedReferences()}")
    log("Num objects: ${client.service.num<IdentifiedObject>()}")
}

private fun runNetworkHierarchy(client: NetworkConsumerClient) {
    val result = client.getNetworkHierarchy()
    result.throwOnError()
    val networkHierarchy = result.value
    log("Num geographical regions: ${networkHierarchy.geographicalRegions.size}")
    log("Num sub geographical regions: ${networkHierarchy.subGeographicalRegions.size}")
    log("Num substations: ${networkHierarchy.substations.size}")
    log("Num feeders: ${networkHierarchy.feeders.size}")
    log("Num circuits: ${networkHierarchy.circuits.size}")
    log("Num loops: ${networkHierarchy.loops.size}")
}

private fun time(desc: String, run: () -> Unit) {
    val startTime = System.currentTimeMillis()
    log("Running $desc...")
    ++indentLevel
    try {
        run()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    --indentLevel
    val duration = System.currentTimeMillis() - startTime
    val durationSec = (duration) / 1000
    log("$desc took ${(durationSec / 60).toInt()}:${(durationSec % 60).toString().padStart(2, '0')}.${(duration % 1000).toString().padStart(3, '0')}")
}

private fun log(msg: String) {
    logger.info("${" ".repeat(indent * indentLevel)}$msg")
}
