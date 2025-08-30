/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package cat.psychward.events.api;

import cat.psychward.events.api.event.Event;

public interface Listener<T extends Event> {

    void onEvent(T event);

}