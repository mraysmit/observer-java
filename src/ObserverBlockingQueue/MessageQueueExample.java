package ObserverBlockingQueue;

import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueueExample {
    public static void main(String[] args) {
        LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();




        // Producer thread
        new Thread(() -> {
            try {
                messageQueue.put(new Message("Topic 1", "Message 1"));
                messageQueue.put(new Message("Topic 2", "Message 2"));
                messageQueue.put(new Message("Topic 3", "Message 3"));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        // Consumer thread
        new Thread(() -> {
            try {
                while (true) {
                    Message message = messageQueue.take();
                    System.out.println("Consumed: " + message);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    record Message(String topic, String payload) {
        public Message {
            if (topic == null) topic = "*";
        }
    }
}