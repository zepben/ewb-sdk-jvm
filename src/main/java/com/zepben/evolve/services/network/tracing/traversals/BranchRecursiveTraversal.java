/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.traversals;

import com.zepben.annotations.EverythingIsNonnullByDefault;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A traversal class that allows you to branch by spawning another traversal instance.
 * I've tried to make this a reusable class, but not sure how useful it is in it's current state.
 * It's working for my current use case (set phase tracing) but may need to be rethought to meet requirements of
 * other traversals.
 * Be seriously careful if you ever plan to subclass this class. It would be extremely easy to break it's intended
 * use. Should possibly even be final...
 */
@SuppressWarnings("WeakerAccess")
@EverythingIsNonnullByDefault
public class BranchRecursiveTraversal<T> extends Traversal<T> {

    private Supplier<TraversalQueue<T>> processQueueSupplier;
    private Supplier<TraversalQueue<Traversal<T>>> branchQueueSupplier;
    private Supplier<Tracker<T>> trackerSupplier;

    private TraversalQueue<T> processQueue;
    private TraversalQueue<Traversal<T>> branchQueue;
    private Tracker<T> tracker;

    @Nullable private BranchRecursiveTraversal<T> parent;

    private QueueNext<T> queueNext;

    private Supplier<Traversal<T>> branchSupplier = defaultBranchSupplier();

    @Nullable private Consumer<T> onBranchStart;

    /**
     * Represents a consumer that takes the current item of the traversal,
     * a function to add items to the traversal queue,
     * a function to add a branch to recurse on,
     * and a supplier that can generate a new Traversal branch.
     *
     * @param <T> The type of object being traversed.
     */
    public interface QueueNext<T> {

        void accept(@Nullable T item, BranchRecursiveTraversal<T> traversal);

    }

    /**
     * Creates a new traversal.
     *
     * @param queueNext            Function that supplies the next available items to step to from the current item in the traversal.
     * @param processQueueSupplier A supplier of the item queue to be used by the traversal.
     * @param trackerSupplier      A supplier of the item tracker to be used by this traversal.
     * @param branchQueueSupplier  A supplier of the branch queue to be used by this traversal.
     */
    public BranchRecursiveTraversal(QueueNext<T> queueNext,
                                    Supplier<TraversalQueue<T>> processQueueSupplier,
                                    Supplier<Tracker<T>> trackerSupplier,
                                    Supplier<TraversalQueue<Traversal<T>>> branchQueueSupplier) {
        this(null, queueNext, processQueueSupplier, trackerSupplier, branchQueueSupplier, null);
    }

    /**
     * Creates a new traversal.
     *
     * @param queueNext            Function that supplies the next available items to step to from the current item in the traversal.
     * @param processQueueSupplier A supplier of the item queue to be used by the traversal.
     * @param trackerSupplier      A supplier of the item tracker to be used by this traversal.
     * @param branchQueueSupplier  A supplier of the branch queue to be used by this traversal.
     */
    public BranchRecursiveTraversal(QueueNext<T> queueNext,
                                    Supplier<TraversalQueue<T>> processQueueSupplier,
                                    Supplier<Tracker<T>> trackerSupplier,
                                    Supplier<TraversalQueue<Traversal<T>>> branchQueueSupplier,
                                    Consumer<T> onBranchStart) {
        this(null, queueNext, processQueueSupplier, trackerSupplier, branchQueueSupplier, onBranchStart);
    }

    /**
     * Creates a new traversal instance that represents a branch,
     * allowing you to specify a parent traversal that owns this instance.
     *
     * @param parent               The traversal that spawned this instance
     * @param queueNext            Function that supplies the next available items to step to from the current item in the traversal.
     * @param processQueueSupplier A supplier of the item queue to be used by the traversal.
     * @param trackerSupplier      A supplier of the item tracker to be used by this traversal.
     * @param branchQueueSupplier  A supplier of the branch queue to be used by this traversal.
     */
    public BranchRecursiveTraversal(@Nullable BranchRecursiveTraversal<T> parent,
                                    QueueNext<T> queueNext,
                                    Supplier<TraversalQueue<T>> processQueueSupplier,
                                    Supplier<Tracker<T>> trackerSupplier,
                                    Supplier<TraversalQueue<Traversal<T>>> branchQueueSupplier,
                                    @Nullable Consumer<T> onBranchStart) {
        this.parent = parent;
        this.queueNext = queueNext;

        this.processQueueSupplier = processQueueSupplier;
        this.branchQueueSupplier = branchQueueSupplier;
        this.trackerSupplier = trackerSupplier;

        this.processQueue = processQueueSupplier.get();
        this.branchQueue = branchQueueSupplier.get();
        this.tracker = trackerSupplier.get();

        this.onBranchStart = onBranchStart;
    }

