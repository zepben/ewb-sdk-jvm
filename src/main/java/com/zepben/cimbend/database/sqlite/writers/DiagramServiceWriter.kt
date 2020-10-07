/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.writers

import com.zepben.cimbend.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.cimbend.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.cimbend.diagram.DiagramService

class DiagramServiceWriter(hasCommon: (String) -> Boolean, addCommon: (String) -> Boolean) : BaseServiceWriter<DiagramService, DiagramCIMWriter>(hasCommon, addCommon) {

    override fun save(service: DiagramService, writer: DiagramCIMWriter): Boolean {
        var status = true

        service.sequenceOf<DiagramObject>().forEach { status = status and validateSave(it, writer::save, "diagram object") }
        service.sequenceOf<Diagram>().forEach { status = status and validateSave(it, writer::save, "diagram") }

        return status
    }

}
