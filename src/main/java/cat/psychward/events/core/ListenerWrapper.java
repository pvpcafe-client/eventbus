/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package cat.psychward.events.core;

import cat.psychward.events.api.event.Event;
import cat.psychward.events.api.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("ClassCanBeRecord") // class *can't* be a record dear intellij
final class ListenerWrapper<T extends Event> {

    @NotNull
    private final Listener<Event> listener;

    private final int priority;

    @Nullable
    private final Object owner;

    @NotNull
    private final Class<T> type;

    @SuppressWarnings("unchecked")
    ListenerWrapper(final @NotNull Class<T> type, final @NotNull Listener<T> listener, final @Nullable Object owner, final int priority) {
        this.listener = (Listener<Event>) listener;
        this.priority = priority;
        this.owner = owner;
        this.type = type;
    }

    public void onEvent(Event pEvent) {
        if (this.type == pEvent.getClass() || this.type == Event.class || this.type.isAssignableFrom(pEvent.getClass()))
            this.listener.onEvent(pEvent);
    }

    public int getPriority() {
        return priority;
    }

    public @Nullable Object getOwner() {
        return owner;
    }
}