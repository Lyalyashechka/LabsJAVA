import java.net.URL;

public class URLDepthPair {
    private URL url;
    private int depth;

    public URLDepthPair() {
        this.url = null;
        this.depth = 0;
    }

    public URLDepthPair (URL url, int depth) {
        this.url = url;
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    public String getUrl() {
        return url.getHost();
    }

    public String getDocPath() {
        return url.getPath();
    }

    @Override
    public String toString() {
        return "URL: " + url.toString() + " search depth: " + depth;
    }
}
