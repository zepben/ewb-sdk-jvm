/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.assetinfo

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61968.assetinfo.WireInsulationKind.*

/**
 * Kind of wire insulation.
 *
 * @property UNKNOWN Unknown insulation kind.
 * @property asbestosAndVarnishedCambric Asbestos and varnished cambric wire insulation.
 * @property beltedPilc Belted pilc wire insulation.
 * @property butyl Butyl wire insulation.
 * @property crosslinkedPolyethylene Crosslinked polyethylene wire insulation.
 * @property ethylenePropyleneRubber Ethylene propylene rubber wire insulation.
 * @property highMolecularWeightPolyethylene High nolecular weight polyethylene wire insulation.
 * @property highPressureFluidFilled High pressure fluid filled wire insulation.
 * @property lowCapacitanceRubber Low capacitance rubber wire insulation.
 * @property oilPaper Oil paper wire insulation.
 * @property other Other kind of wire insulation.
 * @property ozoneResistantRubber Ozone resistant rubber wire insulation.
 * @property rubber Rubber wire insulation.
 * @property siliconRubber Silicon rubber wire insulation.
 * @property treeResistantHighMolecularWeightPolyethylene Tree resistant high molecular weight polyethylene wire insulation.
 * @property treeRetardantCrosslinkedPolyethylene Tree retardant crosslinked polyethylene wire insulation.
 * @property unbeltedPilc Unbelted pilc wire insulation.
 * @property varnishedCambricCloth Varnished cambric cloth wire insulation.
 * @property varnishedDacronGlass Varnished dacron glass wire insulation.
 * @property crosslinkedPolyethyleneWithHelicallyWoundCopperScreen [ZBEX] Cross-Linked Polyethylene (xlpe) with helically wound copper screen
 * @property crosslinkedPolyethyleneWithWavewoundAluminiumScreen [ZBEX] Cross-Linked Polyethylene (xlpe), wavewound aluminium screen
 * @property doubleInsulatedNeutralScreened [ZBEX] Stranded copper conductor, p.v.c. insulated, copper neutral screen, p.v.c.-insulating sheath overall. (Double insulated neutral screened)
 * @property doubleWireArmour [ZBEX] Serving (or bedding) double wire armour
 * @property doubleWireArmourWithPolyvinylChlorideSheath [ZBEX] Double wire armoured PVC outer sheath
 * @property ethylenePropyleneRubberStrandedCopperConductor [ZBEX] Stranded copper, Ethylene Propylene Rubber conductor insulation
 * @property ethylenePropyleneRubberWithHelicallyWoundCopperScreen [ZBEX] Ethylene Propylene Rubber conductor insulation, helically wound copper screen
 * @property NONE [ZBEX] No insulation.
 * @property paperWithLeadAlloySheath [ZBEX] Paper insulated, lead alloy sheath
 * @property paperWithLeadAlloySheathAndPvcOuterSheathScreenedHochstadterConstruction [ZBEX] Paper insulated, lead alloy sheath, P.V.C outer sheath, Screened(Hochstadter) construction
 * @property paperWithLeadAlloySheathSingleWireArmoured [ZBEX] Paper insulated, lead alloy sheath, single wire armoured.
 * @property paperWithLeadAlloySheathSingleWireArmouredBeltedConstruction [ZBEX] Paper insulated, lead alloy sheath, single wire armoured, belted construction
 * @property paperWithLeadAlloySheathSingleWireArmouredHessianServed [ZBEX] Paper insulated, lead alloy sheath, single wire armoured, Hessian served.
 * @property paperWithLeadAlloySheathSingleWireArmouredWithHighDensityPoluethyleneScreen [ZBEX] Paper insulated, lead alloy sheath, single wire armoured, High density polyethylene(& pvc/hdpeh composite) screen
 * @property paperWithLeadAlloySheathSingleWireArmouredWithWavewoundAluminiumScreen [ZBEX] Paper insulated, lead alloy sheath, single wire armoured, wavewound aluminium screen
 * @property polyvinylChloride [ZBEX] p.v.c (Polyvinyl Chloride)
 * @property polyvinylChlorideWithPolyvinylChlorideScreen [ZBEX] p.v.c (Polyvinyl Chloride) with Polyvinyl Chloride screen
 * @property polyvinylChlorideWithWavewoundCopperScreen [ZBEX] p.v.c (Polyvinyl Chloride) with wavewound copper screen
 */
@Suppress("EnumEntryName")
enum class WireInsulationKind {


    UNKNOWN,

    asbestosAndVarnishedCambric,

    beltedPilc,

    butyl,

    crosslinkedPolyethylene,

    ethylenePropyleneRubber,

    highMolecularWeightPolyethylene,

    highPressureFluidFilled,

    lowCapacitanceRubber,

    oilPaper,

    other,

    ozoneResistantRubber,

    rubber,

    siliconRubber,

    treeResistantHighMolecularWeightPolyethylene,

    treeRetardantCrosslinkedPolyethylene,

    unbeltedPilc,

    varnishedCambricCloth,

    varnishedDacronGlass,

    @ZBEX
    crosslinkedPolyethyleneWithHelicallyWoundCopperScreen,

    @ZBEX
    crosslinkedPolyethyleneWithWavewoundAluminiumScreen,

    @ZBEX
    doubleInsulatedNeutralScreened,

    @ZBEX
    doubleWireArmour,

    @ZBEX
    doubleWireArmourWithPolyvinylChlorideSheath,

    @ZBEX
    ethylenePropyleneRubberStrandedCopperConductor,

    @ZBEX
    ethylenePropyleneRubberWithHelicallyWoundCopperScreen,

    @ZBEX
    NONE,

    @ZBEX
    paperWithLeadAlloySheath,

    @ZBEX
    paperWithLeadAlloySheathAndPvcOuterSheathScreenedHochstadterConstruction,

    @ZBEX
    paperWithLeadAlloySheathSingleWireArmoured,

    @ZBEX
    paperWithLeadAlloySheathSingleWireArmouredBeltedConstruction,

    @ZBEX
    paperWithLeadAlloySheathSingleWireArmouredHessianServed,

    @ZBEX
    paperWithLeadAlloySheathSingleWireArmouredWithHighDensityPoluethyleneScreen,

    @ZBEX
    paperWithLeadAlloySheathSingleWireArmouredWithWavewoundAluminiumScreen,

    @ZBEX
    polyvinylChloride,

    @ZBEX
    polyvinylChlorideWithPolyvinylChlorideScreen,

    @ZBEX
    polyvinylChlorideWithWavewoundCopperScreen,

}
