/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.protection

import com.zepben.ewb.cim.extensions.ZBEX

/**
 * [ZBEX]
 * The kind of protection being provided by this protection equipment.
 */
@ZBEX
enum class ProtectionKind {

    /**
     * [ZBEX] Unknown
     */
    @ZBEX
    UNKNOWN,

    /**
     * [ZBEX] Overcurrent
     */
    @ZBEX
    JG,

    /**
     * [ZBEX] Instantaneous
     */
    @ZBEX
    JGG,

    /**
     * [ZBEX] Instantaneous
     */
    @ZBEX
    JGGG,

    /**
     * [ZBEX] Thermal overload
     */
    @ZBEX
    JT,

    /**
     * [ZBEX] Ground overcurrent
     */
    @ZBEX
    J0,

    /**
     * [ZBEX] Instantaneous ground overcurrent
     */
    @ZBEX
    J0GG,

    /**
     * [ZBEX] Sensitive earth fault
     */
    @ZBEX
    SEF,

    /**
     * [ZBEX] Overvoltage
     */
    @ZBEX
    VG,

    /**
     * [ZBEX] Instantaneous overvoltage
     */
    @ZBEX
    VGG,

    /**
     * [ZBEX] Undervoltage
     */
    @ZBEX
    VL,

    /**
     * [ZBEX] Instantaneous
     */
    @ZBEX
    VLL,

    /**
     * [ZBEX] Zero-sequence overvoltage
     */
    @ZBEX
    V0G,

    /**
     * [ZBEX] Instantaneous zero-sequence overvoltage
     */
    @ZBEX
    V0GG,

    /**
     * [ZBEX] Differential Current
     */
    @ZBEX
    JDIFF,

    /**
     * [ZBEX] Under frequency
     */
    @ZBEX
    FREQ,

    /**
     * [ZBEX] Over frequency
     */
    @ZBEX
    FREQG,

    /**
     * [ZBEX] Phase distance
     */
    @ZBEX
    ZL,

    /**
     * [ZBEX] Ground distance
     */
    @ZBEX
    Z0L,

    /**
     * [ZBEX] Load encroachment
     */
    @ZBEX
    LE,

    /**
     * [ZBEX] Negative-sequence overcurrent
     */
    @ZBEX
    J2G,

    /**
     * [ZBEX] A multifunctional relay for universal usages
     */
    @ZBEX
    MULTI_FUNCTION,

    /**
     * [ZBEX] A device used to monitor and protect electrical equipment from damage caused by ground faults
     */
    @ZBEX
    GROUND_CURRENT,

    /**
     * [ZBEX] A device used to detect contact accidents between an electric path and ground caused by arc ground faults
     */
    @ZBEX
    GROUND_VOLTAGE,

    /**
     * [ZBEX] Is a special self-contained air breaker or switching unit having a full complement of current, potential and control transformers, as well as relay functionality.
     */
    @ZBEX
    NETWORK_PROTECTOR,

    /**
     * [ZBEX] A device used to detect faults on long-distance lines, pinpointing not only the fault condition but also measuring the distance between the current sensing mechanism and the fault location in the wire.
     */
    @ZBEX
    DISTANCE,

    /**
     * [ZBEX] A device used to protect generators from the unbalanced load by detecting negative sequence current.
     */
    @ZBEX
    NEGATIVE_OVERCURRENT,

    /**
     * [ZBEX] A device that uses an electromagnet to open or close a circuit when the input (coil) is correctly excited
     */
    @ZBEX
    POWER,

    /**
     * [ZBEX] A device that automatically isolates a faulted section of line from the rest of the distribution system
     */
    @ZBEX
    SECTIONALIZER,

    /**
     * [ZBEX] A device used to regulate the voltage of transmission lines and can also be used to transform voltages.
     */
    @ZBEX
    AUTO_TRANSFORMER

}
