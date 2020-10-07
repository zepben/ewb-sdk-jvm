/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61968.common

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import java.time.Instant

/**
 * Parent class for different groupings of information collected and managed as a part of a business process.
 * It will frequently contain references to other objects, such as assets, people and power system resources.
 *
 * @property title Document title.
 * @property createdDateTime Date and time that this document was created.
 * @property authorName Name of the author of this document.
 * @property type Utility-specific classification of this document, according to its corporate standards, practices, and existing IT systems (e.g., for management of assets, maintenance, work, outage, customers, etc.).
 * @property status Status of subject matter (e.g., Agreement, Work) this document represents.
 * @property comment Free text comment.
 */
abstract class Document(mRID: String = "") : IdentifiedObject(mRID) {

    var title: String = ""
    var createdDateTime: Instant? = null
    var authorName: String = ""
    var type: String = ""
    var status: String = ""
    var comment: String = ""
}
