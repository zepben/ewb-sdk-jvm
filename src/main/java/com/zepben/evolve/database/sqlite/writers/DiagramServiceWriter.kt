/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.writers

import com.zepben.evolve.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.evolve.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.evolve.services.diagram.DiagramService

class DiagramServiceWriter(hasCommon: (String) -> Boolean, addCommon: (String) -> Boolean) : BaseServiceWriter<DiagramService, DiagramCIMWriter>(hasCommon, addCommon) {

    override fun save(service: DiagramService, writer: DiagramCIMWriter): Boolean {
        var status = super.save(service, writer)

        service.sequenceOf<DiagramObject>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Diagram>().forEach { status = status and validateSave(it, writer::save) }

        return status
    }

}
