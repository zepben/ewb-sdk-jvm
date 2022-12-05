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
* Added support for current transformers and power transformers with the following classes in `com.zepben.evolve.cim.*`:
  * In `com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo`:
    * `CurrentTransformerInfo`: Properties of current transformer asset.
    * `PotentialTransformerInfo`: Properties of potential transformer asset.
  * In `com.zepben.evolve.cim.iec61968.infiec61968.infcommon`:
    * `Ratio`: Fraction specified explicitly with a numerator and denominator, which can be used to calculate the quotient.
  * In `com.zepben.evolve.cim.iec61970.base.auxiliaryequipment`:
    * `CurrentTransformer`: Instrument transformer used to measure electrical qualities of the circuit that is being protected and/or monitored.
    * `PotentialTransformer`: Instrument transformer (also known as Voltage Transformer) used to measure electrical qualities of the circuit that
                              is being protected and/or monitored.
    * `PotentialTransformerKind`: The construction kind of the potential transformer. (Enum)
    * `Sensor`: This class describes devices that transform a measured quantity into signals that can be presented at displays,
                used in control or be recorded.
* Added `PowerTransformer().getEnd(Terminal)`, which gets a `PowerTransformerEnd` by the `Terminal` it's connected to.

### Enhancements
* None.

### Fixes
* remove lvfeeder function fixed (previously still adds)
* remove component test updated to contain another check
* Fixed bug where limited connected traces with `maximumSteps = 1` could include equipment 2 steps away.

### Notes
* None.
