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
