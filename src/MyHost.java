/* Implement this class. */

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

public class MyHost extends Host {
    private volatile boolean running = true;
    private double time = 0;
    private double leftToExecute = 0;
    private Task taskExecuted = null;
    private volatile boolean stopExecute;
    private final Semaphore sem = new Semaphore(0);
    private final PriorityBlockingQueue<Task> taskQueue = new PriorityBlockingQueue<>(1, new Comparator<Task>() {

        @Override
        public int compare(Task o1, Task o2) {
            if(o1.getPriority() == o2.getPriority()) {
                return o1.getStart() - o2.getStart();
            } else {
                return o2.getPriority() - o1.getPriority();
            }
        }
    });
    @Override
    public void run() {
        while(running) {
            try {
                sem.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // in cazul in care se da shutdown si inca nu s-a dus waiting time-ul
            if(!running) {
                break;
            }
            try {
                if (taskQueue.size() != 0) {
                    taskExecuted = taskQueue.take();
                } else {
                    taskExecuted = null;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (taskExecuted != null) {
                stopExecute = false;
                double startTimer = Timer.getTimeDouble();
                time = 0;
                leftToExecute = taskExecuted.getLeft();
                while (taskExecuted.getLeft() - time * 1000 >= 0 && !stopExecute) {
                    time = Timer.getTimeDouble() - startTimer;
                    leftToExecute = taskExecuted.getLeft() - time * 1000;
                }
                if (Math.round(time) * 1000 >= taskExecuted.getLeft()) {
                    taskExecuted.finish();
                } else {
                    taskExecuted.setLeft(taskExecuted.getLeft() - Math.round(time) * 1000);
                    taskQueue.add(taskExecuted);
                    sem.release();
                }
                taskExecuted = null;
            }
        }
    }

    @Override
    public void addTask(Task task) {
        taskQueue.add(task);
        sem.release();
        if(taskExecuted != null && taskExecuted.isPreemptible()) {
            if(task.getPriority() > taskExecuted.getPriority()) {
                stopExecute = true;
            }
        }

    }

    @Override
    public int getQueueSize() {
        if (taskExecuted == null) {
            return taskQueue.size();
        } else {
            return 1 + taskQueue.size();
        }
    }

    @Override
    public long getWorkLeft() {
        if (taskExecuted == null) {
            double timeToExecute = 0;
            for (Task taskToExecute : taskQueue) {
                timeToExecute += taskToExecute.getLeft();
            }
            return Math.round(timeToExecute / 1000);
        } else {
            double timeToExecute = 0;
            for (Task taskToExecute : taskQueue) {
                timeToExecute += taskToExecute.getLeft();
            }
            return Math.round((leftToExecute + timeToExecute) / 1000);
        }
    }

    @Override
    public void shutdown() {
        sem.release();
        running = false;
        stopExecute = true;
    }
}
