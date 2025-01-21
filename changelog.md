# Zepben EWB SDK changelog
## [0.24.0] - 2025-01-21
### Breaking Changes
* Database readers and writes for each `BaseService` no longer accept a `MetadataCollection`, and will instead use the collection of the provided service.
* `AcLineSegment.perLengthSequenceImpedance` has been corrected to `perLengthImpedance`. This has been done in a non-breaking way, however the public resolver
  `Resolvers.perLengthSequenceImpedance` is now `Resolvers.perLengthImpedance`, correctly reflecting the CIM relationship.
* Removed `getCurrentEquipmentForFeeder` implementation for `NetworkConsumer` as its functionality is now incorporated in `getEquipmentForContainers`.

### New Features
* Network state services for updating and querying network state events via gRPC.
* Client functionality for updating and querying network states via gRPC service stub.
* `BaseService` now contains a `MetadataCollection` to tightly couple the metadata to the associated service.
* Added `Services`, a new class which contains a copy of each `BaseService` supported by the SDK.
* Added `connectWithAccessTokenInsecure()` for connecting to a gRPC service using an access token without SSL/TLS.
* Added `connectWithAccessToken()` for connecting to a gRPC service using an access token with SSL/TLS.
* Added the following new CIM classes:
  * `AssetFunction`, the function performed by an asset.
  * `BatteryControl`, a new class which describes behaviour specific to controlling a `BatteryUnit`.
  * `Clamp`: A Clamp is a galvanic connection at a line segment where other equipment is connected. A Clamp does not cut the line segment. A Clamp is
    ConductingEquipment and has one Terminal with an associated ConnectivityNode. Any other ConductingEquipment can be connected to the Clamp ConnectivityNode.
    __NOT CURRENTLY FULLY SUPPORTED BY TRACING__
  * `ControlledAppliance`, a new class representing the identity of the appliance controlled by a specific `EndDeviceFunction`.
  * `Cut`: A cut separates a line segment into two parts. The cut appears as a switch inserted between these two parts and connects them together. As the cut is
    normally open there is no galvanic connection between the two line segment parts. But it is possible to close the cut to get galvanic connection. The cut
    terminals are oriented towards the line segment terminals with the same sequence number. Hence the cut terminal with sequence number equal to 1 is oriented
    to the line segment's terminal with sequence number equal to 1. The cut terminals also act as connection points for jumpers and other equipment, e.g. a
    mobile generator. To enable this, connectivity nodes are placed at the cut terminals. Once the connectivity nodes are in place any conducting equipment can
    be connected at them.
    __NOT CURRENTLY FULLY SUPPORTED BY TRACING__
  * `EndDeviceFunction`, the function performed by an end device such as a meter, communication equipment, controllers, etc.
  * `PanDemandResponseFunction`, a new class which contains `EndDeviceFunctionKind` and the identity of the `ControlledAppliance` of this function.
  * `PerLengthPhaseImpedance`, a new class used for representing the impedance of individual wires on an AcLineSegment.
  * `PhaseImpedanceData`, a data class with a link to `PerLengthPhaseImpedance`, for capturing the phase impedance data of an individual wire.
  * `StaticVarCompensator`, a new class representing a facility for providing variable and controllable shunt reactive power.
* Added new enums:
  * `BatteryControlMode`
  * `EndDeviceFunctionKind`
  * `SVCControlMode`

### Enhancements
* Added `ctPrimary` and `minTargetDeadband` to `RegulatingContrl`.
* Added an unordered collection comparator.
* Added the energized relationship for the current state of network between `Feeder` and `LvFeeder`.
* Updated `NetworkConsumer`'s `getEquipmentForContainers`, `getEquipmentContainers` and `getEquipmentForLoop` to allow requesting normal, current or all
  equipments.
* gRPC now supports `FeederDirection.CONNECTOR`.

### Fixes
* None.

### Notes
* `Cut` and `Clamp` have been added to the model, but no processing for them has been added to the tracing, so results will not be what you expect.

