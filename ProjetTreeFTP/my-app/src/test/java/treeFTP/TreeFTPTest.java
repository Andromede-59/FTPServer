package treeFTP;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockftpserver.core.command.Command;
import org.mockftpserver.core.session.Session;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.command.AbstractFakeCommandHandler;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TreeFTPTest {

    private FakeFtpServer fakeFtpServer;
    private TreeFTP treeFTP;
    private OutputHandler outputHandler;
    private Connection connection;
    private UserManager userManager;

    @BeforeEach
    public void setUp() throws Exception {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.setServerControlPort(0); 

        UserAccount userAccount = new UserAccount("anonymous", "anonymous", "/");
        fakeFtpServer.addUserAccount(userAccount);

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/data"));
        fileSystem.add(new FileEntry("/data/file1.txt"));
        fileSystem.add(new DirectoryEntry("/data/subdir"));
        fileSystem.add(new FileEntry("/data/subdir/file2.txt"));
        fakeFtpServer.setFileSystem(fileSystem);

        fakeFtpServer.start();

        connection = new FTPConnection("localhost", fakeFtpServer.getServerControlPort());
        outputHandler = new ConsoleOutputHandler();
        userManager = new UserManager(connection.getReader(), connection.getWriter());
        
        String input = "";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        userManager.login();

        treeFTP = new TreeFTP(connection, outputHandler, false);
    }

    @Test
    public void testGenerateTree() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        treeFTP.generateTree("/data");

        String output = outputStream.toString();
        assertTrue(output.contains("file1.txt"));
        assertTrue(output.contains("subdir"));
        assertTrue(output.contains("file2.txt"));
    }

    @Test
    public void testGenerateTreeWithDepth() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        String input = "1";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);
        treeFTP = new TreeFTP(connection, outputHandler, true);
        treeFTP.generateTree("/data");

        // Verify the output
        String output = outputStream.toString();
        assertTrue(output.contains("file1.txt"));
        assertTrue(output.contains("subdir"));
        assertTrue(output.contains("file2.txt"));
    }

    @Test
    public void testGenerateTreeWithNullDepthLimit() {
        String input = "";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);
        assertThrows(RuntimeException.class, () -> new TreeFTP(connection, outputHandler, true));
    }

    @Test
    public void testReconnect() throws IOException {
        TreeFTP treeFTP = new TreeFTP(connection, outputHandler, false);
        fakeFtpServer.setCommandHandler("CWD", new AbstractFakeCommandHandler() {
            @Override
            protected void handle(Command command, Session session) {
                throw new RuntimeException("IOException");
            }
        });

        assertThrows(RuntimeException.class, () -> treeFTP.generateTree("/data"));
    }

    @AfterEach
    public void tearDown() throws Exception {
        fakeFtpServer.stop();
        connection.close();
    }
}
