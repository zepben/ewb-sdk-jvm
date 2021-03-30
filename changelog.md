### v0.6.0

##### Breaking Changes
* `GrpcChannelFactory.create()` now returns a `GrpcChannel`, which is a wrapper around a `Channel` or `ManagedChannel`. This should only be breaking for Java
  users.
* `BatteryUnit` `ratedE` and `storedE` are now `Long` instead of `Double`.
* The package for `DownstreamTree` has been changed from `*.tracing` to `*.tracing.tree`, you will need to reimport.
* `DownstreamTree.TreeNode` has been moved to a top level class in the `*.tracing.tree` package.
* Converted the remaining tracing classes to Kotlin. This will require fixing of many getter calls to include a `get*` from Java and removal of the function
  call in Kotlin etc.

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
    * Name
    * NameType
    
* New class `JwtCredentials` added to support managing OAuth2 refresh tokens from Auth0. To be using with the gRPC Consumer and Producer clients which now
also accept `JwtCredentials` or other `CallCredentials` at initialisation to apply to the stub.

##### Enhancements
* Added some better testing mechanism for database upgrades.
* Instantiating database reader / writer classes is now faster. (Mainly benefits tests)

##### Fixes
* `Resolvers.powerElectronicsConnection(powerElectronicsUnit: PowerElectronicsUnit).` 
  `Resolvers.powerElectronicsConnection(powerElectronicsConnectionPhase: PowerElectronicsConnectionPhase)` 
  `Resolvers.powerElectronicsUnit(powerElectronicsConnection: PowerElectronicsConnection)` 
  `Resolvers.powerElectronicsConnectionPhase(powerElectronicsConnection: PowerElectronicsConnection)` 
  now all have their reverse resolver assigned.
  
##### Notes
* Name and NameType classes enable the ability to give IdentifiedObjects multiple names.
