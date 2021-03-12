### v0.6.0

##### Breaking Changes
* `GrpcChannelFactory.create()` now returns a `GrpcChannel`, which is a wrapper around a `Channel` or `ManagedChannel`. This should only be breaking for Java users.
* `BatteryUnit` `ratedE` and `storedE` are now `Long` instead of `Double`

##### New Features
* New class `GrpcChannel` that can be used in try-with-resources blocks when communicating with a Grpc server.
  
* NetworkConsumerClient has 4 new functions:
    - For fetching equipment for an EquipmentContainer

          getEquipmentForContainer(service: NetworkService, mrid: String)         
    - For fetching current equipment for a Feeder

          getCurrentEquipmentForFeeder(service: NetworkService, mrid: String)         
    - For fetching equipment for an OperationalRestriction

          getEquipmentForRestriction(service: NetworkService, mrid: String)         
    - For fetching terminals for a ConnectivityNode

          getTerminalsForConnectivityNode(service: NetworkService, mrid: String)         

* `NetworkConsumerClient.get_feeder()` now resolves all references, and thus you can expect to receive a Feeder with all equipment and their associations populated.

* New class `SwitchStateClient` that allows you to set the current state of switches via a gRPC service.

* BaseService has two new functions which allow retrieving the UnresolvedReferences for an mRID by either `toMrid` or `from.mRID`: 
  - getUnresolvedReferencesTo(mRID: String): Sequence<UnresolvedReference<*,*>>
  - getUnresolvedReferencesFrom(mRID: String): Sequence<UnresolvedReference<*,*>>

* Added the following classes:
    * LoadBreakSwitch
    * TransformerEndInfo
    * TransformerTankInfo
    * TransformerStarImpedance
    
##### Enhancements
* Added some better testing mechanism for database upgrades.

##### Fixes
* `Resolvers.powerElectronicsConnection(powerElectronicsUnit: PowerElectronicsUnit).` 
  `Resolvers.powerElectronicsConnection(powerElectronicsConnectionPhase: PowerElectronicsConnectionPhase)` 
  `Resolvers.powerElectronicsUnit(powerElectronicsConnection: PowerElectronicsConnection)` 
  `Resolvers.powerElectronicsConnectionPhase(powerElectronicsConnection: PowerElectronicsConnection)` 
  now all have their reverse resolver assigned.
  
##### Notes
* None.
