package ObserverBasic;

import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Observer {
    public static void main(String[] args) {

    // Create a new Broker object
        Broker broker = new Broker();

        // Register a PrintNameAnimalAddedListener object
        broker.registerMessageAddedListener(new PrintNameMessageAddedListener());

        // Add an animal to the zoo
        broker.addMessage(new Message("Foo"));
        broker.addMessage(new Message("Bar"));
        broker.addMessage(new Message("Baz"));

    }
}

// Define the MessageAddedListener interface
interface MessageAddedListener {
    public void onMessageAdded (Message message);
}

// This is an example of a listener or Subscriber
// This class simply prints the name of the message that was added
class PrintNameMessageAddedListener implements MessageAddedListener {

    @Override
    public void onMessageAdded (Message message) {
        // Print the name of the newly added message
        System.out.println("Notification: a new message was received with payload '" + message.payload() + "'");
    }
}


// Define the Message class
record Message (String payload) {}


// Define the Broker class
class Broker {

    // Define the list of message sent to the Broker
    private List<Message> messages = new ArrayList<>();
    // Define the list of registered listeners
    private List<MessageAddedListener> listeners = new ArrayList<>();

    public void addMessage (Message message) {
        // Add the message to the list of animals
        this.messages.add(message);

        // Notify the list of registered listeners
        this.notifyMessageAddedListeners(message);
    }

    public void registerMessageAddedListener (MessageAddedListener listener) {
        // Add the listener to the list of registered listeners
        this.listeners.add(listener);
    }

    public void unregisterMessageAddedListener (MessageAddedListener listener) {
        // Remove the listener from the list of the registered listeners
        this.listeners.remove(listener);
    }


    // Instead of accepting zero arguments implementation of the notify method deviates slightly from the standard signature of the Observer Pattern.
    // the notify method accepts a single argument: The message that was added
    protected void notifyMessageAddedListeners (Message message) {
        // Notify each of the listeners in the list of registered listeners
        this.listeners.forEach(listener -> listener.onMessageAdded(message));
    }
}

