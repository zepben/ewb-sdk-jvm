/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61968.operations;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.sqlite.tables.iec61968.common.TableDocuments;

@EverythingIsNonnullByDefault
public class TableOperationalRestrictions extends TableDocuments {

    @Override
    public String name() {
        return "operational_restrictions";
    }

    @Override
    protected Class<?> getTableClass() {
        return TableOperationalRestrictions.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