    /**
     * Gives the ability to set a custom branch supplier.
     *
     * @param branchSupplier The branch supplier that can be used to create new branches to traverse.
     */
    public void setBranchSupplier(Supplier<Traversal<T>> branchSupplier) {
        this.branchSupplier = branchSupplier;
    }

    /**
     * @return The current branch supplier.
     */
    public Supplier<Traversal<T>> branchSupplier() {
        return branchSupplier;
    }

    /**
     * The parent instance registered with this traversal.
     *
     * @return The traversal parent.
     */
    @Nullable
    public BranchRecursiveTraversal<T> parent() {
        return parent;
    }

    /**
     * @return The queue of items that this traversal is yet to visit.
     */
    public TraversalQueue<T> queue() {
        return processQueue;
    }

    /**
     * @return The queue of branches that this traversal is yet to visit.
     */
    public TraversalQueue<Traversal<T>> branchQueue() {
        return branchQueue;
    }

    /**
     * @return The tracker used by this traversal.
     */
    @Override
    public Tracker<T> tracker() {
        return tracker;
    }

    /**
     * @return Whether the item has been visited before.
     */
    public boolean hasVisited(@Nullable T item) {
        BranchRecursiveTraversal<T> parent = this.parent;
        while (parent != null) {
            if (parent.tracker().hasVisited(item))
                return true;
            parent = parent.parent;
        }

        return tracker.hasVisited(item);
    }

    /**
     * <p>The default branch supplier to provide the ability to create traversal branches.</p>
     * <p>This supplier creates a new {@link BranchRecursiveTraversal} using the suppliers provided to this instance when
     * it was created and setting this as the parent.</p>
     * <p>It then copies all step actions and stop conditions into the new instance.
     * where its creator has already been.</p>
     *
     * @return The default branch supplier used by this class.
     */
    public Supplier<Traversal<T>> defaultBranchSupplier() {
        return () -> new BranchRecursiveTraversal<>(
            this,
            this.queueNext,
            this.processQueueSupplier,
            this.trackerSupplier,
            this.branchQueueSupplier,
            this.onBranchStart)
            .copyStepActions(this)
            .copyStopConditions(this);
    }

    @Override
    public BranchRecursiveTraversal<T> reset() {
        resetRunFlag();

        processQueue.clear();
        branchQueue.clear();
        tracker.clear();

        return this;
    }

    /**
     * Runs the traversal. All items in the queue will be processed before moving onto processing the branch
     * queue. The results from each of the branch traversals are added to the result of this traversal to give all
     * items that were traversed through all branches.
     */
    @Override
    protected void doRun(boolean canStopOnStartItem) {
        boolean canStop = true;

        T start = startItem();
        if (start != null) {
            processQueue.add(start);
            canStop = canStopOnStartItem;
        }

        while (processQueue.hasNext()) {
            T current = processQueue.next();

            if (visit(current)) {
                boolean isStopping = canStop && matchesAnyStopCondition(current);

                applyStepActions(current, isStopping);

                if (!isStopping)
                    queueNext.accept(current, this);

                canStop = true;
            }
        }

        traverseBranches();
    }

    private boolean visit(@Nullable T item) {
        BranchRecursiveTraversal<T> parent = this.parent;
        while (parent != null) {
            if (parent.tracker().hasVisited(item))
                return false;
            parent = parent.parent;
        }

        return tracker.visit(item);
    }

    private void traverseBranches() {
        while (branchQueue.hasNext()) {
            Traversal<T> traversal = branchQueue.next();
            if (traversal != null) {
                if (onBranchStart != null)
                    onBranchStart.accept(traversal.startItem());
                traversal.run();
            }
        }
    }

}

