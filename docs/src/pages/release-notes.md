#### Release History

| Version | Released |
| --- | --- |
|[0.3.0](#v030)| `10 November 2020` |
| [0.2.0](#v020) | `08 October 2020` |
| [0.1.0](#v010) | `07 September 2020` |

---

NOTE: This library is not yet stable, and breaking changes should be expected until a 1.0.0 release.

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