## [0.23.0] - 2024-10-18
### Breaking Changes
* Updated to latest evolve-grpc major version.
* Removed unused AuthType enum.
* Removed unused kotlinx-serialization-json dependency.
* Updated to latest ewb-conn, and hence the signature of these helper functions have changed:
  * `Connect.connectWithSecret`:
    * `issuerDomain` has been renamed to `issuer`;
  * `Connect.connectWithPassword`:
    * `issuerDomain` has been renamed to `issuer`;
* Renamed `TablePowerElectronicsUnit` to `TablePowerElectronicsUnits`.
* CIM object removal functions no longer support `null`. e.g. You must pass a valid `Terminal` to `ConductingEquipment.removeTerminal` rather than a nullable
  object.
* `DiagramObject.getPoint` no longer throws an `IndexOutOfRange` exception for an invalid sequence number, and returns `null` to match other functions of this
  type.
* Removed the `PowerTransformer.getRating` overload which took a rating value. You can still get a rating via its `TransformerCoolingType`.
* Removed `PowerTransformer.forEachRating` which looped over the collection with an index that made no sense. Please loop over `PowerTransformer.sRatings`
  instead.
* `Equipment` to `EquipmentContainer` links for LV feeders are no longer written to the database, they should never have been.
* Refactored `EwbDataFilePaths`:
  * The `EwbDataFilePaths` class has been refactored into an interface to enhance flexibility and abstraction.
  * A new class, `LocalEwbDataFilePaths`, has been introduced to specifically handle the resolution of database paths for the local file system.
* `Switch.ratedCurrent` has been converted to a `double` (used to be an `integer`). Type safe languages will need to be updated to support floating point
  arithmatic/syntax.
* Deprecated `TracedPhases`, however the internal constructor property has been removed. `Terminal.normalPhases`
  and `Terminal.currentPhases` should be used instead of `Terminal.tracedPhases` going forward.
* `JWTAuthoriser.authorise` no longer accepts a permissions claims field, instead it will attempt to retrieve claims from the "permissions" field if it exists
  in the token, or the "roles" field if the "permissions" field doesn't exist.
* `JWTAuthenticator` has a new signature to accept a list of trusted domains rather than a single domain, and a `JWTMultiIssuerVerifierBuilder` rather than a
  `UrlJwkProvider`.
* `Auth0AuthHandler` has a new signature and no longer accepts a `permissionsField` to pass onto `JWTAuthoriser.authorise`. (See above change to
  `JWTAuthoriser.authorise`)
* `AuthRoute.routeFactory` has a new signature. Now accepts a list of `TrustedIssuer`'s in place of a `urlJwkProvider` and `issuer`.
* Removed obsolete `SwitchStateClient` and corresponding `SwitchStateUpdate` which only communicated with a server implementation that logged the functionality
  was not implemented.

### New Features
* A file named after the ID of an ingestion job is now created when running `MetricsDatabaseWriter.save()`. For this feature to take effect, a `modelPath` must
  be provided when constructing the `MetricsDatabaseWriter`.
* You can now remove the following by index:
  * `PositionPoint` from a `Location`.
  * `DiagramObjectPoint` from a `DiagramObject`.
  * `RelaySetting` from a `ProtectionRelayFunction`.
* Data Model change:
  * Add `phaseCode` variable to `UsagePoint`
  * Added new classes:
    * `Curve`
    * `CurveData`
    * `EarthFaultCompensator`
    * `GroundingImpedance`
    * `PetersenCoil`
    * `ReactiveCapabilityCurve`
    * `RotatingMachine`
    * `SynchronousMachine`
* Added `OpenDssReportBatch` and a new `failure` OpenDSS report type to the hosting capacity API.
* Updated grpc to support `InterventionConfig` and initial implementation of `SwitchState`.
* `JWTAuthenticator` now supports authenticating tokens from multiple different issues via the use of `JWTMultiIssuerVerifierBuilder`.
* `JWTMultiIssuerVerifierBuilder` will create a JWTVerifier based on the `iss` (issuer) of the token provided to `getVerifier()`. The returned JWTVerifier will
  also validate that the audience claim matches the `requiredAudience` supplied to the `JWTMultiIssuerVerifierBuilder`.
