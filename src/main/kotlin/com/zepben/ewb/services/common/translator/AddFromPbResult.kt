/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common.translator

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject

/**
 * The result of trying to add any top level protobuf wrapper class to a service.
 *
 * NOTE: If an existing item is found in the service with the same mRID, it will be returned without merging any properties from the protobuf item.
 *
 * @property mRID The mRID of the object, even if it wasn't added to the service.
 * @property identifiedObject The [IdentifiedObject] reference if it was either added to the service, or `null` if there was an error adding it. This
 * may be a reference to an existing object if it already existed in the service.
 * @property reusedExisting `true` if the [identifiedObject] was found in the service, or `false` is a newly added item was created.
 */
data class AddFromPbResult(
    val mRID: String,
    val identifiedObject: IdentifiedObject?,
    val reusedExisting: Boolean,
)
