/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61968.customers;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.Column;
import com.zepben.cimbend.database.sqlite.tables.iec61968.common.TableOrganisationRoles;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;

@EverythingIsNonnullByDefault
public class TableCustomers extends TableOrganisationRoles {

    public final Column KIND = new Column(++columnIndex, "kind", "TEXT", NOT_NULL);
    public final Column NUM_END_DEVICES = new Column(++columnIndex, "num_end_devices", "INTEGER", NOT_NULL);

    @Override
    public String name() {
        return "customers";
    }

    @Override
    protected Class<?> getTableClass() {
        return TableCustomers.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