* `TrustedIssuer` now supports lazy fetching of `TrustedIssuer.providerDetails` by accepting a lambda that takes an issuer domain and returns a
  `ProviderDetails`.

### Enhancements
* Added feature list in documentation.
* Changed `NetworkContainerMetrics` to a delegate type to assist in writing metrics creators:
  * `NetworkContainerMetrics::plus(key: String, amount: Number)`: Increases a metric by a certain value. If the metric doesn't exist yet, it is
    automatically created and set to zero before being increased. A negative value may be used for `amount` to decrease the metric.
  * `NetworkContainerMetrics::inc(key: String)`: Equivalent to `NetworkContainerMetrics.plus(key, 1.0)`
  * `NetworkContainerMetrics::set(key: String, value: Int)`: Allows setting a metric using an integer rather than a double-precision float:
    ```
    metrics[TotalNetworkContainer]["metric-name"] = 3
    ```
* `GrpcChannelBuilder` tests the connectivity of newly created channels before returning them. This is done by calling `getMetadata()` against all known
  services. The channel is returned after the first successful response. Any connectivity errors will be propagated to the user. If no connectivity errors are
  encountered but no successful responses is received from the known services, a `GrpcConnectionException` is thrown.

### Fixes
* Update superpom for dokka plugin upgrade so maven central deploy works again.

### Notes
* None.


## [0.22.0] - 2024-05-30

### Breaking Changes

* None.

### New Features

* None.

### Enhancements

* Added `specialNeed` to `Customer` to capture any special needs of the customer, e.g. life support.

### Fixes

* None.

### Notes

* None.

## [0.21.0] - 2024-05-16

### Breaking Changes

* None.

### New Features

* None.

### Enhancements

* Added `designTemperature` and `designRating` to `Conductor` to capture limitations in the conductor based on the
  network design and physical surrounds of the conductor.

### Fixes

* Use latest version of ewb-conn for fix to issuer key in auth configuration endpoint

### Notes

* None.

## [0.20.0] - 2024-05-14

### Breaking Changes

* This is the last release using an artifact ID of `evolve-sdk`, future releases will be made as `ewb-sdk`.
* Removed `EwbDatabaseType`. Use `DatabaseType` instead.
* The filename of the `results cache` database has been changed to `results-cache` from `results_cache`. Any existing `results cache` database files will need
  to be renamed to `results-cache` to continue to be used.
* Moved the following modules under `com.zepben.evolve.database.sqlite` to `com.zepben.evolve.database.sqlite.cim`:
  * `customer`
  * `diagram`
  * `metadata`
  * `network`
  * `tables`
  * `upgrade`
* Moved (with some renaming) the following classes from `com.zepben.evolve.database.sqlite.common` to `com.zepben.evolve.database.sqlite.cim`:
  * `BaseServiceReader`
  * `BaseServiceWriter`
  * `BaseDatabaseReader` as `CimDatabaseReader`
  * `BaseDatabaseTables` as `CimDatabaseTables`
  * `BaseDatabaseWriter` as `CimDatabaseWriter`
  * `BaseCimReader` as `CimReader`
  * `BaseCimWriter` as `CimWriter`
* Moved `TableVersion` to `com.zepben.evolve.database.sqlite.common`. Instances for CIM and metrics are in `com.zepben.evolve.database.sqlite.cim.tables`
  and `com.zepben.evolve.database.sqlite.metrics.tables` respectively.

### New Features

* Added `EwbDataFilePaths` for working with files and folders used by EWB.
* Added tables for metrics database (`MetricsDatabaseTables`) and model for ingestion job (`IngestionJob`).
  * Using `MetricsDatabaseWriter`, A single `IngestionJob` may be saved to the database along with its metrics and job sources.
* Added `METRICS` to the `DatabaseType` enum.

### Enhancements
* None.

### Fixes

* None.

### Notes

* None.

## [0.19.0] - 2024-04-08

### Enhancements

