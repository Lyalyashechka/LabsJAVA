import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<Thread> threadsList = new ArrayList<>();

    public static int getNumberCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static void startThreads(List<Thread> threadsList, int numThread, URLPOOL urlPool, int maxPatience) {
        for (int i = 0; i < numThread; i++) {
            CrawlerTask newCrawlerThread = new CrawlerTask(urlPool, maxPatience);
            Thread t = new Thread(newCrawlerThread);
            threadsList.add(t);
            t.start();
        }
    }
    public static void waitThreads(URLPOOL urlPool, int numThread) {
        while(urlPool.getTimeWait() != numThread) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                System.out.println("Error Thread");
            }
        }
    }

    public static void main(String[] args) throws MalformedURLException {
            if (args.length != 2) {
                System.out.println("Error arguments. True format is: URL, int maxPath");
                System.exit(0);
            }

            int numThread = getNumberCores();
            int maxDepth = 0;
            int maxPatience = 1000;

            try {
                maxDepth = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e) {
                System.out.println("Error depth, use int\n" + e.toString());
                System.exit(0);
            }

            URLPOOL urlPool = new URLPOOL(maxDepth);
            try {
                urlPool.pullPair(new URLDepthPair(new URL(args[0]), 0));
            }
            catch (MalformedURLException e) {
                System.out.println(e.toString());
            }

            startThreads(threadsList, numThread, urlPool, maxPatience);
            waitThreads(urlPool, numThread);
            urlPool.getSeen().stream().forEach(System.out::println);
            threadsList.stream().forEach(Thread::interrupt);
    }

}