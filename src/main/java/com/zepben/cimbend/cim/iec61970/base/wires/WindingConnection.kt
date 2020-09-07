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
package com.zepben.cimbend.cim.iec61970.base.wires

/**
 * Winding connection type.
 *
 * @property UNKNOWN_WINDING Default
 * @property D Delta
 * @property Y Wye
 * @property Z ZigZag
 * @property Yn Wye, with neutral brought out for grounding
 * @property Zn ZigZag, with neutral brought out for grounding
 * @property A Auto-transformer common winding
 * @property I Independent winding, for single-phase connections
 */
enum class WindingConnection {

    UNKNOWN_WINDING,
    D,
    Y,
    Z,
    Yn,
    Zn,
    A,
    I
}