* Update super-pom to 0.36.0 for lucene and ktor dependencies.

## [0.18.0] - 2024-04-08

### Breaking Changes

* Updated to super-pom version 0.34.x.
* Hosting capacity LoadShape protobuf now supports reactive power values.
* `IdentifiedObject.addName` has been refactored to take in a `NameType` and a `String`. This is doing the same thing under the hood as previous `addName()`
  function,
  but simplifies the input by lowering the amount of objects that needed to be created prior to adding names.
  Example usage change:
  `obj.addName(nameType, "name", obj))` or `obj.addName(nameType.getOrAddName("name", obj))` becomes `obj.addName(nameType, "name")`
* `addName()`/`removeName()` related function for both `IdentifiedObject` and `NameType` will now also perform the same function on the other object type.
  i.e. Removing a name from the identified object will remove it from the name type and vice versa. Same interaction is also applied to adding a name.
* Removed `ProtectionEquipment`.
* Change of inheritance: `CurrentRelay` &rarr; `ProtectionEquipment`.
  becomes `CurrentRelay` &rarr; `ProtectionRelayFunction`.
* Removed symmetric relation `ProtectionEquipment` &harr; `ProtectedSwitch`.
* Renamed `CurrentRelayInfo` to `RelayInfo`.
  * The override `assetInfo: RelayInfo?` has been moved from `CurrentRelay` to its new parent class, `ProtectionRelayFunction`.
  * Renamed `RelayInfo.removeDelay` to `RelayInfo.removeDelayAt`. The original method name has been repurposed to remove a delay by its value rather than its
    index.
* Reworked values for enumerable type `ProtectionKind`.
* Removed `IdentifiedObject.removeNamesFromTypes()`. Use `IdentifiedObject.clearNames()` instead.
* Removed `DiagramServiceInstanceCache` and `NetworkServiceInstanceCache`.
* The database has been split into three databases, which will change the imports of most related classes:
  1. The existing database containing the network model (`*-network-model.sqlite`) with classes in the `network` package.
  2. A new database containing the customer information (`*-customers.sqlite`) with classes in the `customer` package.
  3. A new database containing the diagrams (`*-diagrams.sqlite`) with classes in the `diagram` package.
* The database split has resulted in the database classes also being split, e.g. `DatabaseReader` is now `NetworkDatabaseReader`, `CustomerDatabaseReader` and `
  DiagramDatabaseReader`.
* Renamed the following tables (and their associated indexes):
  * `battery_unit` to `battery_units`
  * `photo_voltaic_unit` to `photo_voltaic_units`
  * `power_electronics_connection` to `power_electronics_connections`
  * `power_electronics_connection_phase` to `power_electronics_connection_phases`
  * `power_electronics_wind_unit` to `power_electronics_wind_units`
  * `transformer_star_impedance` to `transformer_star_impedances`
* The `UpgradeRunner` is no longer used by the database readers. You must now call it directly if you want a database to upgrade. This change has been put in
  place due to the splitting of the database.

### New Features

* Added `getNames(IdentifiedObject)` to `NameType` to retrieve all names associated with the `NameType` that belongs to an `IdentifiedObject`.
* Added `getNames(NameType)` and `getNames(String)` to `IdentifiedObject` so user can retrieve all names for a given `NameType` of the `IdentifiedObject`
* Added new classes and fields to support advanced modelling of protection relays:
  * `SeriesCompensator`: A series capacitor or reactor or an AC transmission line without charging susceptance.
  * `Ground`: A point where the system is grounded used for connecting conducting equipment to ground.
  * `GroundDisconnector`: A manually operated or motor operated mechanical switching device used for isolating a circuit
    or equipment from ground.
  * `ProtectionRelayScheme`: A scheme that a group of relay functions implement. For example, typically schemes are
    primary and secondary, or main and failsafe.
  * `ProtectionRelayFunction`: A function that a relay implements to protect equipment.
  * `ProtectionRelaySystem`: A relay system for controlling `ProtectedSwitch`es.
  * `RelaySetting`: The threshold settings for a given relay.
  * `VoltageRelay`: A device that detects when the voltage in an AC circuit reaches a preset voltage.
  * `DistanceRelay`: A protective device used in power systems that measures the impedance of a transmission line to
    determine the distance to a fault, and initiates circuit breaker tripping to isolate the faulty
    section and safeguard the power system.
  * `RelayInfo.recloseFast`: True if recloseDelays are associated with a fast Curve, False otherwise.
  * `RegulatingControl.ratedCurrent`: The rated current of associated CT in amps for a RegulatingControl.

