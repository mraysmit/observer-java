package ObserverRunnable;

import java.util.concurrent.*;

public class Observer {

    public static void main(String[] args) {
        Task task = new Task();
        Callback callback = new Callback();

        // #1 Create a new RunnableExecutor object and pass the task, callback, and a message to be displayed when the task is done.
        var runnable = new RunnableExecutor(task, callback, "{Callback Task completed}");
        var thread = new Thread(runnable);
        thread.start();

        // #2 Using CompletableFuture to run the task asynchronously and then accept the result and call the callback.taskDone method.
        CompletableFuture.runAsync(task)
                .thenAccept(result -> callback.taskDone("completable future completion details: " + result));


        // #3 Using ThreadPoolExecutor to run the task asynchronously and then call the callback.taskDone method.
        try (NotifyingThreadPoolExecutor nexecutor = new NotifyingThreadPoolExecutor(callback)) {
            nexecutor.execute(task);
        }

        // #4 Extending FutureTask to run the task asynchronously and then call the callback.taskDone method.
        FutureTask<String> future = new AlertingFutureTask(task, callback);
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            executor.submit(future);
        }

    }

    static class AlertingFutureTask extends FutureTask<String> {
        CallbackInterface callback;
        public AlertingFutureTask(Runnable runnable, Callback callback) {
            super(runnable, null);
            this.callback = callback;
        }
        @Override
        protected void done() {
            callback.taskDone("from FutureTask: notify task done");
        }
    }

    static class NotifyingThreadPoolExecutor extends ThreadPoolExecutor {
        CallbackInterface callback;
        public NotifyingThreadPoolExecutor(CallbackInterface callback) {
            super(1, 1, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));
            this.callback = callback;
        }
        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            callback.taskDone("callback from ThreadPoolExecutor");
        }
    }

    static class RunnableExecutor implements Runnable {
        Runnable task;
        CallbackInterface callback;
        String taskDoneMessage;

        RunnableExecutor(Runnable task, CallbackInterface callback, String taskDoneMessage) {
            this.task = task;
            this.callback = callback;
            this.taskDoneMessage = taskDoneMessage;
        }

        @Override
        public void run() {
            task.run();
            callback.taskDone(taskDoneMessage);
        }
    }


    static class Callback implements CallbackInterface {
        public void taskDone(String details){
            System.out.println("task complete: " + details);
            // notifications go here
        }
    }

    static class Task implements Runnable {
        @Override
        public void run() {
            System.out.println("Task executed");
            // do something
        }
    }

    interface CallbackInterface {
        void taskDone(String details);
    }

}
