## Contributing ##

Contributions are always welcome, however at a minimum must meet the following conditions:

1. All new classes should be written in Kotlin. Java code will only be accepted if it's an interop test.
2. All changes must follow the code style. For Intellij this can be imported from [here](TODO)
3. MPL headers must be added to all new code files.
4. All patches must have tests.
5. All tests must pass.

# Data model changes #

If you are planning on updating the CIM profile, to avoid unnecessary work it is advisable to discuss your requirements
with us prior to writing code. We recommend following the process documented [here](TODO) if you want to update the model.

## Development Set up ##

Simply import the maven project into your favourite IDE.

Our main style requirements are:
- 4 spaces instead of tabs
- Continuation line indent of 4 spaces
- Line wrap at 160 characters

## Checklist for model change ##

Make sure you double (or triple) check the ones listed in the "most important" list below.

1. Update `pom.xml` to import the correct version of `evolve-grpc`.
2. Model updated and tested. Pay attention to:
   1. Ensure descriptions are copied from CIM and added as doc comments.
   2. All extension classes/enums are under the `com.zepben.ewb.cim.extensions` package in their appropriate sub-package.
   3. Mark all extensions (classes, enums and properties) with `@ZBEX` and documented with `[ZBEX]`
3. Add/remove methods added to *Service class if any new classes were added.
4. `FillFields.kt` updated to populate data for tests. Utilise `includeRuntime` if required.
5. Database:
   1. Table class(es) updated. - `com.zepben.ewb.database.sqlite.cim.tables`
   2. New tables added to appropriate database table collections:
      * `com.zepben.ewb.database.sqlite.cim.customer.CustomerDatabaseTables`
      * `com.zepben.ewb.database.sqlite.cim.diagram.DiagramDatabaseTables`
      * `com.zepben.ewb.database.sqlite.cim.network.NetworkDatabaseTables`
   3. Appropriate CIM writer updated:
      * `com.zepben.ewb.database.sqlite.cim.customer.CustomerCimWriter`
      * `com.zepben.ewb.database.sqlite.cim.diagram.DiagramCimWriter`
      * `com.zepben.ewb.database.sqlite.cim.network.NetworkCimWriter`
   4. Appropriate CIM reader updated:
      * `com.zepben.ewb.database.sqlite.cim.customer.CustomerCimReader`
      * `com.zepben.ewb.database.sqlite.cim.diagram.DiagramCimReader`
      * `com.zepben.ewb.database.sqlite.cim.network.NetworkCimReader`
   5. Appropriate service writer updated if a new class was added:
      * `com.zepben.ewb.database.sqlite.cim.customer.CustomerServiceWriter`
      * `com.zepben.ewb.database.sqlite.cim.diagram.DiagramServiceWriter`
      * `com.zepben.ewb.database.sqlite.cim.network.NetworkServiceWriter`
   6. Appropriate service reader updated if a new class was added:
      * `com.zepben.ewb.database.sqlite.cim.customer.CustomerServiceReader`
      * `com.zepben.ewb.database.sqlite.cim.diagram.DiagramServiceReader`
      * `com.zepben.ewb.database.sqlite.cim.network.NetworkServiceReader`
   7. DB version updated. - `com.zepben.ewb.database.sqlite.cim.tables.tableCimVersion`
   8. Migration written. - `com.zepben.ewb.database.sqlite.cim.upgrade`
      1. ChangeSet written. - `com.zepben.ewb.database.sqlite.cim.upgrade.changesets.ChangeSet*`
      2. ChangeSet added to UpgradeRunner. - `com.zepben.ewb.database.sqlite.cim.upgrade.UpgradeRunner`
      3. Tests. - `src/test`
         * ChangeSetValidator written for ChangeSet. - `com.zepben.ewb.database.sqlite.cim.upgrade.changesets.ChangeSet*Validator`
         * ChangeSetValidator added to changeSetValidators. - `com.zepben.ewb.database.sqlite.cim.upgrade.ChangeSetTest`
   9. Add schema tests:
      * `com.zepben.ewb.database.sqlite.cim.customer.CustomerDatabaseSchemaTest`
      * `com.zepben.ewb.database.sqlite.cim.diagram.DiagramDatabaseSchemaTest`
      * `com.zepben.ewb.database.sqlite.cim.network.NetworkDatabaseSchemaTest`
6. Reference resolver(s) added (if new associations).
7. Protobuf/gRPC
   1. *CimToProto(s) updated (including java wrapper).
   2. *ProtoToCim(s) updated (including java wrapper).
   3. *TranslatorTest(s) updated.
8. *ServiceComparator(s) updated.
9. *ServiceComparatorTest(s) added for each new class and property.
10. Exhaustive when functions in *ServiceUtils updated if a new class is added. Update *ServiceUtilsTest to match.
11. Release notes updated.

NOTE: Do not update the StupidlyLargeNetwork file, this will be phased out.

### Most Important ###

These are the most important changes to pay attention to:
1. `Model updated with descriptions copied from CIM` - commonly done incorrectly.
2. `FillFields` - fills in the data that many other tests rely on to detect differences in default vs filled classes.
3. `*ServiceComparator(s) and *ServiceComparatorTest(s)` - used by many tests to validate things, many which can pass with false positive results if not done.
4. `*TranslatorTest(s)` - ensure the classes will work correctly in user code
5. `*DatabaseSchemaTest(s)` - ensure migrators will produce databases that load correctly.
6. `ChangeSetValidator` - must test upgrading existing database tables with populated data to ensure old databases will work.

## Adding support for new services ##

Include new grpc services in the list of services ```GrpcChannelBuilder.testConnection()``` uses when attempting to confirm the connectivity of newly created
grpc channels.