### Enhancements

* Added missing collection methods for `RelayInfo.recloseDelays` (`RelayInfo` was previously named `CurrentRelayInfo`):
  * `RelayInfo.getDelay(sequenceNumber: Int): Double?`
  * `RelayInfo.forEachDelay(action: (sequenceNumber: Int, delay: Double) -> Unit)`
  * `RelayInfo.removeDelay(delay: Double?): Boolean`
    * The original method with this name has been renamed to `RelayInfo.removeDelayAt(index: Int): Double?`.
* Cleaned up code using IntelliJ code inspection. Some typos in documentation have also been fixed.
* Added missing `@JvmOverloads` for the constructors of the following CIM classes: `NoLoadTest`, `OpenCircuitTest`, `PowerTransformerInfo`, `ShortCircuitTest`,
  `ShuntCompensatorInfo`, `SwitchInfo`, `TransformerEndInfo`, `TransformerTankInfo`, `Pole`, `Streetlight`, `TapChangerControl`, `TransformerStarImpedance`,
  `BatteryUnit`, `PhotoVoltaicUnit`, `PowerElectronicsWindUnit`, and `EvChargingUnit`.
* Added helper properties `t1`, `t2`, and `t3` to `ConductingEquipment` which get the first, second, and third terminal respectively. A `NullPointerException`
  is thrown if there is no such terminal (e.g. evaluating `br.t3` for a breaker `br` that has only two terminals).

### Fixes

* Fixed transitive bug that made a database round-trip test fail on Windows due to an issue in `sqlitejdbc.dll`.

### Notes

* None.

## [0.17.1] - 2024-01-12

### Breaking Changes

* None.

### New Features

* Allow setting gRPC `maxInboundMessageSize` via `GrpcChannelBuilder.build`.

### Enhancements

* None.

### Fixes

* Accept larger protobuf messages up to 20MB in size by default.

### Notes

* None.

## [0.17.0] - 2023-11-23

### Breaking Changes

* None.

### New Features

* Updated to evolve-grpc 0.27.0.
* Updated super-pom to version 0.33.0
* Added `connectWithIdentity()` for connecting using Azure Managed Identities.
* Added `getMetadata` to `CustomerConsumerClient`, `DiagramConsumerClient`, and `NetworkConsumerClient`. This returns a `ServiceInfo` containing `DataSource`
  and version information of the connected service.

### Enhancements

* Update docusaurus version and the configuration.

### Fixes

* None.

### Notes

* None.

## [0.16.0] - 2023-09-13

### Breaking Changes

* Deprecated old style accessors in favour of Kotlin accessors for `SinglePhaseKind`. To use the new function make the following modification to your code:
  * Kotlin:
    * `spk.value()` -> `spk.value`
    * `spk.maskIndex()` -> `spk.maskIndex`
    * `spk.bitMask()` -> `spk.bitMask`
  * Java:
    * `spk.value()` -> `spk.getValue()`
    * `spk.maskIndex()` -> `spk.getMaskIndex()`
    * `spk.bitMask()` -> `spk.getBitMask()`
* `SetDirection.run(NetworkService)` will no longer set directions for feeders with a head terminal on an open switch. It is expected these feeders are either
  placeholder feeders with no meaningful equipment, or are energised from another feeder which will set the directions from the other end.

### New Features

* PowerTransformerEnd now supports multiple ratings based on cooling types attached to the transformer. Use new `addRating` and `getRating` methods.
* Added new classes:
  * TapChangerControl
  * EvChargingUnit
  * RegulatingControl
