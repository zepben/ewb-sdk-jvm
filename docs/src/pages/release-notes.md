#### Release History

| Version                | Released            |
|------------------------|---------------------|
| [0.17.1](#v0171)       | `11 January 2024`   |
| [0.17.0](#v0170)       | `22 November 2023`  |
| [0.16.0](#v0160)       | `13 September 2023` |
| [0.15.0](#v0150)       | `01 May 2023`       |
| [0.14.0](#v0140)       | `07 February 2023`  |
| [0.13.0](#v0130)       | `21 October 2022`   |
| [0.12.1](#v0121)       | `22 August 2022`    |
| [0.12.0](#v0120)       | `12 August 2022`    |
| [0.11.0](#v0110)       | `09 May 2022`       |
| [0.10.0](#v0100)       | `27 April 2022`     |
| [0.9.0](#v090)         | `05 April 2022`     |
| [0.8.0](#v080)         | `03 March 2022`     |
| [0.7.0](#v070)         | `22 September 2021` |
| [0.6.0](#v060)         | `06 April 2021`     |
| [0.5.0](#v050)         | `01 February 2021`  |
| [0.4.0](#v040)         | `12 January 2021`   |
| [0.3.0](#v030)         | `10 November 2020`  |
| [0.2.0](#v020)         | `08 October 2020`   |
| [0.1.0](#v010)         | `07 September 2020` |

---

NOTE: This library is not yet stable, and breaking changes should be expected until a 1.0.0 release.

---

## [0.17.1]

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

---

## [0.17.0]

### Breaking Changes

* None.

### New Features

* Updated to evolve-grpc 0.26.0.
* Updated super-pom to version 0.30.0
* Added `connectWithIdentity()` for connecting using Azure Managed Identities.

### Enhancements

* Update docusaurus version and the configuration.

### Fixes

* None.

### Notes

* None.

---

## [0.16.0]

### Breaking Changes

* Deprecated old style accessors in favour of Kotlin accessors for `SinglePhaseKind`. To use the new function make the folloiwng modification to your code:
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
* Feeder directions are now stopped at substation transformers in the same way as assigning equipment incase the feeder has no breaker, or the start point is
  not inline.

### Notes

* Deprecated setting `ratedS` on PowerTransformerEnd.

---

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

---

## [0.14.0]

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

---

### v0.13.0

##### Breaking Changes

* Replaced `Equipment().currentFeeders` with `Equipment().currentContainers`, which yields a `Collection<EquipmentContainer>` instead of a `Collection<Feeder>`.
* Changed the `AssignToFeeders` trace to stop at and exclude LV equipment, which should now be added to the new `LvFeeder` object.
* Renamed `Tracing().assignEquipmentContainerToFeeders` to `Tracing().assignEquipmentToFeeders`
* Changed the minimum supported database version to v43.

##### New Features

* Added support for YYN single phase transformers when determining phases.
* Added `LvFeeder`, a branch of LV network starting at a distribution substation and continuing until the end of the LV network.
* Added the following optional arguments to `NetworkConsumerClient().getEquipment(For)Container(s)`:
    * `includeEnergizingContainers`: Specifies whether to include equipment from containers energizing the ones listed in
      `mRIDs`. This is of the enum type `IncludedEnergizingContainers`, which has three possible values:
        * `EXCLUDE_ENERGIZING_CONTAINERS`: No additional effect (default).
        * `INCLUDE_ENERGIZING_FEEDERS`: Include HV/MV feeders that power LV feeders listed in `mRIDs`.
        * `INCLUDE_ENERGIZING_SUBSTATIONS`: In addition to `INCLUDE_ENERGIZING_FEEDERS`, include substations that energize a HV/MV feeder listed in `mRIDs` or
          included via `INCLUDE_ENERGIZING_FEEDERS`.
    * `includeEnergizedContainers`: Specifies whether to include equipment from containers energized by the ones listed in
      `mRIDs`. This is of the enum type `IncludedEnergizedContainers`, which has three possible values:
        * `EXCLUDE_ENERGIZED_CONTAINERS`: No additional effect (default).
        * `INCLUDE_ENERGIZED_FEEDERS`: Include HV/MV feeders powered by substations listed in `mRIDs`.
        * `INCLUDE_ENERGIZED_LV_FEEDERS`: In addition to `INCLUDE_ENERGIZED_FEEDERS`, include LV feeders that are energizes by a HV/MV feeder listed in `mRIDs`
          or
          included via `INCLUDE_ENERGIZED_FEEDERS`.
* Added `FindSwerEquipment` class which can be used for finding the SWER equipment in a `NetworkService` or `Feeder`.

##### Enhancements

* None.

##### Fixes

* Failure when reading in database tables will now cause a short-circuit failure when all tables are loaded rather than after post load processing.
* Corrected function that calculates the equivalent impedance of a transformer from the results of a short circuit test.

##### Notes

* None.

---

### v0.12.0

##### Breaking Changes

* Changed `TestNetworkBuilder` to have a public constructor and removed `startWith*` functions. Change `TestNetworkBuilder.startWith*` to
  `TestNetworkBuilder().from*` _(kotlin)_, or `new TestNetworkBuilder().from*` _(java)_.
* Changed `TestNetworkBuilder.fromOther` and `TestNetworkBuilder.toOther` to take a creator, rather than an instance.
* Modified `ConnectedEquipmentTrace` in the following ways:
    * Moved from package `com.zepben.evolve.services.network.tracing` to `com.zepben.evolve.services.network.tracing.connectivity`.
    * Changed to process a `ConnectedEquipmentStep`, rather than `ConductingEquipment` directly. `ConnectedEquipmentStep` stores both the
      `ConductingEquipment`, plus the number of steps taken from the starting object.
    * Changed the return types from `BassicTraversal<ConductingEquipment>` to `ConnectedEquipmentTraversal` which has a helper method for starting the trace
      directly from `ConductingEquipment`
    * Updated to use `ConnectedEquipmentStepTracker` which allows revisiting `ConductingEquipment` if a shorter path is found.

##### New Features

* Added `LimitedConnectedEquipmentTrace`, which can be used to trace from `ConductingEquipment` limiting by the number of steps, and optionally, feeder
  direction.

##### Enhancements

* `TestNetworkBuilder.build()` will now assign equipment to feeders if feeders were added to the network.

##### Fixes

* `TreeNode().sortWeight` no longer throws an error when evaluated on nodes for equipment without terminals.
* `PhaseStepTracker` now reports strict subsets of visited phases as visited.

##### Notes

* None.

---

### v0.12.0

##### Breaking Changes

* Changed `TestNetworkBuilder` to have a public constructor and removed `startWith*` functions. Change `TestNetworkBuilder.startWith*` to
  `TestNetworkBuilder().from*` _(kotlin)_, or `new TestNetworkBuilder().from*` _(java)_.
* Changed `TestNetworkBuilder.fromOther` and `TestNetworkBuilder.toOther` to take a creator, rather than an instance.
* Modified `ConnectedEquipmentTrace` in the following ways:
    * Moved from package `com.zepben.evolve.services.network.tracing` to `com.zepben.evolve.services.network.tracing.connectivity`.
    * Changed to process a `ConnectedEquipmentStep`, rather than `ConductingEquipment` directly. `ConnectedEquipmentStep` stores both the
      `ConductingEquipment`, plus the number of steps taken from the starting object.
    * Changed the return types from `BassicTraversal<ConductingEquipment>` to `ConnectedEquipmentTraversal` which has a helper method for starting the trace
      directly from `ConductingEquipment`
    * Updated to use `ConnectedEquipmentStepTracker` which allows revisiting `ConductingEquipment` if a shorter path is found.

##### New Features

* Added `LimitedConnectedEquipmentTrace`, which can be used to trace from `ConductingEquipment` limiting by the number of steps, and optionally, feeder
  direction.

##### Enhancements

* `TestNetworkBuilder.build()` will now assign equipment to feeders if feeders were added to the network.

##### Fixes

* `TreeNode().sortWeight` no longer throws an error when evaluated on nodes for equipment without terminals.
* `PhaseStepTracker` now reports strict subsets of visited phases as visited.

##### Notes

* None.

---

### v0.11.0

##### Breaking Changes

* Made `Terminal` methods `connect` and `disconnect` internal as they were always meant to be. If you have been incorrectly using them, you will need to swap to
  using the methods from `NetworkService` instead.

##### New Features

* None.

##### Enhancements

* Added `connectedTerminals` and `otherTerminals` helper methods to `Terminal`.

##### Fixes

* None.

##### Notes

* None.

---

### v0.10.0

##### Breaking Changes

* None.

##### New Features

* None.

##### Enhancements

* None.

##### Fixes

* `SetPhases` now supports setting backwards through XN/XY transformers.

##### Notes

* None.

---

### v0.9.0

##### Breaking Changes

* None.

##### New Features

* None.

##### Enhancements

* Phases are now set through transformers allowing for phase changes. Only valid phase mappings are currently supported. If a valid mapping has been missed,
  please contact Zepben.
* You can now translate `TracedPhases` to a `PhaseCode` if the individual phases make sense.
* Added `PhaseCode` helper methods `forEach`, `map`, `any` and `all`.

##### Fixes

* `SetDirection` now correctly handles `Switch` instances without phases.
* `SetPhases` no longer removes phases when there is an LV loop with mixed single and 3-phase.

##### Notes

* None.

---

### v0.8.0

##### Breaking Changes

* Changed the `FindWithUsagePoints` trace to handle changes to the LV aggregation via virtual `UsagePoint` instances.
* Updated database version.
* `TownDetail` fields are now nullable.
* Renamed `PhaseDirection` to `FeederDirection`:
    * `IN` renamed to `UPSTREAM`
    * `OUT` renamed to `DOWNSTREAM`
* Separated feeder direction from phase.
    * Direction has been removed from `TracedPhases` and is now accessed directly off the `Terminal`.
    * Direction has been removed from `PhaseStatus` and is now accessed via `DirectionStatus`.
* The following `Terminal` fields are now Kotlin properties rather than accessor functions:
    * `connectivityNodeId`
    * `isConnected`
    * `normalPhases`
    * `currentPhases`
* `PhaseCode.singlePhases` is now a Kotlin read-only property rather than an accessor function.

##### New Features

* Added the following CIM classes/enums:
    * `TransformerConstructionKind`
    * `TransformerFunctionKind`
    * `StreetDetail`
* Added `TestNetworkBuilder` which can be used to create simple test networks.

##### Enhancements

* Added fields to `PowerTransformer` to define `constructionKind` and `function`.
* Added fields to `StreetAddress` to define `poBox` and `streetDetail`.
* Added fields to `EnergySource` to allow representation of a higher-level power grid connection modelled as a slack bus.
* Reworked phase connectivity to better handle unknown primary phases (X/Y).

##### Fixes

* Column names fixed for `TableBaseVoltages.NOMINAL_VOLTAGE`, `TableRemoteSources.MEASUREMENT_MRID`, and `TableControls.CONTROL_MRID` - database upgrade path
  provided.
* Relaxed the constraint on `power_electronics_connection_mrid` (no longer unique) in the following tables:
    * `battery_unit`
    * `photo_voltaic_unit`
    * `power_electronics_wind_unit`
    * `power_electronics_connection_phase`

##### Notes

* None.

---

### v0.7.0

##### Breaking Changes

* `NetworkHierarchy` has been updated to use CIM classes.
* `getFeeder` has been deprecated. Prefer the use of the more generic `getEquipmentContainer`.
* `getIdentifiedObject` now returns a `NoSuchElementException` error if the requested object does not exist rather than a `null` successful result.
* All `CimConsumerClient` implementations have been changed to control a single service rather than one per call.
* `NetworkConsumerClient` has been updated to use the async stub internally and has received a significant performance boost. This only has an impact if you are
  passing the stub directly to the client rather than a channel.
* All SDK methods that retrieve objects with references will now request the network hierarchy first to provide a consistent result, regardless of call order.
* Primitive values on CIM classes are now nullable to allow differentiation between missing and zero values.
* Fixed typo in `NetworkServiceComparator`. Was previously `NetworkServiceCompatatorOptions`.
* Changed `DiagramObject.style` to be a string and removed `DiagramObjectStyle` enum.
* Updated to use v0.15.0 gRPC protocols.
* Moved `ConnectivityResult` from package `com.zepben.evolve.services.network.tracing` to
  `com.zepben.evolve.services.network.tracing.connectivity`.

##### New Features

* Added normal and current version of the connected equipment trace. See `Tracing` for details.
* Added a generic API call, `getEquipmentContainer`, for populating the `Equipment` of an
  `EquipmentContainer`.
* Added API calls for getting loops.
* Added `RunStreaming` examples to the tests, which can be used to talk to a real server.
* Added the following CIM classes:
    * `EquivalentBranch`
    * `EquivalentEquipment`
    * `NoLoadTest`
    * `OpenCircuitTest`
    * `ShortCircuitTest`
    * `ShuntCompensatorInfo`
    * `TransformerTest`
* Objects that belong to a parent object will now be automatically assigned if the parent is null when added. e.g. a `Terminal`
  with no `ConductingEquipment` will be automatically associated to any `ConductingEquipment` to which it is added.
* Added a helper function to `Breaker` to identify if it is the head of a `Feeder`.
* Added `isVirtual` and `connectionCategory` to `UsagePoint`
* Created new traces for tracing connectivity using `ConnectivityResult`. These are available via `Tracing`.

##### Enhancements

* `NetworkHierarchy` now contains circuits and loops.
* Speed of `BaseService.get` has been improved when an explict type is not provided.
* `getIdentifiedObjects` can now take a `Sequence` of mRIDs in addition to an `Iterable` for performance reasons.
* `GrpcChannel` now attempts to clean up channels that fail to shut down.

##### Fixes

* `getIdentifiedObjects` now adds unknown mRIDs to the failed collection.
* Fixed an error in the typing for `GrpcResult` that allowed you to set a value to `null` without specifying a nullable type.
* Fixed errors in the decoding fromm protobuf of optional reference mRIDs that were not assigned.
* Corrected string conversion of `ServiceDifferences` with `NameType` differences.

##### Notes

* None.

---

### v0.6.0

##### Breaking Changes

* `GrpcChannelFactory.create()` now returns a `GrpcChannel`, which is a wrapper around a `Channel` or `ManagedChannel`. This should only be breaking for Java
  users.
* `BatteryUnit` `ratedE` and `storedE` are now `Long` instead of `Double`.
* The package for `DownstreamTree` has been changed from `*.tracing` to `*.tracing.tree`, you will need to reimport.
* `DownstreamTree.TreeNode` has been moved to a top level class in the `*.tracing.tree` package.
* Converted the remaining tracing classes to Kotlin. This will require fixing of many getter calls to include a `get*` from Java and removal of the function
  call in Kotlin etc.

##### New Features

* New class `GrpcChannel` that can be used in try-with-resources blocks when communicating with a Grpc server.

* NetworkConsumerClient has 4 new functions:
    - For fetching equipment for an EquipmentContainer

          getEquipmentForContainer(service: NetworkService, mrid: String)         
    - For fetching current equipment for a Feeder

          getCurrentEquipmentForFeeder(service: NetworkService, mrid: String)         
    - For fetching equipment for an OperationalRestriction

          getEquipmentForRestriction(service: NetworkService, mrid: String)         
    - For fetching terminals for a ConnectivityNode

          getTerminalsForConnectivityNode(service: NetworkService, mrid: String)         

* `NetworkConsumerClient.get_feeder()` now resolves all references, and thus you can expect to receive a Feeder with all equipment and their associations
  populated.

* New class `SwitchStateClient` that allows you to set the current state of switches via a gRPC service.

* BaseService has two new functions which allow retrieving the UnresolvedReferences for an mRID by either `toMrid` or `from.mRID`:
    - getUnresolvedReferencesTo(mRID: String): Sequence<UnresolvedReference<*,*>>
    - getUnresolvedReferencesFrom(mRID: String): Sequence<UnresolvedReference<*,*>>

* Added the following classes:
    * `LoadBreakSwitch`
    * `TransformerEndInfo`
    * `TransformerTankInfo`
    * `TransformerStarImpedance`
    * `Name`
    * `NameType`

* New class `JwtCredentials` added to support managing OAuth2 refresh tokens from Auth0. To be using with the gRPC Consumer and Producer clients which now also
  accept `JwtCredentials` or other `CallCredentials` at initialisation to apply to the stub.

##### Enhancements

* Added some better testing mechanism for database upgrades.
* Instantiating database reader / writer classes is now faster. (Mainly benefits tests)

##### Fixes

* `Resolvers.powerElectronicsConnection(powerElectronicsUnit: PowerElectronicsUnit).`
  `Resolvers.powerElectronicsConnection(powerElectronicsConnectionPhase: PowerElectronicsConnectionPhase)`
  `Resolvers.powerElectronicsUnit(powerElectronicsConnection: PowerElectronicsConnection)`
  `Resolvers.powerElectronicsConnectionPhase(powerElectronicsConnection: PowerElectronicsConnection)`
  now all have their reverse resolver assigned.

##### Notes

* Name and NameType classes enable the ability to give IdentifiedObjects multiple names.

---

### v0.5.0

##### Breaking Changes

None.

##### New Features

* Added the following classes:
    * `BatteryUnit`
    * `BusbarSection`
    * `PhotoVoltaicUnit`
    * `PowerElectronicsConnection`
    * `PowerElectronicsConnectionPhase`
    * `PowerElectronicsWindUnit`

##### Enhancements

* None.

##### Fixes

* None.

##### Notes

* None.

---

### v0.4.0

##### Breaking Changes

* Updated logic for the PhaseInferrer. When trying to fix missing phases it will first check if it has nominal phase data available and use it before trying to
  infer the phase.
* The `AssignToFeeders` was changed to assign all `ConductingEquipment` on the `normalHeadTerminal` side
  (inclusive) stopping at open points, head equipment for other feeders and substation transformers.
* You can no longer run `AssignToFeeders` directly on a feeder.
* `getIdentifiedObjects` in the consumer clients now returns a `GrpcResult` of a `MultiObjectResult`, which includes the map of objects added plus a new field
  `failed` which is the set of mRIDs that were not added to the service because another object with the same `mRID` already existed.
* `getFeeder` in `NetworkConsumerClient` now returns a `GrpcResult` as per `getIdentifiedObjects`
* `addFromPb` now return nullable types. They will return null when adding to the service returns `false`.
* `NetworkProtoToCim.toCim(ConnectivityNode)` will no longer update an existing ConnectivityNode if it already exists in the service. This brings the
  serialisation into line with all other `toCim()` functions, however means that deserialising the same `ConnectivityNode` multiple times will now fail rather
  than merging the `ConnectivityNode`s.
* `Equipment` is now added to a `Feeder` even if the phasing is broken. Side effect of this is un-ganged normal switching will no longer assign feeders
  correctly.
* Packages have been reworked, you will need to update your imports.
* Removed obsolete `PositionPointParser`.
* Improved connectivity when using XY phasing.
* Default value for transformerUtilisation property in PowerTransformer class was updated from 0.0 to NaN.
* Added PowerTransformerInfo class.
* Updated PowerTransformer's assetInfo property to hold instance of PowerTransformerInfo class.

##### New Features

* None.

##### Enhancements

* Updated `Feeder` to allow for updating the `normalHeadTerminal` if the feeder has no equipment assigned.
* Added transformer utilisation property to PowerTransformer class
* PhaseInferrer now supports Neutral phase.

##### Fixes

* None.

##### Notes

* None.

---

### v0.3.0

##### Breaking Changes

* `tryGrpc` no longer throws. Helper methods have been added to `GrpcResult` providing convenient ways of maintaining the previous functionality.

##### New Features

* Added `getFeeder` helper to the `NetworkConsumerClient`.
* Added `customer` and `diagram` consumer clients.
* Added `MetadataCollection` allowing you to specify `DataSource` information.

##### Enhancements

* The gRPC consumer and producer clients now share a common base class.
* Added primaryVoltage property to PowerTransformer for convenience.

##### Fixes

* Fixed null annotations on `ConnectivityResult`.

##### Notes

* None.

---

### v0.2.0

##### Breaking Changes

* `ConnectivityResult` is now immutable.

##### New Features

* None.

##### Enhancements

* Tweaked error handling in the `put` package.

##### Fixes

* Database upgrade now takes its backup before changing the version table to change sets.
* `RemovePhases` now supports SWER.
* `FindWithUsagePoints` now supports first terminal sequence numbers greater than one.

##### Notes

* None.

---

### v0.1.0

Initial release containing the Zepben CIM profile, database [de]serialisation, protobuf [de]serialisation, network model tracing, and gRPC producer clients.
