## [0.14.0]

### Breaking Changes

* Removed `GrpcChannelFactory`. The connect methods in the `Connect` object should be used instead. `GrpcChannelBuilder` may be used if more customisation is
  required (e.g. TLS client authentication).
* Renamed `FeederDirection.has` to `FeederDirection.contains`, which can be used via its operator version `in`. e.g. `BOTH.has(DOWNSTREAM)` can be replaced with
  `BOTH.contains(DOWNSTREAM)` or `DOWNSTREAM in BOTH`
* Removed deprecated function `NetworkConsumerClient.getFeeder`.

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
* Added the following functions to `ConnectedEquipmentTrace` for creating traces that work on `ConductingEquipment`, and ignore phase connectivity, instead
  considering things to be connected if they share a `ConnectivityNode`:
    * `newNormalDownstreamEquipmentTrace`: Creates a trace that traverses in the downstream direction using the normal state of the network.
    * `newNormalUpstreamEquipmentTrace`: Creates a trace that traverses in the upstream direction using the normal state of the network.
    * `newCurrentDownstreamEquipmentTrace`: Creates a trace that traverses in the downstream direction using the current state of the network.
    * `newCurrentUpstreamEquipmentTrace`: Creates a trace that traverses in the upstream direction using the current state of the network.
* Added support for protection equipment with the following classes, enums, and fields:
  * `SwitchInfo`: Switch datasheet information.
  * `ProtectionEquipment`: An electrical device designed to respond to input conditions in a prescribed manner and after specified conditions are met to cause
                           contact operation or similar abrupt change in associated electric control circuits, or simply to display the detected condition.
  * `CurrentRelay`: A device that checks current flow values in any direction or designated direction.
  * `CurrentRelayInfo`: Current relay datasheet information.
  * `RecloseSequence`: A reclose sequence (open and close) is defined for each possible reclosure of a breaker.
  * `ProtectionKind`: The kind of protection being provided by this protection equipment.
  * `ProtectedSwitch::breakingCapacity`: The maximum fault current in amps a breaking device can break safely under prescribed conditions of use.
  * `Switch::ratedCurrent`: The maximum continuous current carrying capacity in amps governed by the device material and construction.
                            The attribute shall be a positive value.
  * `Breaker::inTransitTime`: The transition time from open to close in seconds.

### Enhancements

* Added `FeederDirection.not` operator function.

### Fixes

* `NetworkService.remove` for `LvFeeder` function fixed (previously added instead).
* Added confirmation of removal to the remove component test.
* Fixed bug where limited connected traces with `maximumSteps = 1` could include equipment 2 steps away when using a direction.
* Made `type` column of `potential_transformers` non-null.

### Notes

* None.