* Added new fields:
  * Equipment.commissionedDate
  * UsagePoint
    * ratedPower
    * approvedInverterCapacity
  * ProtectionEquipment
    * directable
    * powerDirection
  * CurrentRelayInfo.recloseDelays
  * DER register fields on PowerElectronicsConnection
* Added new enums
  * PowerDirectionKind
  * RegulatingControlModeKind
  * TransformerCoolingType
* Added support for authentication via Azure Entra ID M2M tokens.

### Enhancements

* Performance enhancement for `ConnectedEquipmentTrace` when traversing elements with single terminals.
* Added support for LV2 below SWER transformers.
* Improved logging when saving a database.
* The `TestNetworkBuilder` has been enhanced with the following features:
  * You can now set the ID's without having to create a customer 'other' creator.
  * Added Kotlin wrappers for `.fromOther` and `.toOther` that allow you to pass a class type rather than a creator. e.g. `.toOther<Fuse>()` instead
    of `.toOther(::Fuse)` or `.toOther( { Fuse(it) } )`.
  * Added inbuilt support for `PowerElectronicsConnection` and `EnergyConsumer`
  * The `to*` and `connect` functions can specify the connectivity node mRID to use. This will only be used if the terminals are not already connected.
* Added `+` and `-` operators to `PhaseCode` and `SinglePhaseKind`.
* `TraversalQueue` now has `addAll` methods taking either a collection or varargs, which by default will just call `add` for each item, but can be overridden if
  there is an `addAll` available on the underlying queue implementation.
* `Traversal` has two new helper methods:
  * `ifNotStopping`: Adds a step action that is only called if the traversal is not stopping on the item.
  * `ifStopping`: Adds a step action that is only called if the traversal is stopping on the item.

### Fixes

* Asking for the traced phases as a phase code when there are no nominal phases no longer throws.
* Feeder directions are now stopped at substation transformers in the same way as assigning equipment in case the feeder has no breaker, or the start point is
  not inline.

### Notes

* Deprecated setting `ratedS` on PowerTransformerEnd.

## [0.15.0]

### Breaking Changes

* The CIM consumer clients (network, diagram and customer) have had the following changes:
  * Implemented the `AutoCloseable` interface. Existing applications that use any of the clients, but do not provide a gRPC stub directly to those clients,
    should be refactored to close the client in order to shut down the `ExecutorService` that is created under the covers.
  * You can provide an optional `ExecutorService` when creating a client, which is used to monitor the gRPC stub. If one is provided, it will be shutdown when
    the client is closed.
* Removed support for `RecloseSequences`. This functionality will be re-added to the model in later versions.

### New Features

* Added `getCustomersForContainer` to `CustomerConsumerClient` which allows fetching all the `Customer`s for a given `EquipmentContainer`
* Added `getDiagramObjects` to `DiagramConsumerClient` which allows fetching all the `DiagramObject`s matching a given mRID.

### Enhancements

* None.

### Fixes

* Stopped the NetworkConsumerClient from resolving the equipment of an EquipmentContainer when resolving references. Equipment for containers must always be
  explicitly requested by the client.

### Notes

* None.

## [0.14.0] - 2023-02-08

### Breaking Changes

* Removed `GrpcChannelFactory`. The connect methods in the `Connect` object should be used instead. `GrpcChannelBuilder` may be used if more customisation is
  required (e.g. TLS client authentication).
* Renamed `FeederDirection.has` to `FeederDirection.contains`, which can be used via its operator version `in`. e.g. `BOTH.has(DOWNSTREAM)` can be replaced with
  `BOTH.contains(DOWNSTREAM)` or `DOWNSTREAM in BOTH`
* Removed deprecated function `NetworkConsumerClient.getFeeder`.
* Renamed function 'breadFirstSupplier' to 'breadthFirstSupplier' in 'BasicQueue' class.

### New Features

