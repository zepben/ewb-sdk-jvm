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

##### Notes

* None.
