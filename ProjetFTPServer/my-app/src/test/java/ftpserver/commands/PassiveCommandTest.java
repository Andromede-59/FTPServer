package ftpserver.commands;

import ftpserver.ClientHandler;
import ftpserver.DataConnection;
import ftpserver.FtpSession;
import ftpserver.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PassiveCommandTest {

    private ClientHandler clientHandler;
    private FtpSession ftpSession;
    private PassiveCommand passiveCommand;
    private StringWriter stringWriter;
    private PrintWriter writer;
    private DataConnection dataConnection;

    @BeforeEach
    void setUp() {
        clientHandler = mock(ClientHandler.class);
        ftpSession = mock(FtpSession.class);
        when(clientHandler.getSession()).thenReturn(ftpSession);
        passiveCommand = new PassiveCommand(ftpSession);
        dataConnection = mock(DataConnection.class);
        when(ftpSession.getDataConnection()).thenReturn(dataConnection);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @Test
    void testExecuteSuccess() throws Exception {
        int port = 2121;
        Socket dataSocket = mock(Socket.class);

        when(dataConnection.startPassiveMode()).thenReturn(port);
        when(dataConnection.acceptPassiveConnection()).thenReturn(dataSocket);

        passiveCommand.execute(writer, new String[]{});

        verify(dataConnection).startPassiveMode();
        verify(dataConnection).acceptPassiveConnection();
        verify(dataConnection).setDataSocket(dataSocket);

        String expectedResponse = String.format("%s(127,0,0,1,%d,%d)",
                ResponseCode.ENTER_PASSIVE_MODE.toString(), port / 256, port % 256);
        assertTrue(stringWriter.toString().contains(expectedResponse));
    }

    @Test
    void testExecuteIOException() throws Exception {
        when(dataConnection.startPassiveMode()).thenThrow(new IOException("Test Exception"));

        passiveCommand.execute(writer, new String[]{});

        verify(dataConnection).startPassiveMode();
        verify(dataConnection, never()).acceptPassiveConnection();
        verify(dataConnection, never()).setDataSocket(any(Socket.class));

        assertTrue(stringWriter.toString().contains(ResponseCode.CANNOT_ENTER_PASSIVE_MODE.toString()));
    }
}