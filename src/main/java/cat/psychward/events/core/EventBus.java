/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package cat.psychward.events.core;

import cat.psychward.events.annotations.Listen;
import cat.psychward.events.api.Listener;
import cat.psychward.events.api.event.Event;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class EventBus {

    private final Map<Class<? extends Event>, ListenerContainer<?>> executables = new ConcurrentHashMap<>();
    private ListenerContainer<?>[] cache = new ListenerContainer[0];

    public <U extends Event> void subscribe(final Object object, final Class<U> eventClass, final Listener<U> listener, final int priority) {
        this.executables.putIfAbsent(eventClass, new ListenerContainer<>(eventClass));
        this.cache = this.executables.values().toArray(new ListenerContainer<?>[0]);

        this.executables.get(eventClass).subscribe(object, listener, priority);
    }

    public <U extends Event> void subscribe(final Object object, final Class<U> eventClass, final Listener<U> listener) {
        subscribe(object, eventClass, listener, 0);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void subscribe(final Object object) {
        for (final Field field : object.getClass().getDeclaredFields()) {
            final Listen listen = field.getDeclaredAnnotation(Listen.class);
            if (listen == null || !field.getType().isAssignableFrom(Listener.class)) continue;

            if (field.getGenericType() instanceof final ParameterizedType type) {
                try {
                    field.setAccessible(true);
                    var clazz = (Class) type.getActualTypeArguments()[0];
                    this.executables.putIfAbsent(clazz, new ListenerContainer<>(clazz));
                    this.cache = this.executables.values().toArray(new ListenerContainer<?>[0]);

                    final Listener listener = (Listener) field.get(object);
                    this.executables.get(clazz).subscribe(object, listener, listen.value());
                } catch (final Exception ignored) {
                }
            }
        }
    }

    public void unsubscribe(final Object object) {
        for (ListenerContainer<?> container : this.cache)
            container.unsubscribeAll(object);
    }

    public <T extends Event> void unsubscribe(final Object object, final Listener<T> listener) {
        for (ListenerContainer<?> container : this.cache)
            container.unsubscribe(object, listener);
    }

    public <U extends Event> U post(final U event) {
        final ListenerContainer<?>[] array = this.cache;
        for (ListenerContainer<?> container : array)
            container.post(event);
        return event;
    }

}