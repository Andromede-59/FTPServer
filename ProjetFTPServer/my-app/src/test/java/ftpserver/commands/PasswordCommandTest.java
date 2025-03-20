package ftpserver.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ftpserver.ClientHandler;
import ftpserver.DataConnection;
import ftpserver.FtpSession;
import ftpserver.ResponseCode;

public class PasswordCommandTest {
    
    private ClientHandler clientHandler;
    private FtpSession ftpSession;
    private DataConnection dataConnection;

    @BeforeEach
    void setUp() {
        clientHandler = mock(ClientHandler.class);
        ftpSession = mock(FtpSession.class);
        when(clientHandler.getSession()).thenReturn(ftpSession);
        dataConnection = mock(DataConnection.class);
        when(ftpSession.getDataConnection()).thenReturn(dataConnection);
    }


    @Test
    public void executeFailedWrongPassword() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter, true);

        PasswordCommand passwordCommand = new PasswordCommand(ftpSession);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            passwordCommand.execute(writer, new String[] { "notanonymous" });
        });

        assertEquals(ResponseCode.PASSWORD_INCORRECT.toString(), stringWriter.toString().trim());

        assertEquals(ResponseCode.PASSWORD_INCORRECT.toString(), exception.getMessage());
    }

    @Test
    public void executeFailedMissArg() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter, true);

        PasswordCommand passwordCommand = new PasswordCommand(ftpSession);

        assertThrows(RuntimeException.class, () -> {
            passwordCommand.execute(writer, new String[] {});
        });
    }

}
