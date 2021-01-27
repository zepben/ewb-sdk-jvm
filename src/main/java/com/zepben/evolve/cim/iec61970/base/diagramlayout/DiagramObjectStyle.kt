/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.diagramlayout

/**
 * A reference to a style used by the originating system for a diagram object.  A diagram object style describes
 * information such as line thickness, shape such as circle or rectangle etc, and color.
 *
 * @property NONE No specific styling should be applied.
 * @property DIST_TRANSFORMER Diagram object should be styled as a distribution transformer.
 * @property ISO_TRANSFORMER Diagram object should be styled as an isolating transformer.
 * @property REVERSIBLE_REGULATOR Diagram object should be styled as a reversible regulator transformer.
 * @property NON_REVERSIBLE_REGULATOR Diagram object should be styled as a non-reversiable transformer.
 * @property ZONE_TRANSFORMER Diagram object should be styled as a zone transformer.
 * @property FEEDER_CB Diagram object should be styled as a feeder circuit breaker.
 * @property CB Diagram object should be styled as a circuit breaker.
 * @property JUNCTION Diagram object should be styled as a junction.
 * @property DISCONNECTOR Diagram object should be styled as a disconnector.
 * @property FUSE Diagram object should be styled as a fuse.
 * @property RECLOSER Diagram object should be styled as a recloser.
 * @property FAULT_INDICATOR Diagram object should be styled as a fault indicator.
 * @property JUMPER Diagram object should be styled as a jumper.
 * @property ENERGY_SOURCE Diagram object should be styled as a energy source.
 * @property SHUNT_COMPENSATOR Diagram object should be styled as a shunt compensator.
 * @property USAGE_POINT Diagram object should be styled as a usage point.
 * @property CONDUCTOR_UNKNOWN Diagram object should be styled as a conductor at unknown voltage.
 * @property CONDUCTOR_LV Diagram object should be styled as a conductor at low voltage.
 * @property CONDUCTOR_6600 Diagram object should be styled as a conductor at 6.6kV.
 * @property CONDUCTOR_11000 Diagram object should be styled as a conductor at 11kV.
 * @property CONDUCTOR_12700 Diagram object should be styled as a conductor at 12.7kV (SWER).
 * @property CONDUCTOR_22000 Diagram object should be styled as a conductor at 22kV.
 * @property CONDUCTOR_33000 Diagram object should be styled as a conductor at 33kV.
 * @property CONDUCTOR_66000 Diagram object should be styled as a conductor at 66kV.
 * @property POWER_ELECTRONICS_CONNECTION Diagram object should be styled as a power electronics connection.
 * @property BATTERY_UNIT Diagram object should be styled as a battery unit.
 * @property PHOTO_VOLTAIC_UNIT Diagram object should be styled as a photo voltaic unit.
 * @property POWER_ELECTRONICS_WIND_UNIT Diagram object should be styled as a power electronics wind unit.
 */
enum class DiagramObjectStyle(val isLineStyle: Boolean) {

    NONE(false),
    DIST_TRANSFORMER(false),
    ISO_TRANSFORMER(false),
    REVERSIBLE_REGULATOR(false),
    NON_REVERSIBLE_REGULATOR(false),
    ZONE_TRANSFORMER(false),
    FEEDER_CB(false),
    CB(false),
    JUNCTION(false),
    SWITCH(false),
    ARC_CHUTE(false),
    BRIDGE(false),
    DISCONNECTOR(false),
    FLICKER_BLADE(false),
    FUSE(false),
    GAS_INSULATED(false),
    LIVE_LINE_CLAMP(false),
    RECLOSER(false),
    FAULT_INDICATOR(false),
    JUMPER(false),
    ENERGY_SOURCE(false),
    SHUNT_COMPENSATOR(false),
    USAGE_POINT(false),
    CONDUCTOR_UNKNOWN(true),
    CONDUCTOR_LV(true),
    CONDUCTOR_6600(true),
    CONDUCTOR_11000(true),
    CONDUCTOR_12700(true),
    CONDUCTOR_22000(true),
    CONDUCTOR_33000(true),
    CONDUCTOR_66000(true),
    CONDUCTOR_132000(true),
    CONDUCTOR_220000(true),
    CONDUCTOR_275000(true),
    CONDUCTOR_500000(true),
    POWER_ELECTRONICS_CONNECTION(false),
    BATTERY_UNIT(false),
    PHOTO_VOLTAIC_UNIT(false),
    POWER_ELECTRONICS_WIND_UNIT(false)
}
