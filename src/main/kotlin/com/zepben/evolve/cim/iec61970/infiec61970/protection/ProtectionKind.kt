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
 * @property EF Earth Fault
 * @property SEF Sensitive Earth Fault
 * @property OC Overcurrent
 * @property IOC Instantaneous Overcurrent
 * @property IEF Instantaneous Earth Fault
 * @property REF Restricted Earth Fault
 */
enum class ProtectionKind {

    UNKNOWN,
    EF,
    SEF,
    OC,
    IOC,
    IEF,
    REF

}
