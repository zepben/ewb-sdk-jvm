/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.cim.iec61970.base.core.PowerSystemResource
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.validateReference
import com.zepben.ewb.testing.MRIDListWrapper

/**
 * Specifies a set of equipment that works together to control a power system quantity such as voltage or flow.
 * Remote bus voltage control is possible by specifying the controlled terminal located at some place remote from the controlling equipment.
 * The specified terminal shall be associated with the connectivity node of the controlled point. The most specific subtype of RegulatingControl shall be used
 * in case such equipment participate in the control, e.g. TapChangerControl for tap changers.
 *
 * For flow control, load sign convention is used, i.e. positive sign means flow out from a TopologicalNode (bus) into the conducting equipment.
 *
 * The attribute minAllowedTargetValue and maxAllowedTargetValue are required in the following cases:
 * For a power generating module operated in power factor control mode to specify maximum and minimum power factor values;
 * Whenever it is necessary to have an off center target voltage for the tap changer regulator. For instance, due to long cables to offshore wind farms and the
 * need to have a simpler setup at the offshore transformer platform, the voltage is controlled from the land at the connection point for the offshore wind
 * farm.
 *
 * Since there usually is a voltage rise along the cable, there is typically an overvoltage of up 3-4 kV compared to the on shore station.
 * Thus, in normal operation the tap changer on the on shore station is operated with a target set point, which is in the lower parts of the dead band.
 *
 * The attributes minAllowedTargetValue and maxAllowedTargetValue are not related to the attribute targetDeadband, and thus they are not treated as an
 * alternative of the targetDeadband. They are needed due to limitations in the local substation controller.
 *
 * The attribute targetDeadband is used to prevent the power flow from move the tap position in circles (hunting) that is to be used regardless of the
 * attributes minAllowedTargetValue and maxAllowedTargetValue.
 *
 * @property discrete The regulation is performed in a discrete mode. This applies to equipment with discrete controls, e.g. tap changers and shunt compensators.
 * @property mode The regulating control mode presently available. This specification allows for determining the kind of regulation without need for obtaining
 * the units from a schedule.
 * @property monitoredPhase Phase voltage controlling this regulator, measured at regulator location.
 * @property targetDeadband This is a deadband used with discrete control to avoid excessive update of controls like tap changers and shunt compensator banks
 * while regulating. The units are the base units appropriate for the mode. The attribute shall be a positive value or zero. If [RegulatingControl.discrete] is
 * set to "false", the RegulatingControl.targetDeadband is to be ignored. Note that for instance, if the targetValue is 100 kV and the targetDeadband is 2 kV
 * the range is from 99 to 101 kV.
 * @property targetValue The target value specified for case input. This value can be used for the target value without the use of schedules. The value has the
 * units appropriate to the mode attribute.
 * @property enabled The flag tells if regulation is enabled.
 * @property maxAllowedTargetValue Maximum allowed target value (RegulatingControl.targetValue).
 * @property minAllowedTargetValue Minimum allowed target value (RegulatingControl.targetValue).
 * @property ratedCurrent [ZBEX] The rated current of associated CT in amps for this RegulatingControl. Forms the base used to convert Line Drop Compensation
 * settings from ohms to voltage.
 * @property terminal The terminal associated with this regulating control. The terminal is associated instead of a node, since the terminal could connect into
 * either a topological node or a connectivity node. Sometimes it is useful to model regulation at a terminal of a bus bar object.
 * @property ctPrimary [ZBEX] Current rating of the CT, expressed in terms of the current (in Amperes) that flows in the Primary where the 'Primary' is the conductor
 * being monitored. It ensures proper operation of the regulating equipment by providing the necessary current references for control actions. An important side
 * effect of this current value is that it also defines the current value at which the full LDC R and X voltages are applied by the controller, where enabled.
 * @property minTargetDeadband [ZBEX] This is the minimum allowable range for discrete control in regulating devices, used to prevent frequent control actions and
 * promote operational stability. This attribute sets a baseline range within which no adjustments are made, applicable across various devices like voltage
 * regulators, shunt compensators, or battery units.
 * @property regulatingCondEqs The [RegulatingCondEq] that are controlled by this regulating control scheme.
 */
abstract class RegulatingControl(mRID: String = "") : PowerSystemResource(mRID) {

    var discrete: Boolean? = null
    var mode: RegulatingControlModeKind = RegulatingControlModeKind.UNKNOWN
    var monitoredPhase: PhaseCode = PhaseCode.NONE
    var targetDeadband: Float? = null
    var targetValue: Double? = null
    var enabled: Boolean? = null
    var maxAllowedTargetValue: Double? = null
    var minAllowedTargetValue: Double? = null

    @ZBEX
    var ratedCurrent: Double? = null
    var terminal: Terminal? = null

    @ZBEX
    var ctPrimary: Double? = null

    @ZBEX
    var minTargetDeadband: Double? = null

    private var _regulatingCondEqs: MutableList<RegulatingCondEq>? = null

    val regulatingCondEqs: MRIDListWrapper<RegulatingCondEq>
        get() = MRIDListWrapper(
            getter = { _regulatingCondEqs },
            setter = { _regulatingCondEqs = it })

    @Deprecated("BOILERPLATE: Use regulatingCondEqs.size instead")
    fun numRegulatingCondEqs(): Int = regulatingCondEqs.size

    @Deprecated("BOILERPLATE: Use regulatingCondEqs.getByMRID(mRID) instead")
    fun getRegulatingCondEq(mRID: String): RegulatingCondEq? = regulatingCondEqs.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use regulatingCondEqs.add(regulatingCondEq) instead")
    fun addRegulatingCondEq(regulatingCondEq: RegulatingCondEq): RegulatingControl {
        regulatingCondEqs.add(regulatingCondEq)
        return this
    }

    @Deprecated("BOILERPLATE: Use regulatingCondEqs.remove(regulatingCondEq) instead")
    fun removeRegulatingCondEq(regulatingCondEq: RegulatingCondEq): Boolean = regulatingCondEqs.remove(regulatingCondEq)

    @Deprecated("BOILERPLATE: Use regulatingCondEqs.clear() instead")
    fun clearRegulatingCondEqs(): RegulatingControl {
        regulatingCondEqs.clear()
        return this
    }
}
