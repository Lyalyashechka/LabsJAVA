import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlerTask implements Runnable {
    private static URLPOOL urlPoll;
    private static final int PORT = 80;
    private static final String REGEX = "href=\"(http.*?)\"";
    private volatile boolean running = true;
    private int maxTimeWait;

    public CrawlerTask(URLPOOL urlPoll, int maxTimeWait) {
        this.urlPoll = urlPoll;
        this.maxTimeWait = maxTimeWait;
    }

    public boolean isUrlValid(String url) {
        boolean checkUrl = url.startsWith(URLDepthPair.URL_PREFIX) || url.startsWith(URLDepthPair.URL_PREFIX_1);
        return checkUrl;
    }

    public void run() {
        while (running) {
            try {
                URLDepthPair currentPage = urlPoll.getPair();
                SearchUrlOnPage(currentPage);
            }
            catch (IOException e) {
                System.out.println(e);
            }
            catch (InterruptedException e) {
                running = false;
            }
        }
    }
    public void SearchUrlOnPage(URLDepthPair page) throws IOException {
        Socket newSock = new Socket(page.getUrl(), PORT);
        newSock.setSoTimeout(maxTimeWait);

        OutputStream os = newSock.getOutputStream();
        PrintWriter writer = new PrintWriter(os, true);

        String docPath = page.getDepth() == 0 ? "/" : page.getDocPath();

        writer.println("GET " + docPath + " HTTP/1.1");
        writer.println("Host: " + page.getUrl());
        writer.println("Connection: close");
        writer.println();

        InputStream pageStream = newSock.getInputStream();
        InputStreamReader pageStreamReader = new InputStreamReader(pageStream);
        BufferedReader pageStreamReaderBuf = new BufferedReader(pageStreamReader);

        while (true) {
            String oneLine = pageStreamReaderBuf.readLine();
            if (oneLine == null) break;
            Pattern regUrl = Pattern.compile(REGEX);
            Matcher regUrlMatcher = regUrl.matcher(oneLine);
            while (regUrlMatcher.find()) {
                String url = regUrlMatcher.group(1);
                if (isUrlValid(url)) {
                    URLDepthPair current = new URLDepthPair(new URL(url), page.getDepth() + 1);
                    if(!urlPoll.getSeen(current)) {
                        int depth = page.getDepth();
                        if (depth < urlPoll.getMaxDepth()) {
                            urlPoll.pullPair(current);
                        }
                    }
                }
            }
        }
        newSock.close();
    }
}
