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

class StorCommandTest {

    private FtpSession ftpSession;
    private DataConnection dataConnection;
    private StorCommand storCommand;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() {
        ftpSession = mock(FtpSession.class);
        dataConnection = mock(DataConnection.class);
        when(ftpSession.getDataConnection()).thenReturn(dataConnection);

        storCommand = new StorCommand(ftpSession);

        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @Test
    void testExecuteSuccessAscii() throws IOException {
        String fileName = "test.txt";
        File file = new File(fileName);

        Socket dataSocket = mock(Socket.class);
        when(dataConnection.getDataSocket()).thenReturn(dataSocket);
        when(ftpSession.getCurrentDirectory()).thenReturn(new File("."));
        when(ftpSession.getTransferType()).thenReturn(TransferType.ASCII);

        ByteArrayInputStream inputStream = new ByteArrayInputStream("Hello, World!".getBytes());
        when(dataSocket.getInputStream()).thenReturn(inputStream);

        storCommand.execute(writer, new String[]{fileName});

        verify(dataConnection).getDataSocket();
        verify(dataConnection).clearDataSocket();

        // Vérifier la réponse
        String expectedResponse = ResponseCode.TRANSFER_COMPLETED.toString();
        assertTrue(stringWriter.toString().contains(expectedResponse),
            "Expected response: " + expectedResponse + ", but got: " + stringWriter.toString());

        file.delete();
    }

    @Test
    void testExecuteSuccessBinary() throws IOException {
        String fileName = "test.bin";
        File file = new File(fileName);

        Socket dataSocket = mock(Socket.class);
        when(dataConnection.getDataSocket()).thenReturn(dataSocket);
        when(ftpSession.getCurrentDirectory()).thenReturn(new File("."));
        when(ftpSession.getTransferType()).thenReturn(TransferType.BINARY);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{0x01, 0x02, 0x03});
        when(dataSocket.getInputStream()).thenReturn(inputStream);

        storCommand.execute(writer, new String[]{fileName});

        verify(dataConnection).getDataSocket();
        verify(dataConnection).clearDataSocket();

        // Vérifier la réponse
        String expectedResponse = ResponseCode.TRANSFER_COMPLETED.toString();
        assertTrue(stringWriter.toString().contains(expectedResponse),
            "Expected response: " + expectedResponse + ", but got: " + stringWriter.toString());

        file.delete();
    }
}