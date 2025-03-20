package ftpserver.commands;

import ftpserver.DataConnection;
import ftpserver.FtpSession;
import ftpserver.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ListCommandTest {

    private FtpSession ftpSession;
    private DataConnection dataConnection;
    private ListCommand listCommand;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() {
        ftpSession = mock(FtpSession.class);
        dataConnection = mock(DataConnection.class);
        when(ftpSession.getDataConnection()).thenReturn(dataConnection);

        listCommand = new ListCommand(ftpSession);

        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @Test
    void testExecuteNoDataConnection() {
        when(dataConnection.getDataSocket()).thenReturn(null);

        listCommand.execute(writer, new String[]{});

        String expectedResponse = ResponseCode.NO_DATA_CONNECTION.toString();
        assertTrue(stringWriter.toString().contains(expectedResponse),
            "Expected response: " + expectedResponse + ", but got: " + stringWriter.toString());
    }

    @Test
    void testExecuteEmptyDirectory() throws IOException {
        Socket dataSocket = mock(Socket.class);
        when(dataConnection.getDataSocket()).thenReturn(dataSocket);

        File currentDirectory = Files.createTempDirectory("testDir").toFile();
        when(ftpSession.getCurrentDirectory()).thenReturn(currentDirectory);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(dataSocket.getOutputStream()).thenReturn(outputStream);

        listCommand.execute(writer, new String[]{});

        String expectedResponse = ResponseCode.DIRECTORY_SEND_OK.toString();
        assertTrue(stringWriter.toString().contains(expectedResponse),
            "Expected response: " + expectedResponse + ", but got: " + stringWriter.toString());

        currentDirectory.delete();
    }

    @Test
    void testExecuteWithFiles() throws IOException {
        Socket dataSocket = mock(Socket.class);
        when(dataConnection.getDataSocket()).thenReturn(dataSocket);

        File currentDirectory = Files.createTempDirectory("testDir").toFile();
        File testFile = new File(currentDirectory, "test.txt");
        testFile.createNewFile();

        when(ftpSession.getCurrentDirectory()).thenReturn(currentDirectory);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(dataSocket.getOutputStream()).thenReturn(outputStream);

        listCommand.execute(writer, new String[]{});

        String expectedResponse = ResponseCode.DIRECTORY_SEND_OK.toString();
        assertTrue(stringWriter.toString().contains(expectedResponse),
            "Expected response: " + expectedResponse + ", but got: " + stringWriter.toString());

        String fileListing = outputStream.toString();
        assertTrue(fileListing.contains("test.txt"),
            "Expected file listing to contain 'test.txt', but got: " + fileListing);

        testFile.delete();
        currentDirectory.delete();
    }

    @Test
    void testExecuteConnectionError() throws IOException {
        Socket dataSocket = mock(Socket.class);
        when(dataConnection.getDataSocket()).thenReturn(dataSocket);

        File currentDirectory = Files.createTempDirectory("testDir").toFile();
        when(ftpSession.getCurrentDirectory()).thenReturn(currentDirectory);

        when(dataSocket.getOutputStream()).thenThrow(new IOException("Connection failed"));

        listCommand.execute(writer, new String[]{});

        String expectedResponse = ResponseCode.CONNECTION_CLOSED.toString();
        assertTrue(stringWriter.toString().contains(expectedResponse),
            "Expected response: " + expectedResponse + ", but got: " + stringWriter.toString());

        currentDirectory.delete();
    }
}