* Added EquivalentNetworkUtils class that contains utility methods to add equivalent networks to the edges of gRPC requested network services.
* Added methods for connecting to the gRPC service in a utility object named `Connect`:
  * `Connect.connectInsecure`: Used to connect to a gRPC service that does not use SSL/TLS.
  * `Connect.connectTls`: Used to connect to a gRPC service that uses SSL/TLS, without user authentication.
  * `Connect.connectWithSecret`: Used to connect to a gRPC service that uses SSL/TLS, using the OAuth 2.0 client credential flow.
  * `Connect.connectWithPassword`: Used to connect to a gRPC service that uses SSL/TLS, using the OAuth 2.0 password grant flow.
* Added support for current transformers and power transformers with the following classes in `com.zepben.evolve.cim.*`:
  * In `com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo`:
    * `CurrentTransformerInfo`: Properties of current transformer asset.
    * `PotentialTransformerInfo`: Properties of potential transformer asset.
  * In `com.zepben.evolve.cim.iec61968.infiec61968.infcommon`:
    * `Ratio`: Fraction specified explicitly with a numerator and denominator, which can be used to calculate the quotient.
  * In `com.zepben.evolve.cim.iec61970.base.auxiliaryequipment`:
    * `CurrentTransformer`: Instrument transformer used to measure electrical qualities of the circuit that is being protected and/or monitored.
    * `PotentialTransformer`: Instrument transformer (also known as Voltage Transformer) used to measure electrical qualities of the circuit that
      is being protected and/or monitored.
    * `PotentialTransformerKind`: The construction kind of the potential transformer. (Enum)
    * `Sensor`: This class describes devices that transform a measured quantity into signals that can be presented at displays,
      used in control or be recorded.
* Added `PowerTransformer().getEnd(Terminal)`, which gets a `PowerTransformerEnd` by the `Terminal` it's connected to.
* Added the following functions to `ConnectedEquipmentTrace` for creating traces that work on `ConductingEquipment`, and ignore phase connectivity, instead
  considering things to be connected if they share a `ConnectivityNode`:
  * `newNormalDownstreamEquipmentTrace`: Creates a trace that traverses in the downstream direction using the normal state of the network.
  * `newNormalUpstreamEquipmentTrace`: Creates a trace that traverses in the upstream direction using the normal state of the network.
  * `newCurrentDownstreamEquipmentTrace`: Creates a trace that traverses in the downstream direction using the current state of the network.
  * `newCurrentUpstreamEquipmentTrace`: Creates a trace that traverses in the upstream direction using the current state of the network.
* Added support for protection equipment with the following classes, enums, and fields:
  * `SwitchInfo`: Switch datasheet information.
  * `ProtectionEquipment`: An electrical device designed to respond to input conditions in a prescribed manner and after specified conditions are met to cause
    contact operation or similar abrupt change in associated electric control circuits, or simply to display the detected condition.
  * `CurrentRelay`: A device that checks current flow values in any direction or designated direction.
  * `CurrentRelayInfo`: Current relay datasheet information.
  * `RecloseSequence`: A reclose sequence (open and close) is defined for each possible reclosure of a breaker.
  * `ProtectionKind`: The kind of protection being provided by this protection equipment.
  * `ProtectedSwitch::breakingCapacity`: The maximum fault current in amps a breaking device can break safely under prescribed conditions of use.
  * `Switch::ratedCurrent`: The maximum continuous current carrying capacity in amps governed by the device material and construction.
    The attribute shall be a positive value.
  * `Breaker::inTransitTime`: The transition time from open to close in seconds.

### Enhancements

* Added `FeederDirection.not` operator function.

### Fixes

* `NetworkService.remove` for `LvFeeder` function fixed (previously added instead).
* Added confirmation of removal to the remove component test.
* Fixed bug where limited connected traces with `maximumSteps = 1` could include equipment 2 steps away when using a direction.
* Made `type` column of `potential_transformers` non-null.
* AssignToFeeders now correctly assigns AuxiliaryEquipment.
* AssignToLvFeeders now correctly assigns AuxiliaryEquipment.
* AssignToFeeders now correctly assigns ProtectionEquipment.
* AssignToLvFeeders now correctly assigns ProtectionEquipment.

### Notes

* None.
