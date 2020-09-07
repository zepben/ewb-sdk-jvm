/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.database;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.cim.iec61968.assetinfo.CableInfo;
import com.zepben.cimbend.cim.iec61968.assetinfo.OverheadWireInfo;
import com.zepben.cimbend.cim.iec61968.assets.AssetOwner;
import com.zepben.cimbend.cim.iec61968.common.Location;
import com.zepben.cimbend.cim.iec61968.common.Organisation;
import com.zepben.cimbend.cim.iec61968.customers.Customer;
import com.zepben.cimbend.cim.iec61968.customers.PricingStructure;
import com.zepben.cimbend.cim.iec61968.customers.Tariff;
import com.zepben.cimbend.cim.iec61968.metering.Meter;
import com.zepben.cimbend.cim.iec61968.metering.UsagePoint;
import com.zepben.cimbend.cim.iec61968.operations.OperationalRestriction;
import com.zepben.cimbend.cim.iec61970.base.auxiliaryequipment.FaultIndicator;
import com.zepben.cimbend.cim.iec61970.base.core.*;
import com.zepben.cimbend.cim.iec61970.base.diagramlayout.Diagram;
import com.zepben.cimbend.cim.iec61970.base.diagramlayout.DiagramObject;
import com.zepben.cimbend.cim.iec61970.base.wires.*;
import com.zepben.cimbend.diagram.DiagramService;
import com.zepben.cimbend.network.NetworkService;

@EverythingIsNonnullByDefault
public interface IDatabase {

    boolean create();

    boolean load(NetworkService network, DiagramService diagramService);

    boolean preSave();

    boolean save(BaseVoltage baseVoltage);

    boolean save(PerLengthSequenceImpedance perLengthSequenceImpedance);

    boolean save(CableInfo cableInfo);

    boolean save(OverheadWireInfo overheadWireInfo);

    boolean save(Junction node);

    boolean save(EnergySource energySource);

    boolean save(LinearShuntCompensator linearShuntCompensator);

    boolean save(EnergyConsumer energyConsumer);

    boolean save(Disconnector disconnector);

    boolean save(Breaker breaker);

    boolean save(Fuse fuse);

    boolean save(Recloser recloser);

    boolean save(FaultIndicator faultIndicator);

    boolean save(Jumper jumper);

    boolean save(PowerTransformer powerTransformer);

    boolean save(AcLineSegment acLineSegment);

    boolean save(UsagePoint usagePoint);

    boolean save(Meter meter);

    boolean save(GeographicalRegion geographicalRegion);

    boolean save(SubGeographicalRegion subGeographicalRegion);

    boolean save(Substation substation);

    boolean save(Feeder feeder);

    boolean save(Site site);

    boolean save(Diagram diagram);

    boolean save(DiagramObject diagramObject);

    boolean save(OperationalRestriction restriction);

    boolean save(Customer customer);

    boolean save(PricingStructure pricingStructure);

    boolean save(Tariff tariff);

    boolean save(Organisation organisation);

    boolean save(AssetOwner assetOwner);

    boolean save(Location location);

    boolean postSave();

}
