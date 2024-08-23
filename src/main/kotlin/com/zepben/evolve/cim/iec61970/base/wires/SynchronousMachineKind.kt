/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

/**
 * Synchronous machine type.
 */
@Suppress("EnumEntryName")
enum class SynchronousMachineKind {

    UNKNOWN,

    /**
     * Indicates the synchronous machine can operate as a generator.
     */
    generator,

    /**
     * Indicates the synchronous machine can operate as a condenser.
     */
    condenser,

    /**
     * Indicates the synchronous machine can operate as a generator or as a condenser.
     */
    generatorOrCondenser,

    /**
     * Indicates the synchronous machine can operate as a motor.
     */
    motor,

    /**
     * Indicates the synchronous machine can operate as a generator or as a motor.
     */
    generatorOrMotor,

    /**
     * Indicates the synchronous machine can operate as a motor or as a condenser.
     */
    motorOrCondenser,

    /**
     * Indicates the synchronous machine can operate as a generator or as a condenser or as a motor.
     */
    generatorOrCondenserOrMotor

}
