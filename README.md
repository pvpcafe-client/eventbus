# eventbus
Java PubSub event bus implementation for PvPCafe Client.

## example usage
### including the library in your project using gradle
```kts
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.pvpcafe-client:eventbus:1.0")
}
```

### using the library

#### initializing an event bus instance
```java
private final EventBus eventBus = new EventBus();
```

#### creating an event class
```java
public class ExampleEvent implements Event {
}
```

#### subscribing a listener (field method)
```java
public class Example {
    public void fieldSubscribeExample() {
        eventBus.subscribe(this);
    }
    
    // NOTE: you can have as many field-listeners as you want, just make sure to annotate them with @Listen
    @Listen
    public final Listener<ExampleEvent> onExampleEvent = event -> {
        System.out.println("Example event fired (field method)");
    };
}
```

#### subscribing a listener (direct method)
```java
public class Example {
    public void directSubscribeExample() {
        eventBus.subscribe(this, ExampleEvent.class, event -> {
            System.out.println("Example event fired (direct method)");
        });
    }
}
```

#### posting an event via the bus
```java
public class Example {
    public void postExample() {
        eventBus.post(new ExampleEvent()); // returns the posted event
    }
}
```

#### unsubscribing a listener-owner
```java
public class Example {
    public void unsubscribeExample() {
        eventBus.unsubscribe(this);
    }
}
```

## License
Licensed under Mozilla Public License 2.0 ([LICENSE](LICENSE)).