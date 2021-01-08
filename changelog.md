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