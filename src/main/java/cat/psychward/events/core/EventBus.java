/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package cat.psychward.events.core;

import cat.psychward.events.annotations.Listen;
import cat.psychward.events.api.event.Event;
import cat.psychward.events.api.Listener;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class EventBus {

    private final List<ListenerWrapper<?>> executables = new ArrayList<>();
    private ListenerWrapper<?>[] cache;

    public <U extends Event> void subscribe(final Object object, final Class<U> eventClass, final Listener<U> listener, final int priority) {
        executables.add(new ListenerWrapper<>(eventClass, listener, object, priority));
        executables.sort(Comparator.comparingInt(ListenerWrapper::getPriority));

        this.cache = executables.toArray(new ListenerWrapper<?>[0]);
    }

    public <U extends Event> void subscribe(final Object object, final Class<U> eventClass, final Listener<U> listener) {
        subscribe(object, eventClass, listener, 0);
    }

    @SuppressWarnings("unchecked")
    public <U extends Event> void subscribe(final Object object) {
        for (final Field field : object.getClass().getDeclaredFields()) {
            final Listen listen = field.getDeclaredAnnotation(Listen.class);
            if (listen == null || !field.getType().isAssignableFrom(Listener.class)) continue;

            if (field.getGenericType() instanceof final ParameterizedType type) {
                try {
                    field.setAccessible(true);
                    final Listener<U> listener = (Listener<U>) field.get(object);
                    executables.add(new ListenerWrapper<>((Class<U>) type.getActualTypeArguments()[0], listener, object, listen.value()));
                } catch (final Exception ignored) {}
            }
        }
        executables.sort(Comparator.comparingInt(ListenerWrapper::getPriority));

        this.cache = executables.toArray(new ListenerWrapper<?>[0]);
    }

    public void unsubscribe(final Object object) {
        executables.removeIf(e -> Objects.equals(object, e.getOwner()));

        this.cache = executables.toArray(new ListenerWrapper<?>[0]);
    }

    public <U extends Event> U post(final U event) {
        final ListenerWrapper<?>[] array = this.cache;
        for (ListenerWrapper<?> listenerWrapper : array)
            listenerWrapper.onEvent(event);
        return event;
    }

}