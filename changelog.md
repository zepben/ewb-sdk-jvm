### v0.13.0-SNAPSHOT1

##### Breaking Changes
* Replaced `Equipment().currentFeeders` with `Equipment().currentContainers`, which yields a `Collection<EquipmentContainer>` instead of a `Collection<Feeder>`.
* Changed the `AssignToFeeders` trace to stop at and exclude LV equipment, which should now be added to the new `LvFeeder` object.
* Renamed `Tracing().assignEquipmentContainerToFeeders` to `Tracing().assignEquipmentToFeeders`
* Changed the minimum supported database version to v43.

##### New Features
* Added `LvFeeder`, a branch of LV network starting at a distribution substation and continuing until the end of the LV network.

##### Enhancements
* None.

##### Fixes
* None.

##### Notes
* None.
