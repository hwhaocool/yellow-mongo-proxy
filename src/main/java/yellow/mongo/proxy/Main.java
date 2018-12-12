package yellow.mongo.proxy;

public class Main {
    
    private static final int PORT = 9632;

    private Main() {
    }

    public static void main(String[] args) {
        System.out.println("Starting...");
        ProxyServer server = new ProxyServer(PORT);
        server.run();

        System.out.println("Bye!");
    }

}
