import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws MalformedURLException {
            if (args.length != 2) {
                System.out.println("Error arguments. True format is: URL, int maxPath");
                System.exit(0);
            }
            int maxDepth = 0;
            URLDepthPair firstPair = new URLDepthPair();
            try {
                maxDepth = Integer.parseInt(args[1]);
                firstPair = new URLDepthPair(new URL(args[0]), 0);
            }
            catch (MalformedURLException e) {
                System.out.println("Error URL\n" + e.toString());
                System.exit(0);
            }
            catch (NumberFormatException e) {
                System.out.println("Error depth, use int\n" + e.toString());
                System.exit(0);
            }
            Crawler run = new Crawler(firstPair, maxDepth);
            run.processingUrl();
    }

}