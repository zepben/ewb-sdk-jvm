#### Release History

| Version                  | Released              |
| ------------------------ | --------------------- |
| [1.1.0](#110)            | `30 September 2025`   |
| [1.0.1](#101)            | `19 September 2025`   |
| [1.0.0](#100)            | `27 August 2025`      |
| [0.29.0](#0290)          | `05 June 2025`        |
| [0.28.0](#0280)          | `28 April 2025`       |
| [0.27.0](#0270)          | `24 April 2025`       |
| [0.26.1](#0261)          | `03 April 2025`       |
| [0.26.0](#0260)          | `02 April 2025`       |
| [0.25.0](#0250)          | `04 March 2025`       |
| [0.24.1](#0241)          | `23 January 2025`     |
| [0.24.0](#0240)          | `21 January 2025`     |
| [0.23.0](#0230)          | `18 October 2024`     |
| [0.22.0](#0220)          | `30 May 2024`         |
| [0.21.0](#0210)          | `15 May 2024`         |
| [0.20.0](#0200)          | `14 May 2024`         |
| [0.19.0](#0190)          | `08 April 2024`       |
| [0.18.0](#0180)          | `08 April 2024`       |
| [0.17.1](#0171)          | `11 January 2024`     |
| [0.17.0](#0170)          | `22 November 2023`    |
| [0.16.0](#0160)          | `13 September 2023`   |
| [0.15.0](#0150)          | `01 May 2023`         |
| [0.14.0](#0140)          | `07 February 2023`    |
| [0.13.0](#0130)          | `21 October 2022`     |
| [0.12.0](#0120)          | `12 August 2022`      |
| [0.11.0](#0110)          | `09 May 2022`         |
| [0.10.0](#0100)          | `27 April 2022`       |
| [0.9.0](#090)            | `05 April 2022`       |
| [0.8.0](#080)            | `03 March 2022`       |
| [0.7.0](#070)            | `22 September 2021`   |
| [0.6.0](#060)            | `06 April 2021`       |
| [0.5.0](#050)            | `01 February 2021`    |
| [0.4.0](#040)            | `12 January 2021`     |
| [0.3.0](#030)            | `10 November 2020`    |
| [0.2.0](#020)            | `08 October 2020`     |
| [0.1.0](#010)            | `07 September 2020`   |

---

NOTE: This library is not yet stable, and breaking changes should be expected until a 1.0.0 release.

---

## [1.1.0]

### Breaking Changes
* Discrepancies in the v61 upgrade scripts and the schema creation objects have been fixed. This includes correcting the types of some columns, changing the
  nullability of some columns and adding missing indexes.
* `Timestamp.toInstant()` and `Instant.toTimestamp()` have been removed from the public API. If you were using them, you will need to copy the implementation.
* The following gRPC fields have been modified to support nulls (they were missed in v1.0.0):
  * `Document.createdDateTime`
  * `Equipment.commissionedDate`
  * `MeasurementValue.timeStamp`
  * `RelayInfo.curveSetting`
  * `RelayInfo.recloseFast`
* `NetworkCimReader` is now an `AutoClosable` to clean up local caches used when loading the database. Failure to close the reader simply means the unused
  memory will be kept indefinitely, rather than being released back to the server, but you may get code analysis errors due to this change.

### New Features
* Added the following new CIM classes:
  * `DateTimeInterval`, interval between two date and time points, where the interval includes the start time but excludes end time.
  * `ElectronicAddress`, electronic address information.
  * `TelephoneNumber`, telephone number.
* Added the following new CIM extension classes:
  * `ContactDetails`, the details required to contact a person or company. These can be accessed/used via a `UsagePoint`.
  * `DirectionalCurrentRelay`, a directional current relay is a type of protective relay used in electrical power systems to detect the direction of current
    flow and operate only when the current exceeds a certain threshold in a specified direction.
* Added new CIM extension enums:
  * `ContactMethodType`
  * `PolarizingQuantityType`
* `EquipmentTreeBuilder` will now calculate `leaves` when specified to do so via the `calculateLeaves` constructor parameter.

### Enhancements
* `BaseService.contains` has been marked as an `operator`, allowing Kotlin to use `in` against a service. It has also been expanded to support objects in
  addition to mRIDs.
* `Agreement` now supports `validityInterval`, the date and time interval the agreement is valid (from going into effect to termination).
* `StreetDetail` now supports extension `buildingNumber`, the number of the building.
* `TownDetail` now supports `country`, the name of the country.
* The `compareUnorderedValueCollection` methods in `BaseServiceComparator` and `PropertyOmpareres` have been relaxed to take any `Colleciton` rather than only
  accepting a `List`.

### Fixes
* Fixed a bug in the LV feeder assignment when processing sites in the current state of the network. This prevented LV feeders on switches (rather than the
  transformer) from being assigned to the current state energising feeder.
* Indexes have been fixed in the database schema. Many were missing due to not calling `super`, so the method of adding indexes has been reworked to prevent
  this in the future.
* `CurrentStateEvent.timestamp` now correctly clears the timestamp if you have a `null` timestamp.

### Notes
* None.

---

## [1.0.1]

### Breaking Changes
* None.

### New Features
* None.

### Enhancements
* None.

### Fixes
* Fixed a bug in the LV feeder assignment when processing sites in the current state of the network. This prevented LV feeders on switches (rather than the
  transformer) from being assigned to the current state energising feeder.

### Notes
* None.

---

## [1.0.0]

### Breaking Changes
* Renamed the package to `com.zepben.ewb`. You will need to update all your imports `com.zepben.evolve.*` -\> `com.zepben.ewb.*`. This also updates the maven
  artifact:
  ```xml
  <dependency>
      <groupId>com.zepben</groupId>
      <artifactId>ewb-sdk</artifactId>
  </dependency>
  ```
* Relocated the following classes into the Zepben extensions area, marking them as [ZBEX]:
  * `DistanceRelay`: `cim.iec61970.base.protection` -\> `cim.extensions.iec61970.base.protection`.
  * `EvChargingUnit`: `cim.iec61970.infiec61970.wires.generation.production` -\> `cim.extensions.iec61970.base.generation.production`.
  * `Loop`: `cim.iec61970.infiec61970.feeder` -\> `cim.extensions.iec61970.base.feeder`.
  * `LvFeeder`: `cim.iec61970.infiec61970.feeder` -\> `cim.extensions.iec61970.base.feeder`.
  * `PowerDirectionKind`: `cim.iec61970.infiec61970.protection` -\> `cim.extensions.iec61970.base.protection`.
  * `ProtectionKind`: `cim.iec61970.infiec61970.protection` -\> `cim.extensions.iec61970.base.protection`.
  * `ProtectionRelayFunction`: `cim.iec61970.base.protection` -\> `cim.extensions.iec61970.base.protection`.
  * `ProtectionRelayScheme`: `cim.iec61970.base.protection` -\> `cim.extensions.iec61970.base.protection`.
  * `ProtectionRelaySystem`: `cim.iec61970.base.protection` -\> `cim.extensions.iec61970.base.protection`.
  * `RelayInfo`: `cim.iec61968.infiec61968.infassetinfo` -\> `cim.extensions.iec61968.assetinfo`.
  * `RelaySetting`: `cim.iec61970.base.protection` -\> `cim.extensions.iec61970.base.protection`.
  * `Site`: `cim.iec61970.base.core` -\> `cim.extensions.iec61970.base.core`.
  * `TransformerCoolingType`: `cim.iec61970.base.wires` -\> `cim.extensions.iec61970.base.wires`.
  * `TransformerEndRatedS`: `cim.iec61970.base.wires` -\> `cim.extensions.iec61970.base.wires`.
  * `VectorGroup`: `cim.iec61970.base.wires` -\> `cim.extensions.iec61970.base.wires`.
  * `VoltageRelay`: `cim.iec61970.base.protection` -\> `cim.extensions.iec61970.base.protection`.
  * `WindingConnection`: `cim.iec61970.base.wires.winding` -\> `cim.iec61970.base.wires`.
* Relocated the following classes that were in the wrong packages:
  * `Pole`: `cim.iec61968.assets` -\> `cim.iec61968.infiec61968.infassets`.
  * `StreetlightLampKind`: `cim.iec61968.assets` -\> `cim.iec61968.infiec61968.infassets`.
  * All classes in the incorrectly located `cim.iec61970.base.wires.generation.production` -\> `cim.iec61970.base.generation.production`.
* The protobuf implementation has the following changes, which will only have an impact if you are using protobuf directly:
  * Updated the values of the following enums to conform to Protobuf standard naming:
    * `BatteryControlMode`
    * `BatteryStateKind`
    * `CustomerKind`
    * `DiagramStyle`
    * `EndDeviceFunctionKind`
    * `FeederDirection`
    * `IncludedEnergizedContainers`
    * `IncludedEnergizingContainers`
    * `LogLevel`
    * `LogSource`
    * `NetworkState`
    * `OrientationKind`
    * `PhaseCode`
    * `PhaseShuntConnectionKind`
    * `PotentialTransformerKind`
    * `PowerDirectionKind`
    * `ProtectionKind`
    * `RegulatingControlModeKind`
    * `SinglePhaseKind`
    * `StreetlightLampKind`
    * `SVCControlMode`
    * `SwitchAction`
    * `SynchronousMachineKind`
    * `TransformerConstructionKind`
    * `TransformerCoolingType`
    * `TransformerFunctionKind`
    * `UnitSymbol`
    * `VectorGroup`
    * `WindingConnection`
    * `WireMaterialKind`
  * Renumbered the protobuf fields for:
    * `AcLineSegment`
    * `Control`
    * `Diagram`
    * `TransformerEnd`
* Renamed the following enum values:
  * `PowerDirectionKind.UNKNOWN_DIRECTION` -\> `PowerDirectionKind.UNKNOWN`
  * `RegulatingControlModeKind.UNKNOWN_CONTROL_MODE` -\> `RegulatingControlModeKind.UNKNOWN`
  * `TransformerCoolingType.UNKNOWN_COOLING_TYPE` -\> `TransformerCoolingType.UNKNOWN`
  * `WindingConnection.UNKNOWN_WINDING` -\> `WindingConnection.UNKNOWN`
* Added `TransformerFunctionKind.UNKNOWN` to allow distinction between an unknown function, and a function that is not covered by the enum (i.e. `other`). This
  addition has changed the order of the enum values, with `other` now being the last entry, instead of the first.
* `NetworkConsumerClient` no longer uses the protobuf `IncludedEnergizedContainers`, `IncludedEnergizingContainers` and `NetworkState` enums directly, it now
  uses SDK versions of these enums. To use these enums you will need to update your imports, and use the simplified versions of the enum values:
  * `IncludedEnergizedContainers` from package `com.zepben.ewb.streaming.get`.
    * `EXCLUDE_ENERGIZED_CONTAINERS` -\> `NONE`.
    * `INCLUDE_ENERGIZED_FEEDERS` -\> `FEEDERS`.
    * `INCLUDE_ENERGIZED_LV_FEEDERS` -\> `LV_FEEDERS`.
  * `IncludedEnergizingContainers` from package `com.zepben.ewb.streaming.get`.
    * `EXCLUDE_ENERGIZING_CONTAINERS` -\> `NONE`.
    * `INCLUDE_ENERGIZING_FEEDERS` -\> `FEEDERS`.
    * `INCLUDE_ENERGIZING_SUBSTATIONS` -\> `SUBSTATIONS`.
  * `NetworkState` from package `com.zepben.ewb.services.network`.
    * `ALL_NETWORK_STATE` -\> `ALL`.
    * `NORMAL_NETWORK_STATE` -\> `NORMAL`.
    * `CURRENT_NETWORK_STATE` -\> `CURRENT`.
* The `evolve-conn` dependency has been incorporated into the SDK with the following package changes:
  * `com.zepben.auth` -\> `com.zepben.ewb.auth`
  * `com.zepben.evolve.conn` -\> `com.zepben.ewb.conn`
* The following CIM fields have been made nullable. Note if previously accessing these fields you will now have to handle them potentially being null.
  * Analog.positiveFlowIn
  * Document.*
  * EnergyConsumer.grounded
  * EnergySource.isExternalGrid
  * IdentifiedObject.name
  * IdentifiedObject.description
  * IdentifiedObject.numDiagramObjects
  * Meter.companyMeterId
  * NameType.description
  * Pole.classification
  * PowerSystemResource.numControls
  * RegulatingCondEq.controlEnabled
  * ShuntCompensator.grounded
  * StreetAddress.postalCode
  * StreetAddress.poBox
  * StreetDetail.*
  * SynchronousMachine.earthing
  * TapChanger.controlEnabled
  * TransformerEnd.grounded
  * UsagePoint.isVirtual

### New Features
* Added `com.zepben.ewb.issues` package including `IssuesLog`, `IssueTracker` and `IssueTrackerGroup` for tracking issues.

### Enhancements
* Upgrade ewb-grpc to 1.0.0 to support customer level load results.

### Fixes
* Fixed bug that would cause a null pointer exception when processing metrics job sources with a null metadata timestamp
* Marked some extensions properties and classes with [ZBEX] that were missing them (might still be more). In addition to the ones moved into the extensions
  package:
  * `PhaseCode.Y`
  * `PhaseCode.YN`
  * `PowerElectronicsConnection.inverterStandard`
  * `PowerElectronicsConnection.sustainOpOvervoltLimit`
  * `PowerElectronicsConnection.stopAtOverFreq`
  * `PowerElectronicsConnection.stopAtUnderFreq`
  * `PowerElectronicsConnection.invVoltWattRespMode`
  * `PowerElectronicsConnection.invWattRespV1`
  * `PowerElectronicsConnection.invWattRespV2`
  * `PowerElectronicsConnection.invWattRespV3`
  * `PowerElectronicsConnection.invWattRespV4`
  * `PowerElectronicsConnection.invWattRespPAtV1`
  * `PowerElectronicsConnection.invWattRespPAtV2`
  * `PowerElectronicsConnection.invWattRespPAtV3`
  * `PowerElectronicsConnection.invWattRespPAtV4`
  * `PowerElectronicsConnection.invVoltVarRespMode`
  * `PowerElectronicsConnection.invVarRespV1`
  * `PowerElectronicsConnection.invVarRespV2`
  * `PowerElectronicsConnection.invVarRespV3`
  * `PowerElectronicsConnection.invVarRespV4`
  * `PowerElectronicsConnection.invVarRespQAtV1`
  * `PowerElectronicsConnection.invVarRespQAtV2`
  * `PowerElectronicsConnection.invVarRespQAtV3`
  * `PowerElectronicsConnection.invVarRespQAtV4`
  * `PowerElectronicsConnection.invReactivePowerMode`
  * `PowerElectronicsConnection.invFixReactivePower`
  * `PowerTransformerEnd.ratings`
  * `RegulatingControl.ratedCurrent`
  * `Sensor.relayFunctions`
  * `UsagePoint.approvedInverterCapacity`
* QueryNetworkStateService will now correctly report processing errors in responses.

### Notes
* None.

---

## [0.29.0]

### Breaking Changes
* You can no longer register a `StepActionWithContextValue` with `Traversal.ifStopping` or `Traversal.ifNotStopping`, you must use `Traversal.addStepAction`
  instead.
* Added a new `debugLogging` and `name` parameters to the constructor of the following traces. The helper functions in `Tracing` also have these parameters,
  which defaults to `null` and `networkTrace`, meaning anyone using these wrappers will be unaffected by the change:
  * `AssignToFeeders`
  * `AssignToLvFeeders`
  * `ClearDirection`
  * `FindSwerEquipment`
  * `PhaseInferrer`
  * `RemovePhases`
  * `SetDirection`
  * `SetPhases`
* `NetworkStateOperators` has a new abstract `description`. If you are creating custom operators you will need to add it.

### New Features
* You can now pass a logger to all `Tracing` methods and `TestNetworkBuilder.build` to enable debug logging for the traces it runs. The debug logging will
  include the results of all queue and stop condition checks, and each item that is stepped on.

### Enhancements
* `NetworkTrace` now supports starting from a known `NetworkTraceStep.Path`. This allows you to force a trace to start in a particular direction, or to continue
  a follow-up trace from a detected stop point.
* Support disabling certificate verification in SDK connection helpers

### Fixes
* `QueryNetworkStateService.reportBatchStatus` now correctly sends the `Empty` response.
* Phases are now correctly assigned to the LV side of an LV2 transformer that is in parallel with a previously energised LV1 transformer.

### Notes
* None.

---

## [0.28.0]

### Enhancements
* Upgrade ewb-grpc to 0.36.0 to provide mapbox vector tile proto.

---

## [0.27.0]

### Breaking Changes
* None.

### New Features
* Added relationships between `Asset` and `PowerSystemResource` which enables linking `Equipment` to `Pole`:
  * `Asset.powerSystemResources`
  * `PowerSystemResource.assets`

### Enhancements
* Updated to ewb-grpc 0.35.0. This brings with it upgrades to the latest protobuf (4.30.2) and gRPC (1.71.0) versions.

### Fixes
* NetworkCimWriter, DiagramCimWriter, and CustomerCimWriter are now public so can be used downstream.
* `AssignToFeeders` and `AssignToLvFeeders` will now associate `PowerElectronicUnits` with their `powerElectronicsConnection` `Feeder`/`LvFeeder`.

### Notes
* None.

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

---

## [0.26.1]

### Fixes
* `NetworkTrace`/`Traversal` now correctly respects `canStopOStart` when providing multiple start items.

---

## [0.26.0]

### Breaking Changes
* None.

### New Features
* None.

### Enhancements
* Added support to `TestNetworkBuilder` for:
  * `withClamp` - Adds a clamp to the previously added `AcLineSegment`
  * `withCut` - Adds a cut to the previously added `AcLineSegment`
  * `connectTo` - Connects the previously added item, rather than having to specify it again in `connect`.

### Fixes
* The follow fixes were added to Traversal and NetworkTrace:
  * `canStopAtStartItem` now works for branching traversals.
  * Traversal start items are added to the queue before traversal starts, so that the start items honour the queue type order.
  * Stop conditions on the `NetworkTrace` now are checked based on a step type, like `QueueCondition` does, rather than by checking `canActionItem`.
  * `Cut` and `Clamp` are now correctly supported in `SetDirection` and `DirectionCondition`.
  * `NetworkTrace` now handles starting on `Cut` , `Clamp`, and `AcLineSegment` and their terminals in a explicit / sensible way.
  * `NetworkTraceStepPathProvider` now correctly handles:
    * Next paths when starting on a `Clamp` terminal.
    * Traversing AcLineSegments with single cuts or clamps.
* Added missing `@JvmOverloads` annotations to the `TestNetworkBuilder`.
* Fixes from ewb-conn-jvm 0.12.1:
  * JWTAuthenticator will now handle JwkExceptions and return 403 Unauthenticated responses.
  * JWTAuthenticator will now pass through unhandled exceptions to the caller rather than wrapping them in 500 errors.
    Exceptions now need to be handled by the caller of `authenticate()`.

### Notes
* None.

---

## [0.25.0]

### Breaking Changes
* Traversal / Tracing API has been completely rewritten. `Traversal` has a different public API and `BranchRecursiveTraversal` no longer exists.
  All traces that used to be used via the `Tracing.*` factory functions should be migrated to use the new `NetworkTrace` class instantiated from the factory
  functions in `com.zepben.evolve.services.network.tracing.networktrace.Tracing`. The `NetworkTrace` should cover all existing use cases while being easier to
  use and read. See the documentation for usage details.
* `SetDirection` now correctly applies the `BOTH` direction on all parts of the loop again, so if you were relying on the broken intermediate state, you will
  need to update your code.
* `RemovePhases` now stops at open points like the `SetPhases` counterpart. If you were relying on the bug to remove phases through open points you will now
  need to start additional traces from the other side of the open points to maintain this behaviour.
* `SetDirection` now correctly sets directions for networks with `BusbarSection`.
* `RemoveDirection` has been removed. It did not work reliably with dual fed networks with loops. You now need to clear direction using the new
  `ClearDirection` and reapply directions where appropriate using `SetDirection`.
* `FindWithUsagePoints` was deemed too use-case specific for the SDK and has been removed.
* Removal of deprecated `Terminal.tracedPhases` property. Use `Terminal.normalPhases` and `Terminal.currentPhases` instead.
* The following change have been made to `SqliteTable`:
  * It will only find columns defined in Kotlin classes, Java is no longer supported.
  * Its constructor is now internal.
  * It now inherits from the new `SqlTable`.
  * It is now in module `com.zepben.evolve.database.sqlite.common`.
* The following change have been made to `Column`:
  * Its package has changed from `com.zepben.evolve.database.sqlite.cim.tables` to `com.zepben.evolve.database.sql`.
  * Its constructor is now internal.
* All references to the following have been renamed in `com.zepben.evolve.database`. This includes full or partial copies in the names of functions,
  parameters and descriptions/documentation:
  * `save` has been renamed to `write`, so the writers now write, rather than save.
  * `load` has been renamed to `read`, so the readers now read, rather than load.
* Database readers and writers no longer have the container of the data they will read/write passed to the constructor. They now have this passed to the `read`
  or `write` method.
* The following classes and methods are now internal:
  * `BaseServiceReader`, `BaseServiceWriter`, `BaseCollectionReader`, `BaseCollectionWriter`, `BaseEntryWriter`, 
  * `CimReader`, `CimWriter`,
  * `CustomerCimReader`, `CustomerCimWriter`, `CustomerServiceReader`, `CustomerServiceWriter`, 
  * `DiagramCimReader`, `DiagramCimWriter`, `DiagramServiceReader`, `DiagramServiceWriter`, 
  * `MetadataCollectionReader`, `MetadataCollectionWriter`, `MetadataEntryReader`, `MetadataEntryWriter`, 
  * `MetricsEntryWriter`, `MetricsWriter`,
  * `NetworkServiceReader`, `NetworkServiceWriter`,
  * Extension methods in `com.zepben.evolve.database.sqlite.extensions` (e.g. `ResultSet.getNullableDouble`)
* The following classes now have internal constructors:
  * `BaseDatabaseWriter`, `CimDatabaseReader`, `CimDatabaseWriter`,
  * `CimDatabaseTables`, `BaseDatabaseTables`, `CustomerDatabaseTables`, `DiagramDatabaseTables`, `NetworkDatabaseTables`, `MetricsDatabaseTables`
* Removed `Class.getFieldExt` extension function.
* `InjectionJob.metadata` property is no longer a nullable type and is now a readonly val.
* Moved the following classes and methods from `com.zepben.evolve.database.sqlite.common` to `com.zepben.evolve.database.sql`:
  * `BaseDatabaseTables`
  * `BaseDatabaseWriter`
  * `BaseEntryWriter`
  * `MissingTableConfigException`
* Moved `MetricsDatabaseTables` and `MetricsDatabaseWriter` to `com.zepben.evolve.database.postgres.metrics`.
* `MetricsDatabaseWriter` now only supports connections to existing Postgres databases with a metrics schema already in-place. Evolve App Server will be
  responsible to create and update this schema.
  * For this reason, `METRICS` has been removed from the enum `com.zepben.evolve.database.paths.DatabaseType`.
* `AcLineSegment` supports adding a maximum of 2 terminals. Mid-span terminals are no longer supported and models should migrate to using `Clamp`.
* `Clamp` supports only adding a single terminal.
* `Cut` supports adding a maximum of 2 terminals.
* Direction aware helpers in `Condition` that didn't use the `FeederDirectionStateOperations` have been removed, with the remaining helpers now taking
  `NetworkStateOperators` as a receiver.
* `NetworkStateOperators` implements a new sub-interface `ConnectivityStateOperators`.

### New Features
* Added `ClearDirection` that clears feeder directions.
* Added new `FeederDirection.CONNECTOR` value for `Connector` equipment that are modelled only with a single terminal.
* Created a new `SqlTable` that doesn't support creating schema creation statements by default.
  * Created a new `PostgresTable` to model tables in Postgres.

### Enhancements
* The following enhancements have been made to the `TestNetworkBuilder`:
  * You can now add sites via `addSite`.
  * You can now add busbar sections natively with `fromBusbarSection` and `toBusbarSection`.
  * The prefix for generated mRIDs for "other" equipment can be specified with the `defaultMridPrefix` argument in `fromOther` and `toOther`.
  * The action block for `fromOther` now has a receiver of the created type, rather than the generic `ConductingEquipment`.
* You can now start the `AssignToFeeder` trace from a specified `Terminal` rather than all feeder heads.
* When processing feeder assignments, all LV feeders belonging to a dist substation site will now be considered energized when the site is energized by a
  feeder.
* Major speed improvements have been made for `RemovePhases` when dealing with large networks with many nested loops.
* `SetDirection` now supports networks with `BusbarSection` and will apply the `FeederDirection.CONNECTOR` value to their terminals.
* Added `connectionTestTimeoutMs` field to `GrpcBuildArgs` with a default value of `5000`. This timeout is only applied to requests made in the initial connection tests.
* Updated to ewb-grpc 0.34.1:
  * Changed AddJumperEvent to not use reserved words.
* `UpdateNetworkStateService.setCurrentStates` no longer blocks while waiting for `onSetCurrentStates` callbacks when handling the `onCompleted` request. This
  only effects the gRPC threads.
* `QueryNetworkStateClient.reportBatchStatus` can be used to send status responses for batches returned from the service via
  `QueryNetworkStateClient.getCurrentStates`.
* Tracing models with `Cut` and `Clamp` are now supported via the new tracing API.

### Fixes
* `RemovePhases` now stops at open points like the `SetPhases` counterpart.
* `AssignToFeeder` and `AssignToLvFeeder` will no longer trace from start terminals that belong to open switches.
* When finding `LvFeeders` in the `Site` we will now exclude `LvFeeders` that start with an open `Switch`
* You can now pass GrpcBuildArgs to the `connect*` helper functions when connecting to EWB. See `GrpcBuildArgs` for options.

---

## [0.24.1]

### Breaking Changes
* Added `connectionTestTimeoutMs` field to `GrpcBuildArgs` with a default value of `5000`. This timeout is only applied to requests made in the initial connection tests.
* Updated to ewb-grpc 0.34.1:
  * Changed AddJumperEvent to not use reserved words.

### Fixes
* GrpcChannelBuilder's initial connectivity test no longer fails due to a lack of permissions on a subset of services.
* Updated to latest SDK:
  - AddJumperEvent from and to changed to fromConnection and toConnection
* AddJumperEvent now uses correct protobuf classes when converting
* RemoveJumperEvent now uses correct protobuf classes when converting

---

## [0.24.0]

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

---

## [0.23.0]

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
*  Refactored `EwbDataFilePaths`: 
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

---

## [0.22.0]


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

---

## [0.21.0]


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

---

## [0.20.0]


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

---

## [0.19.0]

### Enhancements
* Update super-pom to 0.36.0 for lucene and ktor dependencies.

---

## [0.18.0]


### Breaking Changes

* Updated to super-pom version 0.34.x.
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
    * `RelayInfo.forEachDelay(action: (sequenceNumber: Int, delay: Double) -\> Unit)`
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
        * `spk.value()` -\> `spk.value`
        * `spk.maskIndex()` -\> `spk.maskIndex`
        * `spk.bitMask()` -\> `spk.bitMask`
    * Java:
        * `spk.value()` -\> `spk.getValue()`
        * `spk.maskIndex()` -\> `spk.getMaskIndex()`
        * `spk.bitMask()` -\> `spk.getBitMask()`
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

## [0.13.0]

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

## [0.12.0]

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

## [0.11.0]

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

## [0.10.0]

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

## [0.9.0]

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

## [0.8.0]

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

## [0.7.0]

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

## [0.6.0]

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
    - getUnresolvedReferencesTo(mRID: String): Sequence\<UnresolvedReference\<*,*\>\>
    - getUnresolvedReferencesFrom(mRID: String): Sequence\<UnresolvedReference\<*,*\>\>

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

## [0.5.0]

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

## [0.4.0]

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

## [0.3.0]

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

## [0.2.0]

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

## [0.1.0]

Initial release containing the Zepben CIM profile, database [de]serialisation, protobuf [de]serialisation, network model tracing, and gRPC producer clients.
