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
