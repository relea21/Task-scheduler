/* Implement this class. */

import java.util.Comparator;
import java.util.List;

public class MyDispatcher extends Dispatcher {
    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
        //Deoarece de fiecare data cand se creeaza un nou dispatcher e nevoie sa se reinitieze algoritmul folosit
        UniversalDispatcher.resetInstance();
    }
    @Override
    synchronized public void addTask(Task task) {
        UniversalDispatcher.getInstance(algorithm).addTask(hosts,task);
    }
}

abstract class UniversalDispatcher {
    private static UniversalDispatcher instance = null;

    protected UniversalDispatcher() {}

    public static UniversalDispatcher getInstance(SchedulingAlgorithm algorithm) {
        if (instance == null) {
            synchronized (UniversalDispatcher.class) {
                if (instance == null) {
                    switch (algorithm) {
                        case ROUND_ROBIN:
                            instance = new RoundRobinDispatcher();
                            break;
                        case SHORTEST_QUEUE:
                            instance = new ShortestQueueDispatcher();
                            break;
                        case SIZE_INTERVAL_TASK_ASSIGNMENT:
                            instance = new SizeIntervalTaskDispatcher();
                            break;
                        case LEAST_WORK_LEFT:
                            instance = new LeastWorkLeftDispatcher();
                            break;
                    }
                }
                return instance;
            }
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }
    public abstract void addTask(List<Host> hosts, Task task);
}
class RoundRobinDispatcher extends UniversalDispatcher {
    private int lastHost = 0;

    @Override
    public void addTask(List<Host> hosts, Task task) {
        hosts.get(lastHost % hosts.size()).addTask(task);
        lastHost++;
    }
}
class ShortestQueueDispatcher extends UniversalDispatcher {
    @Override
    public void addTask(List<Host> hosts, Task task) {
        hosts.sort(new Comparator<Host>() {
            public int compare(Host o1, Host o2) {
                if(o1.getQueueSize() == o2.getQueueSize())
                    return (int)(o1.getId() - o2.getId());
                return o1.getQueueSize() - o2.getQueueSize();
            }
        });
        hosts.get(0).addTask(task);
    }
}
class SizeIntervalTaskDispatcher extends UniversalDispatcher {
    @Override
    public void addTask(List<Host> hosts, Task task) {
        switch (task.getType()) {
            case SHORT:
                hosts.get(0).addTask(task);
                break;
            case MEDIUM:
                hosts.get(1).addTask(task);
                break;
            case LONG:
                hosts.get(2).addTask(task);
                break;
        }
    }
}
class LeastWorkLeftDispatcher extends UniversalDispatcher {
    @Override
    public void addTask(List<Host> hosts, Task task) {
        hosts.sort(new Comparator<Host>() {
            public int compare(Host o1, Host o2) {
                if(o1.getWorkLeft() == o2.getWorkLeft())
                    return (int)(o1.getId() - o2.getId());
                return (int)(o1.getWorkLeft() - o2.getWorkLeft());
            }
        });
        hosts.get(0).addTask(task);
    }
}
