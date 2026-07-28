/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate

import com.zepben.ewb.cim.iec61970.base.core.Identifiable
import kotlin.reflect.KMutableProperty1

class Backfill<T : Identifiable, O : Identifiable>(
    val getter: (T) -> Identifiable?,
    val setter: (T, O?) -> Unit,
    val backfillProp: KMutableProperty1<T, O?>,
) {
    fun apply(owner: O, element: T) {
        if (getter(element) == null)
            setter(element, owner)

        val ref = getter(element)
        require(ref === owner) {
            "${element.typeNameAndMRID()} `${backfillProp.name}` property references ${ref?.typeNameAndMRID()}, expected ${owner.typeNameAndMRID()}."
        }
    }

    fun clear(element: T) {
        setter(element, null)
    }

}