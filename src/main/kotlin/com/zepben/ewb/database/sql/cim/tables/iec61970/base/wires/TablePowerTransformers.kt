/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableConductingEquipment
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `PowerTransformer` columns required for the database table.
 *
 * @property VECTOR_GROUP Vector group of the transformer for protective relaying, e.g., Dyn1. For unbalanced transformers, this may not be simply
 * determined from the constituent winding connections and phase angle displacements.
 *
 * The vectorGroup string consists of the following components in the order listed: high voltage winding connection, mid-voltage winding connection(for three
 * winding transformers), phase displacement clock number from 0 to 11,  low voltage winding connection phase displacement clock number from 0 to 11.
 * The winding connections are D(delta), Y(wye), YN(wye with neutral), Z(zigzag), ZN(zigzag with neutral), A(auto transformer). Upper case means the high
 * voltage, lower case mid or low.The high voltage winding always has clock position 0 and is not included in the vector group string.
 * Some examples: YNy0(two winding wye to wye with no phase displacement), YNd11(two winding wye to delta with 330 degrees phase displacement),
 * YNyn0d5(three winding transformer wye with neutral high voltage, wye with neutral mid-voltage and no phase displacement, delta low voltage with 150 degrees
 * displacement).
 *
 * Phase displacement is defined as the angular difference between the phasors representing the voltages between the neutral point(real or imaginary) and the
 * corresponding terminals of two windings, a positive sequence voltage system being applied to the high-voltage terminals, following each other in
 * alphabetical sequence if they are lettered, or in numerical sequence if they are numbered: the phasors are assumed to rotate in a counter-clockwise sense.
 * @property TRANSFORMER_UTILISATION The fraction of the transformer’s normal capacity (nameplate rating) that is in use. It may be expressed as the result of
 * the calculation S/Sn, where S = Load on Transformer (in VA), Sn = Transformer Nameplate Rating (in VA). A value of NaN signifies the data is missing/unknown.
 * @property CONSTRUCTION_KIND The construction kind of this transformer.
 * @property FUNCTION The function of this transformer.
 * @property POWER_TRANSFORMER_INFO_MRID Set of power transformer data, from an equipment library or data sheet.
 */
@Suppress("PropertyName")
class TablePowerTransformers : TableConductingEquipment() {

    val VECTOR_GROUP: Column = Column(++columnIndex, "vector_group", Column.Type.STRING, NOT_NULL)
    val TRANSFORMER_UTILISATION: Column = Column(++columnIndex, "transformer_utilisation", Column.Type.DOUBLE, NULL)
    val CONSTRUCTION_KIND: Column = Column(++columnIndex, "construction_kind", Column.Type.STRING, NOT_NULL)
    val FUNCTION: Column = Column(++columnIndex, "function", Column.Type.STRING, NOT_NULL)
    val POWER_TRANSFORMER_INFO_MRID: Column = Column(++columnIndex, "power_transformer_info_mrid", Column.Type.STRING, NULL)

    override val name: String = "power_transformers"

}
