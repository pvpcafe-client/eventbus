/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package cat.psychward;

import cat.psychward.events.annotations.Listen;
import cat.psychward.events.api.Listener;
import cat.psychward.events.core.EventBus;

public class Example {

    private final EventBus eventBus = new EventBus();


    public void fieldSubscribeExample() {
        eventBus.subscribe(this);
    }

    // NOTE: you can have as many field-listeners as you want, just make sure to annotate them with @Listen
    @Listen
    public final Listener<ExampleEvent> onExampleEvent = event -> {
        System.out.println("Example event fired (field method)");
    };


    public void directSubscribeExample() {
        eventBus.subscribe(this, ExampleEvent.class, event -> {
            System.out.println("Example event fired (direct method)");
        });
    }


    public void postExample() {
        eventBus.post(new ExampleEvent()); // returns the posted event
    }


    public void unsubscribeExample() {
        eventBus.unsubscribe(this);
    }


    public static void main(String[] args) {
        final Example example = new Example();
        example.fieldSubscribeExample();

        example.directSubscribeExample();

        example.postExample();

        example.unsubscribeExample();
    }

}