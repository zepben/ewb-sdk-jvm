/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61968.common

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject

/**
 * Identifies a way in which an organisation may participate in the utility enterprise (e.g., customer, manufacturer, etc).
 *
 * @property organisation [Organisation] having this role.
 */
abstract class OrganisationRole(mRID: String = "") : IdentifiedObject(mRID) {

    var organisation: Organisation? = null
}
