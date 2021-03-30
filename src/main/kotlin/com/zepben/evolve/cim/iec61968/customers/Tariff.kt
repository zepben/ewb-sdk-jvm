/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61968.customers

import com.zepben.evolve.cim.iec61968.common.Document

/**
 * Document, approved by the responsible regulatory agency, listing the terms and conditions,
 * including a schedule of prices, under which utility services will be provided. It has a
 * unique number within the state or province. For rate schedules it is frequently allocated
 * by the affiliated Public utilities commission (PUC).
 */
class Tariff @JvmOverloads constructor(mRID: String = "") : Document(mRID)
