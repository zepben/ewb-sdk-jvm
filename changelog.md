### v0.12.0

##### Breaking Changes

* Changed `TestNetworkBuilder` to have a public constructor and removed `startWith*` functions. Change `TestNetworkBuilder.startWith*` to
  `TestNetworkBuilder().from*` _(kotlin)_, or `new TestNetworkBuilder().from*` _(java)_.
* Changed `TestNetworkBuilder.fromOther` and `TestNetworkBuilder.toOther` to take a creator, rather than an instance.
* Modified `ConnectedEquipmentTrace` in the following ways:
  * Moved from package `com.zepben.evolve.services.network.tracing` to `com.zepben.evolve.services.network.tracing.connectivity`.
  * Changed to process a `ConnectedEquipmentStep`, rather than `ConductingEquipment` directly. `ConnectedEquipmentStep` stores both the
    `ConductingEquipment`, plus the number of steps taken from the starting object.
  * Changed the return types from `BassicTraversal<ConductingEquipment>` to `ConnectedEquipmentTraversal` which has a helper method for starting the trace
    directly from `ConductingEquipment`
  * Updated to use `ConnectedEquipmentStepTracker` which allows revisiting `ConductingEquipment` if a shorter path is found.

##### New Features

* None.

##### Enhancements

* `TestNetworkBuilder.build()` will now assign equipment to feeders if feeders were added to the network.

##### Fixes

* `TreeNode().sortWeight` no longer throws an error when evaluated on nodes for equipment without terminals.
* `PhaseStepTracker` now reports strict subsets of visited phases as visited.

##### Notes

* None.
