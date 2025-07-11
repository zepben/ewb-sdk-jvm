/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.wires

import com.zepben.ewb.cim.extensions.ZBEX

/**
 * [ZBEX]
 * Vector group of the transformer for protective relaying, e.g., Dyn1. For unbalanced transformers, this may not be
 * simply determined from the constituent winding connections and phase angle displacements.
 *
 * The vectorGroup string consists of the following components in the order listed: high voltage winding connection,
 * mid voltage winding connection (for three winding transformers), phase displacement clock number from 0 to 11, low
 * voltage winding connection phase displacement clock number from 0 to 11.
 *
 * The winding connections are D (delta), Y (wye), YN (wye with neutral), Z (zigzag), ZN (zigzag with neutral),
 * A (auto transformer). Upper case means the high voltage, lower case mid or low. The high voltage winding always has
 * clock position 0 and is not included in the vector group string.
 *
 * Some examples:
 *                YNy0 (two winding wye to wye with no phase displacement),
 *                YNd11 (two winding wye to delta with 330 degrees phase displacement),
 *                YNyn0d5 (three winding transformer wye with neutral high voltage, wye with neutral mid
 *                         voltage and no phase displacement, delta low voltage with 150 degrees displacement).
 *
 * Phase displacement is defined as the angular difference between the phasors representing the voltages between the
 * neutral point (real or imaginary) and the corresponding terminals of two windings, a positive sequence voltage system
 * being applied to the high-voltage terminals, following each other in alphabetical sequence if they are lettered, or
 * in numerical sequence if they are numbered: the phasors are assumed to rotate in a counter-clockwise sense.
 */
@ZBEX
enum class VectorGroup {

    /**
     * [ZBEX] UNKNOWN.
     */
    @ZBEX
    UNKNOWN,

    /**
     * [ZBEX] Dd0.
     */
    @ZBEX
    DD0,

    /**
     * [ZBEX] Dz0.
     */
    @ZBEX
    DZ0,

    /**
     * [ZBEX] Dzn0.
     */
    @ZBEX
    DZN0,

    /**
     * [ZBEX] YNy0.
     */
    @ZBEX
    YNY0,

    /**
     * [ZBEX] YNyn0.
     */
    @ZBEX
    YNYN0,

    /**
     * [ZBEX] Yy0.
     */
    @ZBEX
    YY0,

    /**
     * [ZBEX] Yyn0.
     */
    @ZBEX
    YYN0,

    /**
     * [ZBEX] Zd0.
     */
    @ZBEX
    ZD0,

    /**
     * [ZBEX] ZNd0.
     */
    @ZBEX
    ZND0,

    /**
     * [ZBEX] Dyn1.
     */
    @ZBEX
    DYN1,

    /**
     * [ZBEX] Dz1.
     */
    @ZBEX
    DZ1,

    /**
     * [ZBEX] Dzn1.
     */
    @ZBEX
    DZN1,

    /**
     * [ZBEX] Yd1.
     */
    @ZBEX
    YD1,

    /**
     * [ZBEX] YNd1.
     */
    @ZBEX
    YND1,

    /**
     * [ZBEX] YNzn1.
     */
    @ZBEX
    YNZN1,

    /**
     * [ZBEX] Yz1.
     */
    @ZBEX
    YZ1,

    /**
     * [ZBEX] Yzn1.
     */
    @ZBEX
    YZN1,

    /**
     * [ZBEX] Zd1.
     */
    @ZBEX
    ZD1,

    /**
     * [ZBEX] ZNd1.
     */
    @ZBEX
    ZND1,

    /**
     * [ZBEX] ZNyn1.
     */
    @ZBEX
    ZNYN1,

    /**
     * [ZBEX] Zy1.
     */
    @ZBEX
    ZY1,

    /**
     * [ZBEX] Zyn1.
     */
    @ZBEX
    ZYN1,

    /**
     * [ZBEX] Dy5.
     */
    @ZBEX
    DY5,

    /**
     * [ZBEX] Dyn5.
     */
    @ZBEX
    DYN5,

    /**
     * [ZBEX] Yd5.
     */
    @ZBEX
    YD5,

    /**
     * [ZBEX] YNd5.
     */
    @ZBEX
    YND5,

    /**
     * [ZBEX] YNz5.
     */
    @ZBEX
    YNZ5,

    /**
     * [ZBEX] YNzn5.
     */
    @ZBEX
    YNZN5,

    /**
     * [ZBEX] Yz5.
     */
    @ZBEX
    YZ5,

    /**
     * [ZBEX] Yzn5.
     */
    @ZBEX
    YZN5,

    /**
     * [ZBEX] ZNy5.
     */
    @ZBEX
    ZNY5,

    /**
     * [ZBEX] ZNyn5.
     */
    @ZBEX
    ZNYN5,

