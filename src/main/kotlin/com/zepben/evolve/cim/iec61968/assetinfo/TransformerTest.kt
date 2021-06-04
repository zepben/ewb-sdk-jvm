/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.assetinfo

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject

/**
 * Test result for transformer ends, such as short-circuit, open-circuit (excitation) or no-load test.
 *
 * @property basePower Base power at which the tests are conducted, usually equal to the ratedS of one of the involved transformer ends in VA.
 * @property temperature Temperature at which the test is conducted in degrees Celsius.
 */
abstract class TransformerTest(mRID: String = "") : IdentifiedObject(mRID) {

    var basePower: Int = 0
    var temperature: Double = 0.0

}
