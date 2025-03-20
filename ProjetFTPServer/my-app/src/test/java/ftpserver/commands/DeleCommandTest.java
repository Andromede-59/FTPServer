package ftpserver.commands;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class DeleCommandTest {

    private FtpSession ftpSession;
    private DeleCommand deleCommand;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() {
        ftpSession = mock(FtpSession.class);
        when(ftpSession.getCurrentDirectory()).thenReturn(new File(System.getProperty("java.io.tmpdir")));

        deleCommand = new DeleCommand(ftpSession);

        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @Test
    void testExecuteSyntaxError() {
        deleCommand.execute(writer, new String[]{});

        String expectedResponse = ResponseCode.SYNTAX_ERROR + "DELE requires a file name";
        assertTrue(stringWriter.toString().contains(expectedResponse),
            "Expected response: " + expectedResponse + ", but got: " + stringWriter.toString());
    }

    @Test
    void testExecuteFileNotFound() {
        String fileName = "nonexistent.txt";

        deleCommand.execute(writer, new String[]{fileName});

        String expectedResponse = ResponseCode.FILE_NOT_FOUND + "File not found: " + fileName;
        assertTrue(stringWriter.toString().contains(expectedResponse),
            "Expected response: " + expectedResponse + ", but got: " + stringWriter.toString());
    }

    @Test
    void testExecuteSuccess() throws Exception {
        String fileName = "test.txt";
        File file = new File(System.getProperty("java.io.tmpdir"), fileName);
        file.createNewFile();

        deleCommand.execute(writer, new String[]{fileName});

        String expectedResponse = ResponseCode.FILE_ACTION_SUCCESSFUL + "File deleted successfully";
        assertTrue(stringWriter.toString().contains(expectedResponse),
            "Expected response: " + expectedResponse + ", but got: " + stringWriter.toString());

        assertTrue(!file.exists(), "File should have been deleted");

        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testExecuteDeletionFailed() throws Exception {
        String fileName = "test.txt";
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File file = mock(File.class); // Mock de File

        when(file.exists()).thenReturn(true);
        when(file.isFile()).thenReturn(true);
        when(file.delete()).thenReturn(false); // Simuler un échec de suppression

        // Simulez correctement le répertoire courant
        when(ftpSession.getCurrentDirectory()).thenReturn(tmpDir);

        // Créer un mock de la méthode `createFile` pour retourner un mock de fichier
        DeleCommand spyDeleCommand = spy(deleCommand);
        when(spyDeleCommand.createFile(tmpDir, fileName)).thenReturn(file);

        // Exécuter la commande
        spyDeleCommand.execute(writer, new String[]{fileName});

        String expectedResponse = ResponseCode.FILE_ACTION_NOT_TAKEN + " File deletion failed";
        assertTrue(stringWriter.toString().contains(expectedResponse),
            "Expected response: " + expectedResponse + ", but got: " + stringWriter.toString());
    }
}
