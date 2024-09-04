package ObserverConsumer;

import java.util.function.Consumer;

public class Callbacks {

    public static void main(String[] args) {

        Operation<String> operation = new Operation<>();

        // Call performOperation with a lambda expression as the callback
        operation.performOperation("Example Data", (result) -> {
            System.out.println("Callback received: " + result);
        });


    };

    static class Operation<T> {
        public void performOperation(T data, Consumer<T> callback) {
            T result = data;
            callback.accept(result);
        }
    }



}