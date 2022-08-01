### v0.12.0

##### Breaking Changes

* Changed `TestNetworkBuilder` to have a public constructor and removed `startWith*` functions. Change `TestNetworkBuilder.startWith*` to
  `TestNetworkBuilder().from*` _(kotlin)_, or `new TestNetworkBuilder().from*` _(java)_.
* Changed `TestNetworkBuilder.fromOther` and `TestNetworkBuilder.toOther` to take a creator, rather than an instance.

##### New Features

* None.

##### Enhancements

* `TestNetworkBuilder.build()` will now assign equipment to feeders if feeders were added to the network.

##### Fixes

* `TreeNode().sortWeight` no longer throws an error when evaluated on nodes for equipment without terminals.
* `PhaseStepTracker` now reports strict subsets of visited phases as visited.

##### Notes

* None.
