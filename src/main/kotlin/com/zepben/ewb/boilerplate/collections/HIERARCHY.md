# Collection hierarchy

This package provides collection views over backing fields used by the CIM
model. Italicised names are structural bridge classes: production code does
not instantiate them directly or expose properties typed as them.

## Collection classes

- *[AbstractBackedCollection](#abstract-backed-collection)* — extends
  `AbstractMutableCollection<T>`
  - *[AbstractBackedList](#abstract-backed-list)*
    - [LazyList](#lazy-list)
      - [LazyIndexList](#lazy-index-list)
  - [MridCollection](#mrid-collection)
    - *[MridBackfillCollection](#mrid-backfill-collection)*
      - *[AbstractMridList](#abstract-mrid-list)* — also implements `List<T>`
        - [LazyMridList](#lazy-mrid-list)
        - [MridList](#mrid-list)
      - [LazyMridMap](#lazy-mrid-map)

## Iterator classes

- [VolatileIterator](#volatile-iterator) — implements `MutableIterator<T>`
  - [CallbackMutableIterator](#callback-mutable-iterator)

## Nested implementation classes

- [CallbackMutableIterator](#callback-mutable-iterator)
  - [CallbackMutableIterator.Current](#callback-mutable-iterator-current)

<a id="abstract-backed-collection"></a>

## AbstractBackedCollection

Centralises `MutableCollection` delegation for contents stored elsewhere. It
applies optional validation during addition and provides storage and removal
hooks used by the specialised collection branches.

<a id="abstract-backed-list"></a>

## AbstractBackedList

Adds indexed reads and optional sorting to `AbstractBackedCollection`. It
implements the lightweight `ArcList` interface, which combines the `add`,
`remove`, and `clear` operations of `MutableCollection` with the indexed reads
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

<a id="volatile-iterator"></a>

## VolatileIterator

Is the public iterator type returned by `AbstractBackedCollection`. Traversal
is ordinary, but `remove` is deprecated because an iterator can remain attached
to an old backing instance after nullable storage is replaced.

<a id="callback-mutable-iterator"></a>

## CallbackMutableIterator

Wraps a backing mutable iterator and invokes collection-specific cleanup after
a successful iterator removal. It is internal and is instantiated by
`AbstractBackedCollection.iterator()`.

<a id="callback-mutable-iterator-current"></a>

## CallbackMutableIterator.Current

Holds the most recently returned element so `CallbackMutableIterator` can pass
the removed value to its cleanup callback, including when that value is
`null`.
