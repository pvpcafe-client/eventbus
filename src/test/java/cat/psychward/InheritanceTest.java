/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package cat.psychward;

import cat.psychward.events.annotations.Listen;
import cat.psychward.events.api.Listener;
import cat.psychward.events.core.EventBus;

public class InheritanceTest {

    public static void main(String[] args) {
        final EventBus eventBus = new EventBus();
        final Parent object = new Child();
        eventBus.subscribe(object);
        eventBus.post(new ExampleEvent());
    }

    private static class Parent {
        @Listen
        private final Listener<ExampleEvent> parentListener = event -> {
            System.out.println("Parent event fired");
        };
    }

    private static class Child extends Parent {
        @Listen
        private final Listener<ExampleEvent> childListener = event -> {
            System.out.println("Child event fired");
        };
    }

}