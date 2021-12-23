### v0.8.0

##### Breaking Changes

* Changed the `FindWithUsagePoints` trace to handle changes to the LV aggregation via virtual `UsagePoint` instances.
* Updated database version.

##### New Features

* Added the following CIM classes/enums:
  * `TransformerConstructionKind`
  * `TransformerFunctionKind`

##### Enhancements

* Added fields to `PowerTransformer` to define `constructionKind` and `function`.

##### Fixes

* Column names fixed for `TableBaseVoltages.NOMINAL_VOLTAGE`, `TableRemoteSources.MEASUREMENT_MRID`, and `TableControls.CONTROL_MRID` - database upgrade path
  provided.

##### Notes

* None.
