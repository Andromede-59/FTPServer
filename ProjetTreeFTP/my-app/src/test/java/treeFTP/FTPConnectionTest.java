package treeFTP;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockftpserver.core.command.Command;
import org.mockftpserver.core.session.Session;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.command.AbstractFakeCommandHandler;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FTPConnectionTest {
    private FakeFtpServer fakeFtpServer;
    private FTPConnection ftpConnection;

    @BeforeEach
    public void setUp() throws Exception {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount("user", "password", "/data"));

        UnixFakeFileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/data"));
        fileSystem.add(new FileEntry("/data/file1.txt", "file1 content"));
        fileSystem.add(new FileEntry("/data/file2.txt", "file2 content"));
        fakeFtpServer.setFileSystem(fileSystem);

        fakeFtpServer.setServerControlPort(0);
            
        fakeFtpServer.start();

        int port = fakeFtpServer.getServerControlPort();
        ftpConnection = new FTPConnection("localhost", port);
    }

    @Test
    public void testGetReader() {
        assertNotNull(ftpConnection.getReader());
    }

    @Test
    public void testGetWriter() {
        assertNotNull(ftpConnection.getWriter());
    }

    @Test
    public void testClose() throws Exception {
        ftpConnection.close();
        assertThrows(IOException.class, () -> ftpConnection.getReader().ready());
    }

    @Test
    public void testEnterPassiveMode() {
        fakeFtpServer.setCommandHandler("PASV", new AbstractFakeCommandHandler() {
            @Override
            protected void handle(Command command, Session session) {
                session.sendReply(227, "Entering Passive Mode (127,0,0,1,4,4)");
            }
        });

        try {
            ServerSocket passiveServerSocket = new ServerSocket(1028);
            ftpConnection.enterPassiveMode();

            Socket acceptedSocket = passiveServerSocket.accept();
            assertNotNull(acceptedSocket);
            acceptedSocket.close();
            passiveServerSocket.close();
            assertNotNull(ftpConnection.getDataSocket());
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }       
    }

    @AfterEach
    public void tearDown() throws Exception {
        ftpConnection.close();
        fakeFtpServer.stop();
    }
}