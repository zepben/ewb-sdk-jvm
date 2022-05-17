### v0.12.0

##### Breaking Changes

* Changed `TestNetworkBuilder` to have a public constructor and removed `startWith*` functions. Change `TestNetworkBuilder.startWith*` to
  `TestNetworkBuilder().from*` _(kotlin)_, or `new TestNetworkBuilder().from*` _(java)_.
* Changed `TestNetworkBuilder.fromOther` and `TestNetworkBuilder.toOther` to take a creator, rather than an instance.

##### New Features

* None.

##### Enhancements

* None.

##### Fixes

* None.

##### Notes

* None.
