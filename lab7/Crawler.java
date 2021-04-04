import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {
    public static final int CONNECT_PORT = 80;
    public static final String REGEX = "href=\"(http.*?)\"";
    private LinkedList<URLDepthPair> queue;
    private LinkedList<URLDepthPair> sites;
    private int maxDepth;
    public Crawler() {
        this.maxDepth = 0;
        queue = new LinkedList<>();
        sites = new LinkedList<>();
    }
    public Crawler(URLDepthPair startUrl, int maxDepth) {
        this.maxDepth = maxDepth;
        queue = new LinkedList<>();
        queue.add(startUrl);
        sites = new LinkedList<>();
    }

    public LinkedList<URLDepthPair> getSites() {
        return sites;
    }

    public void SearchUrlOnPage(URLDepthPair page) throws IOException {
        Socket newSock = new Socket(page.getUrl(), CONNECT_PORT);

        OutputStream os = newSock.getOutputStream();
        PrintWriter writer = new PrintWriter(os, true);

        // Send HTTP request
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
                if (!sites.contains(url))
                    queue.add(new URLDepthPair(new URL(url), page.getDepth() + 1));
            }
        }

        System.out.println(page.toString());
        sites.add(page);
        newSock.close();
        if (page.getDepth() < maxDepth) processingUrl();
    }

    public void processingUrl() {
        while (!queue.isEmpty()) {
            URLDepthPair nextUrlForSearch = queue.removeFirst();
            if(nextUrlForSearch.getDepth() < maxDepth) {
                try {
                    SearchUrlOnPage(nextUrlForSearch);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
