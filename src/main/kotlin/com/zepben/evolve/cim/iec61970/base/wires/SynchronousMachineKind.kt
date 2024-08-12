/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

/**
 * Synchronous machine type.
 *
 * @property UNKNOWN
 * @property generator Indicates the synchronous machine can operate as a generator.
 * @property condenser Indicates the synchronous machine can operate as a condenser.
 * @property generatorOrCondenser Indicates the synchronous machine can operate as a generator or as a condenser.
 * @property motor Indicates the synchronous machine can operate as a motor.
 * @property generatorOrMotor Indicates the synchronous machine can operate as a generator or as a motor.
 * @property motorOrCondenser Indicates the synchronous machine can operate as a motor or as a condenser.
 * @property generatorOrCondenserOrMotor Indicates the synchronous machine can operate as a generator or as a condenser or as a motor.
 */
enum class SynchronousMachineKind {

    UNKNOWN,
    generator,
    condenser,
    generatorOrCondenser,
    motor,
    generatorOrMotor,
    motorOrCondenser,
    generatorOrCondenserOrMotor

}
