import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class IGFSSServer {
    public static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        ensureKeysExist();

        try (ServerSocket listenSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("IGFSS Server started on port " + SERVER_PORT);
            System.out.println("Waiting for client connections...");

            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                new Connection(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Listen socket: " + e.getMessage());
        }
    }

    private static void ensureKeysExist() {
        File pubFile = new File("IGFSSPub.ser");
        File priFile = new File("IGFSSPri.ser");

        if (!pubFile.exists() || !priFile.exists()) {
            try {
                GroupKeyGen.main(new String[0]);
            } catch (Exception e) {
                System.out.println("Unable to generate key files: " + e.getMessage());
            }
        }
    }
}
