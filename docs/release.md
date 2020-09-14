#### Release History

| Version | Released |
| --- | --- |
| [0.2.0](#v020) | `TBD` |
| [0.1.0](#v010) | `07 September 2020` |

---

NOTE: This library is not yet stable, and breaking changes should be expected until
a 1.0.0 release.

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

Initial release containing the Zepben CIM profile, database [de]serialisation,
protobuf [de]serialisation, network model tracing, and gRPC producer clients.

