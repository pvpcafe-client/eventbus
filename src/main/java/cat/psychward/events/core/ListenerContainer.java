/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package cat.psychward.events.core;

import cat.psychward.events.api.Listener;
import cat.psychward.events.api.event.Event;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@SuppressWarnings({"unchecked"})
public final class ListenerContainer<T extends Event> {

    private final Map<Object, List<Listener<? extends Event>>> ownerToListeners = new ConcurrentHashMap<>();

    private final Map<Listener<?>, Integer> priorityMap = new ConcurrentHashMap<>();

    private Listener<T>[] listeners = new Listener[0];

    private final Class<?> eventClass;

    public ListenerContainer(Class<?> eventClass) {
        this.eventClass = eventClass;
    }

    public void subscribe(Object owner, Listener<? extends Event> listener, int priority) {
        this.ownerToListeners.computeIfAbsent(owner, __ -> new ArrayList<>())
                .add(listener);
        this.priorityMap.put(listener, priority);

        this.updateListeners();
    }

    public void unsubscribe(Object owner, Listener<? extends Event> listener) {
        this.ownerToListeners.computeIfAbsent(owner, __ -> new ArrayList<>())
                .remove(listener);
        this.priorityMap.remove(listener);

        this.updateListeners();
    }

    public void unsubscribeAll(Object owner) {
        final List<Listener<? extends Event>> listeners = this.ownerToListeners.get(owner);
        if (listeners != null) {
            this.ownerToListeners.remove(owner);
            for (Listener<? extends Event> listener : listeners)
                this.priorityMap.remove(listener);

            this.updateListeners();
        }
    }

    private void updateListeners() {
        this.listeners = this.ownerToListeners.values()
                .stream().flatMap(list -> Stream.of(list.toArray(Listener[]::new)))
                .sorted(Comparator.comparingInt(priorityMap::get))
                .toArray(Listener[]::new);
    }

    public void post(Event event) {
        if (eventClass != event.getClass()) return;

        final T t = (T) event;
        final Listener<T>[] listeners = this.listeners;

        for (Listener<T> listener : listeners)
            listener.onEvent(t);
    }
}