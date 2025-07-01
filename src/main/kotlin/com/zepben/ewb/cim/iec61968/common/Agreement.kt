/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.common

/**
 * Formal agreement between two parties defining the terms and conditions for a set of services. The specifics of
 * the services are, in turn, defined via one or more service agreements.
 */
abstract class Agreement(mRID: String = "") : Document(mRID)
