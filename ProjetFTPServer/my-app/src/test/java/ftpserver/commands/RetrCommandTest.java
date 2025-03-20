package ftpserver.commands;

import ftpserver.DataConnection;
import ftpserver.FtpSession;
import ftpserver.ResponseCode;
import ftpserver.TransferType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class RetrCommandTest {

    private FtpSession ftpSession;
    private DataConnection dataConnection;
    private RetrCommand retrCommand;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() {
        ftpSession = mock(FtpSession.class);
        dataConnection = mock(DataConnection.class);
        when(ftpSession.getDataConnection()).thenReturn(dataConnection);

        retrCommand = new RetrCommand(ftpSession);

        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @Test
    void testExecuteSuccessAscii() throws IOException {
        String fileName = "test.txt";
        File file = new File(fileName);
        file.createNewFile(); // Crée un fichier temporaire

        Socket dataSocket = mock(Socket.class);
        when(dataConnection.getDataSocket()).thenReturn(dataSocket);
        when(ftpSession.getCurrentDirectory()).thenReturn(new File("."));
        when(ftpSession.getTransferType()).thenReturn(TransferType.ASCII);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(dataSocket.getOutputStream()).thenReturn(outputStream);

        retrCommand.execute(writer, new String[]{fileName});

        verify(dataConnection).getDataSocket();
        verify(dataConnection).clearDataSocket();

        String expectedResponse = ResponseCode.RETR_SUCCESSFUL.toString();
        assertTrue(stringWriter.toString().contains(expectedResponse),
            "Expected response: " + expectedResponse + ", but got: " + stringWriter.toString());

        file.delete();
    }

    @Test
    void testExecuteSuccessBinary() throws IOException {
        String fileName = "test.bin";
        File file = new File(fileName);
        file.createNewFile();

        Socket dataSocket = mock(Socket.class);
        when(dataConnection.getDataSocket()).thenReturn(dataSocket);
        when(ftpSession.getCurrentDirectory()).thenReturn(new File("."));
        when(ftpSession.getTransferType()).thenReturn(TransferType.BINARY);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(dataSocket.getOutputStream()).thenReturn(outputStream);

        retrCommand.execute(writer, new String[]{fileName});

        verify(dataConnection).getDataSocket();
        verify(dataConnection).clearDataSocket();

        String expectedResponse = ResponseCode.RETR_SUCCESSFUL.toString();
        assertTrue(stringWriter.toString().contains(expectedResponse),
            "Expected response: " + expectedResponse + ", but got: " + stringWriter.toString());

        file.delete();
    }

    @Test
    void testExecuteFileNotFound() {
        String fileName = "nonexistent.txt";

        when(ftpSession.getCurrentDirectory()).thenReturn(new File("."));

        retrCommand.execute(writer, new String[]{fileName});

        String expectedResponse = ResponseCode.FILE_NOT_FOUND.toString();
        assertTrue(stringWriter.toString().contains(expectedResponse),
            "Expected response: " + expectedResponse + ", but got: " + stringWriter.toString());
    }
}