package ftpserver.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import ftpserver.DataConnection;
import ftpserver.FtpSession;
import ftpserver.ResponseCode;

public class SizeCommandTest {
    private FtpSession ftpSession;
    private DataConnection dataConnection;
    private SizeCommand sizeCommand;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() {
        ftpSession = mock(FtpSession.class);
        dataConnection = mock(DataConnection.class);
        when(ftpSession.getDataConnection()).thenReturn(dataConnection);

        sizeCommand = new SizeCommand(ftpSession);

        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @Test 
    public void testExecuteFailedUserNotLoggedIn() {
        sizeCommand.execute(writer, new String[0]);
        String expectedResponse = ResponseCode.FILE_ACTION_NOT_TAKEN.toString() + "User not logged in.";
        assertTrue(stringWriter.toString().contains(expectedResponse),
            "Expected response: " + expectedResponse + ", but got: " + stringWriter.toString());
    }

    @Nested
    class AuthenticatedTests {

        @BeforeEach
        void setUpAuthenticated() {
            when(ftpSession.isAuthenticated()).thenReturn(true);
        }

        @Test
        public void testExecuteSuccess() throws IOException {
            File tempFile = File.createTempFile("new_file", ".tmp");
            tempFile.deleteOnExit(); // Ensure the file is deleted after the test

            when(ftpSession.resolveFile("new_file")).thenReturn(tempFile);
            sizeCommand.execute(writer, new String[] {"new_file"});
            String expectedResponse = ResponseCode.FILE_STATUS.toString() + "File size: 0";
            assertTrue(stringWriter.toString().contains(expectedResponse),
                "Expected response: " + expectedResponse + ", but got: " + stringWriter.toString());
        }
    }
}
