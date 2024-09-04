package ObserverConcurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// This is an example of a Publisher service
public class Observer {
    public static void main(String[] args) {

        // Client Subscriber
        var listener = new NewMessageListenerNotifier("First Listener");

        // Initialize the Broker instance on a new thread
        var broker = BrokerService.getInstance();
        // Register the listener
        broker.registerNewMessageListener(listener);
        broker.addMessage(new Message("Foo"));
        broker.addMessage(new Message("Bar"));
        broker.addMessage(new Message("Baz"));
        broker.unregisterNewMessageListener(listener);
    }
}


// Define the NewMessageListener interface
interface NewMessageListener {
    public String listenerName();
    public void onMessageAdded (Message message);
}

// This is an example of a listener or Subscriber service
// This class simply prints the name of the message that was added
record NewMessageListenerNotifier(String listenerName) implements NewMessageListener {

    @Override
    public void onMessageAdded(Message message) {
        // Print the name of the newly added message
        System.out.println("Notification: a new message was received with payload '" + message.payload() + "'");
    }
}

// Define the Message class
record Message (String payload) {}

class BrokerService {
    private static Broker instance;

    private BrokerService() {}

    public static Broker getInstance() {

        if (instance == null) {
            synchronized (BrokerService.class) {
                if (instance == null) {
                    instance = new Broker();
                }
            }
        }
        return instance;
    }
}


// Define the Broker class
class Broker {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    protected final Lock readLock = readWriteLock.readLock();
    protected final Lock writeLock = readWriteLock.writeLock();

    // Define the list of message sent to the Broker
    private List<Message> messages = new ArrayList<>();
    // Define the list of registered listeners
    private List<NewMessageListener> listeners = new ArrayList<>();

    public void addMessage (Message message) {
        // Add the message to the list of animals
        this.messages.add(message);

        // Notify the list of registered listeners
        this.notifyMessageAddedListeners(message);
    }

    public void registerNewMessageListener(NewMessageListener listener) {
        // Lock the list of listeners for writing
        this.writeLock.lock();

        try {
            // Add the listener to the list of registered listeners
            this.listeners.add(listener);
            System.out.println("Added Listener: " + listener.listenerName());
        }
        finally {
            // Unlock the writer lock
            this.writeLock.unlock();
        }
    }

    public void unregisterNewMessageListener(NewMessageListener listener) {
        // Lock the list of listeners for writing
        this.writeLock.lock();

        try {
            // Remove the listener from the list of the registered listeners
            this.listeners.remove(listener);
            System.out.println("Removed Listener: " + listener.listenerName());
        }
        finally {
            // Unlock the writer lock
            this.writeLock.unlock();
        }
    }

    // the notify method accepts a single argument: The message that was added
    protected void notifyMessageAddedListeners (Message message) {
        // Lock the list of listeners for reading
        this.readLock.lock();

        try {
            // Notify each of the listeners in the list of registered listeners
            this.listeners.forEach(listener -> {
                listener.onMessageAdded(message);
                System.out.println("Notified listener name: " + listener.listenerName());
            });
        }
        finally {
            this.readLock.unlock();
        }
    }
}

