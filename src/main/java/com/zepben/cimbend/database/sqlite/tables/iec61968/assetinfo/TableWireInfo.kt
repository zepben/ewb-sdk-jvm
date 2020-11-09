/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61968.assetinfo;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.Column;
import com.zepben.cimbend.database.sqlite.tables.iec61968.assets.TableAssetInfo;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;

@EverythingIsNonnullByDefault
public abstract class TableWireInfo extends TableAssetInfo {

    public final Column RATED_CURRENT = new Column(++columnIndex, "rated_current", "NUMBER", NOT_NULL);
    public final Column MATERIAL = new Column(++columnIndex, "material", "TEXT", NOT_NULL);

}
