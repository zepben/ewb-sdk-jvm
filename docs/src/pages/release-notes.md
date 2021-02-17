#### Release History

| Version | Released |
| --- | --- |
|[0.5.0](#v050)| `01 February 2021` |
|[0.4.0](#v040)| `12 January 2021` |
|[0.3.0](#v030)| `10 November 2020` |
| [0.2.0](#v020) | `08 October 2020` |
| [0.1.0](#v010) | `07 September 2020` |

---

NOTE: This library is not yet stable, and breaking changes should be expected until a 1.0.0 release.

---

### v0.5.0

##### Breaking Changes
 None.

##### New Features
* Added the following classes:
  * BatteryUnit
  * BusbarSection
  * PhotoVoltaicUnit
  * PowerElectronicsConnection
  * PowerElectronicsConnectionPhase
  * PowerElectronicsWindUnit

##### Enhancements
* None.

##### Fixes
* None.

##### Notes
* None.

---

### v0.4.0

##### Breaking Changes
* Updated logic for the PhaseInferrer. When trying to fix missing phases it will first check if it has nominal phase data available and use it before trying to infer the phase.
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

