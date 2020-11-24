# Evolve SDK #

The Evolve SDK contains everything necessary to communicate with a [Zepben EWB Server](TODO). See the [architecture](docs/architecture.md) documentation for more details.

## Requirements ##

- Java 11

## Building ##

Requirements to build

- JDK11 (tested with Amazon Corretto)
- Maven

To build:

    mvn package

## Checklist for model change ##
1. Model updated and tested.
2. Description copied from CIM and added as doc comment to new changes (on class, property etc)
3. FillFields.kt updated to populate data for tests.
4. Database:
    1. Table class(es) updated.
    2. Writer updated.
    3. Reader updated.
    4. DB version updated.
    5. Migration written.
5. Reference resolver(s) added (if new associations).
6. Protobuf/gRPC
    1. CimToProto updated and tested.
    2. ProtoToCim updated and tested.
7. ServiceComparator(s) updated and tested.
8. Exhaustive when functions in *ServiceUtils updated if a new class is added.
9. Release notes updated.
