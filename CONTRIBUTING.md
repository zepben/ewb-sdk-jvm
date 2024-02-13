## Contributing ##

Contributions are always welcome, however at a minimum must meet the following conditions:

1. All new classes should be written in Kotlin. Java code will only be accepted if it's a minor addition to an existing
   java file. Conversion of java code to Kotlin is welcome, but will only be accepted if it's relevant to the change being made.
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
- Tabsize of 4
- Spaces instead of tabs
- Continuation line indent of 4
- Line wrap at 160 characters

## Checklist for model change ##

1. Update `pom.xml` to import the correct version of `evolve-grpc`.
2. Model updated and tested.
3. Add/remove methods added to *Service class if new class was added.
4. Descriptions copied from CIM and added as doc comments to new changes (on class, property etc)
5. `FillFields.kt` updated to populate data for tests. Utilise `includeRuntime` if required.
6. Database:
    1. Table class(es) updated. - `com.zepben.evolve.database.sqlite.tables`
    2. New tables added to DatabaseTables - `DatabaseTables.kt`
    3. Writer updated. - `com.zepben.evolve.database.sqlite.writers`
    4. Reader updated. - `com.zepben.evolve.database.sqlite.readers`
    5. ServiceReader updated if a new class was added. - `com.zepben.evolve.database.sqlite.readers.*ServiceReader`
    6. ServiceWriter updated if a new class was added. - `com.zepben.evolve.database.sqlite.writers.*ServiceWriter`
    7. DB version updated. - `TableVersion.kt`
    8. Migration written. - `com.zepben.evolve.database.sqlite.upgrade`
        1. ChangeSet added to UpgradeRunner. - `com.zepben.evolve.database.sqlite.upgrade.UpgradeRunner`
        2. ChangeSetValidator written for ChangeSet. - `com.zepben.evolve.database.sqlite.upgrade.changesets.ChangeSet*Validator`
        3. ChangeSetValidator added to changeSetValidators. - `com.zepben.evolve.database.sqlite.upgrade.ChangeSetTest`
    9. Add schema tests - `DatabaseSqliteTest.kt`
7. Reference resolver(s) added (if new associations).
8. Protobuf/gRPC
    1. *CimToProto(s) updated (including java wrapper).
    2. *ProtoToCim(s) updated (including java wrapper).
    3. *TranslatorTest(s) updated.

NOTE: Do not update the StupidlyLargeNetwork file, this will be phased out.

1. *ServiceComparator(s) updated and tested.
2. Exhaustive when functions in *ServiceUtils updated if a new class is added. Update *ServiceUtilsTest to match.
3. Release notes updated.
