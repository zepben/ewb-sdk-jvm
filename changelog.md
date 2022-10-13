### v0.13.0-SNAPSHOT1

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
    * `INCLUDE_ENERGIZED_LV_FEEDERS`: In addition to `INCLUDE_ENERGIZED_FEEDERS`, include LV feeders that are energizes by a HV/MV feeder listed in `mRIDs` or
      included via `INCLUDE_ENERGIZED_FEEDERS`.
* Added `FindSwerEquipment` class which can be used for finding the SWER equipment in a `NetworkService` or `Feeder`.

##### Enhancements
* None.

##### Fixes
* Failure when reading in database tables will now cause a short-circuit failure when all tables are loaded rather than after post load processing.

##### Notes
* None.
