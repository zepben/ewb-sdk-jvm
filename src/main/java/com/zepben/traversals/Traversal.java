/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.traversals;

import com.zepben.annotations.EverythingIsNonnullByDefault;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * <p>Base class that provides some common functionality for traversals. This includes things like registering callbacks
 * to be called at every step in the traversal as well as registering stop conditions that traversals can check for when
 * to stop following a path.</p>
 * <p>This base class does not actually provide any way to traverse the items. It needs to be implemented in
 * subclasses. See {@link BasicTraversal} for an example.</p>
 * <p>Note this class is not thread safe!</p>
 *
 * @param <T> Object type to be traversed.
 */
@SuppressWarnings("WeakerAccess")
@EverythingIsNonnullByDefault
public abstract class Traversal<T> {

    private volatile boolean running = false;
    private volatile boolean hasRun = false;
    @Nullable private T startItem = null;
    private List<Predicate<T>> stopConditions = new ArrayList<>();
    private List<BiConsumer<T, Boolean>> stepActions = new ArrayList<>();

    /**
     * <p>Add a callback to check whether the current item in the traversal is a stop point.</p>
     * <p>If any of the registered stop conditions return true, the traversal will not call the callback to queue more items.
     * Note that a match on a stop condition doesn't necessarily stop the traversal, it just stops
     * traversal of the current branch.</p>
     *
     * @param condition A predicate that if returns true will cause the traversal to stop traversing the branch.
     * @return this traversal instance.
     */
    public Traversal<T> addStopCondition(Predicate<T> condition) {
        stopConditions.add(condition);
        return this;
    }

    /**
     * Clears all of the stop conditions registered on this traversal.
     */
    public void clearStopConditions() {
        stopConditions.clear();
    }

    /**
     * Copies all the stop conditions from another traversal to this traversal
     *
     * @param other The other traversal object to copy from.
     * @return this traversal instance.
     */
    public Traversal<T> copyStopConditions(Traversal<T> other) {
        stopConditions.addAll(other.stopConditions);
        return this;
    }

    /**
     * Checks all the stop conditions for the passed in item and returns true if any match.
     * This calls all registered stop conditions even if one has already returned true to make sure everything is
     * notified about this item.
     *
     * @param item The item to pass to the stop conditions.
     * @return true if any of the stop conditions return true.
     */
    public boolean matchesAnyStopCondition(@Nullable T item) {
        boolean stop = false;
        for (Predicate<T> condition : stopConditions)
            stop |= condition.test(item);
        return stop;
    }

    /**
     * Add a callback which is called for every item in the traversal (including the starting item).
     *
     * @param action Action to be called on each item in the traversal, passing if the trace will stop on this step.
     * @return this traversal instance.
     */
    public Traversal<T> addStepAction(BiConsumer<T, Boolean> action) {
        stepActions.add(action);
        return this;
    }

    /**
     * Clears all step actions registered on this traversal.
     */
    public void clearStepActions() {
        stepActions.clear();
    }

    /**
     * Copies all the step actions from the passed in traversal to this traversal.
     *
     * @param other The other traversal object to copy from.
     * @return this traversal instance.
     */
    public Traversal<T> copyStepActions(Traversal<T> other) {
        stepActions.addAll(other.stepActions);
        return this;
    }

    /**
     * Calls all the step actions with the passed in item.
     *
     * @param item       The item to pass to the step actions.
     * @param isStopping Indicates if the trace will stop on this step.
     */
    public void applyStepActions(@Nullable T item, boolean isStopping) {
        stepActions.forEach(a -> a.accept(item, isStopping));
    }

    /**
     * Sets the item the traversal will start at.
     *
     * @param item The item to start at.
     * @return this traversal instance.
     */
    public Traversal<T> setStart(T item) {
        startItem = item;
        return this;
    }

    /**
     * Gets the item the traversal will start at.
     *
     * @return The item the traversal will start at, or {@code null} if it has not been set.
     */
    @Nullable
    public T startItem() {
        return startItem;
    }

    public abstract Tracker<T> tracker();

    /**
     * Starts the traversal calling {@link #setStart(Object)} on the parameter before running while allowing the
     * traversal to stop on the start item.
     *
     * @param start The item to start at.
     */
    public final void run(T start) {
        setStart(start);
        run();
    }

    /**
     * Starts the traversal calling {@link #setStart(Object)} on the parameter before running.
     *
     * @param start              The item to start at.
     * @param canStopOnStartItem indicates if the traversal will check the start item for stop conditions.
     */
    public final void run(T start, boolean canStopOnStartItem) {
        setStart(start);
        run(canStopOnStartItem);
    }

    /**
     * Starts the traversal allowing the traversal to stop on the start item. {@link #setStart(Object)} should
     * of been called to set the starting item or use the overloaded run method that takes an item to start at.
     */
    public final void run() {
        run(true);
    }

    /**
     * Starts the traversal. {@link #setStart(Object)} should of been called to set the starting item or use the
     * overloaded run method that takes an item to start at.
     *
     * @param canStopOnStartItem indicates if the traversal will check the start item for stop conditions.
     */
    public final void run(boolean canStopOnStartItem) {
        if (running)
            throw new IllegalStateException("Traversal is already running.");

        if (hasRun)
            throw new IllegalStateException("Traversal must be reset before reuse.");

        running = true;
        hasRun = true;
        doRun(canStopOnStartItem);
        running = false;
    }

    protected final void resetRunFlag() {
        if (running)
            throw new IllegalStateException("Traversal is currently running.");

        hasRun = false;
    }

    public abstract Traversal<T> reset();

    protected abstract void doRun(boolean canStopOnStartItem);

}
