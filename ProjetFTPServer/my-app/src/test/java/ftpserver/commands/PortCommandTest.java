package ftpserver.commands;

import ftpserver.DataConnection;
import ftpserver.FtpSession;
import ftpserver.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PortCommandTest {

    private FtpSession ftpSession;
    private DataConnection dataConnection;
    private PortCommand portCommand;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() {
        // Initialisation des mocks
        ftpSession = mock(FtpSession.class);
        dataConnection = mock(DataConnection.class);
        when(ftpSession.getDataConnection()).thenReturn(dataConnection);

        // Initialisation de la commande PortCommand
        portCommand = new PortCommand(ftpSession);

        // Initialisation du StringWriter pour capturer la sortie
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @Test
    void testExecuteSuccess() throws IOException {
        String ip = "127,0,0,1";
        int portHigh = 123;
        int portLow = 45;
        String portCommandArg = ip + "," + portHigh + "," + portLow;

        // Mock de la DataConnection et de la méthode clearDataSocket()
        Socket dataSocket = mock(Socket.class); // On va mocker la socket ici
        doNothing().when(dataConnection).clearDataSocket();
        when(dataConnection.getDataSocket()).thenReturn(dataSocket);

        // Créer un spy sur PortCommand pour intercepter la création de la Socket
        PortCommand spyPortCommand = spy(portCommand);

        // Mock de la création de la Socket dans PortCommand
        doReturn(dataSocket).when(spyPortCommand).createSocket();  // Simule l'appel à new Socket()

        // Exécution de la commande avec le spy
        spyPortCommand.execute(writer, new String[]{portCommandArg});

        // Vérification que la méthode clearDataSocket et setDataSocket ont été appelées
        verify(dataConnection).clearDataSocket();
        verify(dataConnection).setDataSocket(any(Socket.class)); // Vérification de l'appel de setDataSocket

        // Vérification de la réponse attendue
        String expectedResponse = ResponseCode.COMMAND_PORT_OK + "Active data connection established to 127.0.0.1:" + (portHigh * 256 + portLow);

        System.out.println("Actual response: " + stringWriter.toString());
        System.out.println("Expected response: " + expectedResponse);

        // Vérification que la sortie contient la réponse attendue
        assertTrue(stringWriter.toString().contains(expectedResponse),
                "Expected response: " + expectedResponse + ", but got: " + stringWriter.toString());
    }



    @Test
    void testExecuteInvalidFormat() {
        // Données de test avec un format invalide
        String invalidPortCommandArg = "127,0,0,1"; // Manque le port
    
        // Exécuter la commande
        portCommand.execute(writer, new String[]{invalidPortCommandArg});
    
        // Vérifier la réponse
        String expectedResponse = ResponseCode.SYNTAX_ERROR + "Invalid address format";

        assertTrue(stringWriter.toString().contains(expectedResponse),
            "Expected response: " + expectedResponse + ", but got: " + stringWriter.toString());
    }

    // @Test
    // void testExecuteConnectionFailed() {
    //     // Données de test
    //     String ip = "127,0,0,1";
    //     int portHigh = 123;
    //     int portLow = 45;
    //     String portCommandArg = ip + "," + portHigh + "," + portLow;

    //     // Simuler une échec de connexion avec une RuntimeException
    //     doThrow(new RuntimeException("Connection failed")).when(dataConnection).setDataSocket(any(Socket.class));

    //     // Exécuter la commande
    //     portCommand.execute(writer, new String[]{portCommandArg});

    //     // Vérifier la réponse
    //     String expectedResponse = ResponseCode.CONNECTION_CLOSED + "Failed to establish active data connection: Connexion refusée";
    //     assertTrue(stringWriter.toString().contains(expectedResponse));
    // }

    @Test
    void testCreateSocket() throws IOException {
        // Créer une socket
        Socket socket = portCommand.createSocket();

        // Vérifier que la socket est créée
        assertTrue(socket instanceof Socket);
    }
}