package ftpserver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FtpServerTest {

    private FtpServer ftpServer;
    private int port = 9000; 
    private Thread serverThread;

    @BeforeEach
    void setUp() throws IOException {
        ftpServer = new FtpServer(port);

        serverThread = new Thread(ftpServer::start);
        serverThread.start();
    }

    @AfterEach
    void tearDown() {
        ftpServer.shutdown();
        try {
            serverThread.join(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testServerAcceptsConnection() throws IOException {
        try (Socket clientSocket = new Socket("localhost", port)) {
            assertTrue(clientSocket.isConnected(), "Le client doit pouvoir se connecter au serveur FTP.");
        }
    }

    @Test
    void testServerHandlesMultipleConnections() throws IOException, InterruptedException {
        int connectionCount = 5;
        Socket[] clients = new Socket[connectionCount];

        for (int i = 0; i < connectionCount; i++) {
            clients[i] = new Socket("localhost", port);
            assertTrue(clients[i].isConnected(), "Le client " + i + " doit se connecter.");
        }

        for (Socket client : clients) {
            client.close();
        }
        
        TimeUnit.MILLISECONDS.sleep(100);
    }

    @Test
    void testServerShutdown() throws IOException {
        ftpServer.shutdown();

        try (Socket clientSocket = new Socket()) {
            assertThrows(IOException.class, () -> 
                clientSocket.connect(new InetSocketAddress("localhost", port)),
                "Après la fermeture, le serveur ne doit plus accepter de connexions."
            );
        }
    }

    @Test
    void testClientHandlerExecution() throws IOException {
        Socket mockSocket = mock(Socket.class);
        File rootDirectory = new File("ftpserver_folder");

        ClientHandler handler = new ClientHandler(mockSocket, rootDirectory);
        Thread handlerThread = new Thread(handler);
        handlerThread.start();

        verify(mockSocket, atLeastOnce()).getInputStream();
        handlerThread.interrupt(); // Arrêter proprement le thread de test
    }
}
