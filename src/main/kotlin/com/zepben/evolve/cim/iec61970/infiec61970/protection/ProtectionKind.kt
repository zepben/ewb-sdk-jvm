/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.infiec61970.protection

/**
 * The kind of protection being provided by this protection equipment.
 *
 * @property UNKNOWN Unknown
 * @property JG Overcurrent
 * @property JGG Instantaneous
 * @property JGGG Instantaneous
 * @property JT Thermal overload
 * @property J0 Ground overcurrent
 * @property J0GG Instantaneous ground overcurrent
 * @property SEF Sensitive earth fault
 * @property VG Overvoltage
 * @property VGG Instantaneous overvoltage
 * @property VL Undervoltage
 * @property VLL Instantaneous
 * @property V0G Zero-sequence overvoltage
 * @property V0GG Instantaneous zero-sequence overvoltage
 * @property JDIFF Differential Current
 * @property FREQ Under frequency
 * @property FREQG Over frequency
 * @property ZL Phase distance
 * @property Z0L Ground distance
 * @property LE Load encroachment
 * @property J2G Negative-sequence overcurrent
 * @property MULTI_FUNCTION A multifunctional relay for universal usages
 * @property GROUND_CURRENT A device used to monitor and protect electrical equipment from damage caused by ground faults
 * @property GROUND_VOLTAGE A device used to detect contact accidents between an electric path and ground caused by arc ground faults
 * @property NETWORK_PROTECTOR Is a special self-contained air breaker or switching unit having a full complement of current, potential and control transformers, as well as relay functionality.
 * @property DISTANCE A device used to detect faults on long-distance lines, pinpointing not only the fault condition but also measuring the distance between the current sensing mechanism and the fault location in the wire.
 * @property NEGATIVE_OVERCURRENT A device used to protect generators from the unbalanced load by detecting negative sequence current.
 * @property POWER A device that uses an electromagnet to open or close a circuit when the input (coil) is correctly excited
 * @property SECTIONALIZER A device that automatically isolates a faulted section of line from the rest of the distribution system
 * @property AUTO_TRANSFORMER A device used to regulate the voltage of transmission lines and can also be used to transform voltages.
 */
enum class ProtectionKind {

    UNKNOWN,
    JG,
    JGG,
    JGGG,
    JT,
    J0,
    J0GG,
    SEF,
    VG,
    VGG,
    VL,
    VLL,
    V0G,
    V0GG,
    JDIFF,
    FREQ,
    FREQG,
    ZL,
    Z0L,
    LE,
    J2G,
    MULTI_FUNCTION,
    GROUND_CURRENT,
    GROUND_VOLTAGE,
    NETWORK_PROTECTOR,
    DISTANCE,
    NEGATIVE_OVERCURRENT,
    POWER,
    SECTIONALIZER,
    AUTO_TRANSFORMER

}
