## [0.14.0]

### Breaking Changes
* Removed `GrpcChannelFactory`. The connect methods in the `Connect` object should be used instead. `GrpcChannelBuilder` may be used if more customisation is
  required (e.g. TLS client authentication).

### New Features
* Added methods for connecting to the gRPC service in a utility object named `Connect`:
  * `Connect.connectInsecure`: Used to connect to a gRPC service that does not use SSL/TLS.
  * `Connect.connectTls`: Used to connect to a gRPC service that uses SSL/TLS, without user authentication.
  * `Connect.connectWithSecret`: Used to connect to a gRPC service that uses SSL/TLS, using the OAuth 2.0 client credential flow.
  * `Connect.connectWithPassword`: Used to connect to a gRPC service that uses SSL/TLS, using the OAuth 2.0 password grant flow.


### Enhancements
* None.

### Fixes
* remove lvfeeder function fixed (previously still adds)
* remove component test updated to contain another check

### Notes
* None.