    /**
     * [ZBEX] Zy5.
     */
    @ZBEX
    ZY5,

    /**
     * [ZBEX] Zyn5.
     */
    @ZBEX
    ZYN5,

    /**
     * [ZBEX] Dd6.
     */
    @ZBEX
    DD6,

    /**
     * [ZBEX] Dz6.
     */
    @ZBEX
    DZ6,

    /**
     * [ZBEX] Dzn6.
     */
    @ZBEX
    DZN6,

    /**
     * [ZBEX] YNy6.
     */
    @ZBEX
    YNY6,

    /**
     * [ZBEX] YNyn6.
     */
    @ZBEX
    YNYN6,

    /**
     * [ZBEX] Yy6.
     */
    @ZBEX
    YY6,

    /**
     * [ZBEX] Yyn6.
     */
    @ZBEX
    YYN6,

    /**
     * [ZBEX] Zd6.
     */
    @ZBEX
    ZD6,

    /**
     * [ZBEX] ZNd6.
     */
    @ZBEX
    ZND6,

    /**
     * [ZBEX] Dy7.
     */
    @ZBEX
    DY7,

    /**
     * [ZBEX] Dyn7.
     */
    @ZBEX
    DYN7,

    /**
     * [ZBEX] Dz7.
     */
    @ZBEX
    DZ7,

    /**
     * [ZBEX] Dzn7.
     */
    @ZBEX
    DZN7,

    /**
     * [ZBEX] Yd7.
     */
    @ZBEX
    YD7,

    /**
     * [ZBEX] YNd7.
     */
    @ZBEX
    YND7,

    /**
     * [ZBEX] YNzn7.
     */
    @ZBEX
    YNZN7,

    /**
     * [ZBEX] Yz7.
     */
    @ZBEX
    YZ7,

    /**
     * [ZBEX] Yzn7.
     */
    @ZBEX
    YZN7,

    /**
     * [ZBEX] Zd7.
     */
    @ZBEX
    ZD7,

    /**
     * [ZBEX] ZNd7.
     */
    @ZBEX
    ZND7,

    /**
     * [ZBEX] ZNyn7.
     */
    @ZBEX
    ZNYN7,

    /**
     * [ZBEX] Zy7.
     */
    @ZBEX
    ZY7,

    /**
     * [ZBEX] Zyn7.
     */
    @ZBEX
    ZYN7,

    /**
     * [ZBEX] Dy11.
     */
    @ZBEX
    DY11,

    /**
     * [ZBEX] Dyn11.
     */
    @ZBEX
    DYN11,

    /**
     * [ZBEX] Yd11.
     */
    @ZBEX
    YD11,

    /**
     * [ZBEX] YNd11.
     */
    @ZBEX
    YND11,

    /**
     * [ZBEX] YNz11.
     */
    @ZBEX
    YNZ11,

    /**
     * [ZBEX] YNzn11.
     */
    @ZBEX
    YNZN11,

    /**
     * [ZBEX] Yz11.
     */
    @ZBEX
    YZ11,

    /**
     * [ZBEX] Yzn11.
     */
    @ZBEX
    YZN11,

    /**
     * [ZBEX] ZNy11.
     */
    @ZBEX
    ZNY11,

    /**
     * [ZBEX] ZNyn11.
     */
    @ZBEX
    ZNYN11,

    /**
     * [ZBEX] Zy11.
     */
    @ZBEX
    ZY11,

    /**
     * [ZBEX] Zyn11.
     */
    @ZBEX
    ZYN11,

    /**
     * [ZBEX] Dy1.
     */
    @ZBEX
    DY1,

    /**
     * [ZBEX] Y0.
     */
    @ZBEX
    Y0,

    /**
     * [ZBEX] YN0.
     */
    @ZBEX
    YN0,

    /**
     * [ZBEX] D0.
     */
    @ZBEX
    D0,

    /**
     * [ZBEX] ZNy1.
     */
    @ZBEX
    ZNY1,

    /**
     * [ZBEX] ZNy7.
     */
    @ZBEX
    ZNY7,

    /**
     * [ZBEX] Ddn0.
     */
    @ZBEX
    DDN0,

    /**
     * [ZBEX] DNd0.
     */
    @ZBEX
    DND0,

    /**
     * [ZBEX] DNyn1.
     */
    @ZBEX
    DNYN1,

    /**
     * [ZBEX] DNyn11.
     */
    @ZBEX
    DNYN11,

    /**
     * [ZBEX] YNdn1.
     */
    @ZBEX
    YNDN1,

    /**
     * [ZBEX] YNdn11.
     */
    @ZBEX
    YNDN11,

    /**
     * [ZBEX] Scott-T Transformer.
     */
    @ZBEX
    TTN11

}
