# Collection hierarchy

This package provides collection views over backing fields used by the CIM
model. Italicised names are structural bridge classes: production code does
not instantiate them directly or expose properties typed as them.

## Collection classes

- *[AbstractBackedCollection](#abstract-backed-collection)* ([code](./AbstractBackedCollection.kt#L22)) — implements
  `ArcCollection<T>`
  - *[AbstractBackedList](#abstract-backed-list)* ([code](./AbstractBackedList.kt#L19))
    - [LazyList](#lazy-list) ([code](./LazyList.kt#L48))
      - [LazyIndexList](#lazy-index-list) ([code](./LazyIndexList.kt#L36))
  - [MridCollection](#mrid-collection) ([code](./MridCollection.kt#L20))
    - *[MridBackfillCollection](#mrid-backfill-collection)* ([code](./MridBackfillCollection.kt#L23))
      - *[AbstractMridList](#abstract-mrid-list)* ([code](./AbstractMridList.kt#L22)) — also implements `List<T>`
        - [LazyMridList](#lazy-mrid-list) ([code](./LazyMridList.kt#L26))
        - [MridList](#mrid-list) ([code](./MridList.kt#L25))
      - [LazyMridMap](#lazy-mrid-map) ([code](./LazyMridMap.kt#L35))

<a id="abstract-backed-collection"></a>

## AbstractBackedCollection

Implements the narrow `ArcCollection` contract for contents stored elsewhere.
It exposes add, remove, and clear mutation, returns a read-only iterator, and
provides validation, storage, and post-removal cleanup hooks for specialised
branches. Its default `remove` uses the backing collection's mutable iterator;
storage-specific branches may override it when they preserve the same cleanup
lifecycle.

<a id="abstract-backed-list"></a>

## AbstractBackedList

Adds indexed reads and optional sorting to `AbstractBackedCollection`. It
implements the lightweight `ArcList` interface, which combines the `add`,
`remove`, and `clear` operations of `ArcCollection` with the indexed reads
of `List` without exposing the indexed mutation contract of `MutableList`.

<a id="lazy-list"></a>

## LazyList

Adapts a nullable mutable-list field. A `null` field is observed as an empty
list, storage is created on the first addition, and the field returns to `null`
when the collection becomes empty.

<a id="lazy-index-list"></a>

## LazyIndexList

Extends `LazyList` with explicit indexed insertion and removal without
implementing `MutableList`. Its inherited `subList` is a read-only backed view,
with the usual unspecified behaviour after structural changes to the base
list.

<a id="mrid-collection"></a>

## MridCollection

Adds lookup and uniqueness enforcement for `Identifiable` elements keyed by
mRID. This is the common public type exposed by many CIM model properties,
regardless of the concrete list or map storage used underneath.

<a id="mrid-backfill-collection"></a>

## MridBackfillCollection

Introduces the owner type and typed backfill lifecycle between
`MridCollection` and its backfill-aware implementations. Keeping this concern
in an intermediate class avoids adding an otherwise irrelevant owner type to
the widely used `MridCollection<T>` API.

<a id="abstract-mrid-list"></a>

## AbstractMridList

Combines the mRID/backfill branch with read-only `List` access and optional
sorting. It exists to share this intersection between the nullable and
non-null list implementations.

<a id="lazy-mrid-list"></a>

## LazyMridList

Stores mRID-identified elements in a nullable list while supporting typed
backfill, validation, lookup, and optional sorting. It creates backing storage
on first addition and resets it to `null` after the final removal or a clear.

<a id="mrid-list"></a>

## MridList

Stores mRID-identified elements in a non-null mutable list. It provides the
same lookup, uniqueness, backfill, validation, and sorting lifecycle as
`LazyMridList`, but retains an empty backing list after clearing.

<a id="lazy-mrid-map"></a>

## LazyMridMap

Stores elements in a nullable map keyed by mRID and exposes the map values as
a collection. It uses identity-aware membership and removal, and resets the
backing map to `null` when empty.
