import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class URLPOOL {
    private LinkedList<URLDepthPair> queue;
    private Set<URLDepthPair> seen;
    private int maxDepth;
    private int timeWait;

    public URLPOOL(int maxDepth) {
        queue = new LinkedList<>();
        seen = new LinkedHashSet<>();
        this.maxDepth = maxDepth;
        timeWait = 0;
    }

    public synchronized void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public synchronized URLDepthPair getPair() throws InterruptedException {
        while (queue.size() == 0) {
            timeWait++;
            wait();
            timeWait--;
        }
        return queue.removeFirst();
    }

    public synchronized void pullPair(URLDepthPair pair) {
        if (pair.getDepth() < maxDepth) queue.addLast(pair);
        seen.add(pair);
        notify();
    }

    public synchronized int getMaxDepth () {
        return maxDepth;
    }

    public synchronized boolean getSeen(URLDepthPair url) {
        return seen.contains(url);
    }

    public synchronized Set<URLDepthPair> getSeen() {
        return seen;
    }

    public synchronized int getTimeWait() {
        return timeWait;
    }
}
