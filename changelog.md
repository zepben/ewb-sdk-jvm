### v0.7.0

##### Breaking Changes

* `NetworkHierarchy` has been updated to use CIM classes.
* `getFeeder` has been deprecated. Prefer the use of the more generic `getEquipmentContainer`.
* `getIdentifiedObject` now returns a `NoSuchElementException` error if the requested object does not exist rather than a `null` successful result.

##### New Features

* Added normal and current version of the connected equipment trace. See `Tracing` for details.
* Added a generic API call, `getEquipmentContainer`, for populating the `Equipment` of an
  `EquipmentContainer`.
* Added API calls for getting loops.

##### Enhancements

* `NetworkHierarchy` now contains circuits and loops.

##### Fixes

* `getIdentifiedObjects` now adds unknown mRIDs to the failed collection.
* Fixed an error in the typing for `GrpcResult` that allowed you to set a value to `null` without specifying a nullable type.

##### Notes

* None.
