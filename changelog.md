### v0.10.0

##### Breaking Changes

* Made `Terminal` methods `connect` and `disconnect` internal as they were always meant to be. If you have been incorrectly using them, you will need to swap to
  using the methods from `NetworkService` instead.

##### New Features

* None.

##### Enhancements

* Added `connectedTerminals` and `otherTerminals` helper methods to `Terminal`.

##### Fixes

* `SetPhases` now supports setting backwards through XN/XY transformers.

##### Notes

* None.
