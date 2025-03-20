package ftpserver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DataConnectionTest {

    @Mock
    private ServerSocket mockServerSocket;

    @Mock
    private Socket mockSocket;

    private DataConnection dataConnection;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        dataConnection = new DataConnection();
    }

    @AfterEach
    void tearDown() throws IOException {
        dataConnection.close();
    }


    @Test
    void testStartPassiveMode() throws IOException {
        int port = dataConnection.startPassiveMode();
    
        // Vérifie que le port est dans la plage correcte
        assertTrue(port >= 20000 && port <= 30000, "Le port doit être dans la plage 20000-30000.");
    }
    

    @Test
    void testAcceptPassiveConnection() throws IOException, InterruptedException {
        final int port = dataConnection.startPassiveMode();
    
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress("localhost", 0)); // Utilisation de 0 pour un port dynamique
    
            Socket mockSocket = mock(Socket.class);
    
            new Thread(() -> {
                try {
                    System.out.println("Tentative de connexion au serveur sur le port : " + port);
                    Socket socket = new Socket("localhost", port);
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Erreur lors de la connexion au serverSocket" + e.getMessage());
                    e.printStackTrace();
                }
            }).start();
    
            dataConnection.setDataSocket(mockSocket);
    
            Socket returnedSocket = dataConnection.acceptPassiveConnection();
            assertNotNull(returnedSocket, "Le socket de données doit être accepté.");
    
        } catch (IOException e) {
            e.printStackTrace();
            fail("Le ServerSocket n'a pas pu être créé ou lié au port.");
        }
    
        Thread.sleep(200);
    }
    
    
    
    
    @Test
    void testClearDataSocket() throws IOException {
        // Simuler un socket de données
        dataConnection.setDataSocket(mockSocket);

        // Simuler la fermeture du socket de données
        doNothing().when(mockSocket).close();

        // Appeler clearDataSocket
        dataConnection.clearDataSocket();

        // Vérifier que la méthode close() a bien été appelée sur le socket
        verify(mockSocket, times(1)).close();
    }

    @Test
    void testClose() throws IOException {
        dataConnection = new DataConnection();
    
        // Simuler l'ouverture du mode passif
        dataConnection.startPassiveMode();
    
        // Simuler une connexion de données
        Socket mockSocket = mock(Socket.class);
        dataConnection.setDataSocket(mockSocket);
    
        // Simuler la fermeture des sockets
        doNothing().when(mockSocket).close();
    
        // Appeler la méthode close() de DataConnection
        dataConnection.close();
    
        // Vérifier que le socket de données a bien été fermé
        verify(mockSocket, times(1)).close();
    }
    
    @Test
    void testFindAvailablePort() {
        // Ce test est destiné à vérifier la logique de recherche d'un port disponible
        // Ici, nous ne pouvons pas vraiment tester l'aléatoire mais nous pouvons vérifier qu'un port est effectivement retourné
        assertDoesNotThrow(() -> {
            int port = dataConnection.startPassiveMode();
            assertTrue(port >= 20000 && port <= 30000, "Le port trouvé doit être dans la plage autorisée.");
        });
    }

    @Test
    void testFindAvailablePortNoPortsAvailable() throws IOException {
        DataConnection dataConnection = spy(new DataConnection());
    
        // Simuler une exception lors de l'appel à startPassiveMode
        doThrow(new RuntimeException("No available ports"))
            .when(dataConnection)
            .findAvailablePort();
    
        assertThrows(IOException.class, () -> dataConnection.startPassiveMode());
    }    
      
}
