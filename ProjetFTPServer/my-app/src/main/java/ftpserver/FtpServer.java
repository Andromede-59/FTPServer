package ftpserver;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * FTP server
 */
public class FtpServer {
    private final ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private final File rootDirectory;

    public FtpServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.threadPool = Executors.newCachedThreadPool();
        this.rootDirectory = new File("ftpserver_folder");
        if(!rootDirectory.exists()) {
            rootDirectory.mkdir();
        }
    }

    /**
     * Start the server
     */
    public void start() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Démarre un nouveau handler dans le pool de threads
                threadPool.execute(new ClientHandler(clientSocket, rootDirectory));
            }
        } catch (IOException e) {
            System.out.println("Error accepting connection: " + e.getMessage());
            shutdown();
        }
    }

    /**
     * Shutdown the server
     */
    public void shutdown() {
        threadPool.shutdown();
        try {
            serverSocket.close();
        } catch (IOException e) {
            // Log error
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }
}