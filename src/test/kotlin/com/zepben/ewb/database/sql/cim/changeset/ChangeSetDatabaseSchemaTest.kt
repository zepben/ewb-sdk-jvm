/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.changeset

import com.zepben.ewb.database.sql.cim.CimDatabaseSchemaTest
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.DataSet
import com.zepben.ewb.services.common.testdata.SchemaServices
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.variant.VariantService
import com.zepben.ewb.services.variant.VariantServiceComparator
import com.zepben.ewb.services.variant.testdata.fillFields
import org.junit.jupiter.api.Test
import java.sql.Connection

class ChangeSetDatabaseSchemaTest : CimDatabaseSchemaTest<
    VariantService,
    ChangeSetDatabaseWriter,
    ChangeSetDatabaseReader,
    VariantServiceComparator,
    ChangeSet,
    >(
    describeObject = DataSet::typeNameAndMRID,
    addToService = VariantService::add,
) {
    override fun createService(): VariantService = VariantService()

    override fun createWriter(filename: String): ChangeSetDatabaseWriter =
        ChangeSetDatabaseWriter(filename)

    override fun createReader(connection: Connection, databaseDescription: String): ChangeSetDatabaseReader =
        ChangeSetDatabaseReader(connection, databaseDescription)

    override fun createComparator(): VariantServiceComparator = VariantServiceComparator()

    override fun createIdentifiedObject(): ChangeSet = ChangeSet(generateId())

//    @Test
    internal fun `test schema for each supported type`() {
        // TODO: stage is in 'the other database' so this doesn't work...
        validateSchema(SchemaServices.variantServicesOfChangeSets(::ChangeSet, ChangeSet::fillFields))
    }
}
