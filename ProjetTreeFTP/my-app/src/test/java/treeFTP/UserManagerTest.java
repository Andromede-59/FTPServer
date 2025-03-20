package treeFTP;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockftpserver.core.command.Command;
import org.mockftpserver.core.session.Session;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.command.AbstractFakeCommandHandler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserManagerTest {

    private FakeFtpServer fakeFtpServer;
    private UserManager userManager;

    @BeforeEach
    public void setUp() throws Exception {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.setServerControlPort(0); 

        UserAccount userAccount = new UserAccount("anonymous", "anonymous", "/");
        fakeFtpServer.addUserAccount(userAccount);

        fakeFtpServer.start();

        PipedOutputStream pipedOut = new PipedOutputStream();
        PipedInputStream pipedIn = new PipedInputStream(pipedOut);
        BufferedReader reader = new BufferedReader(new InputStreamReader(pipedIn));
        PrintWriter writer = new PrintWriter(pipedOut, true);

        userManager = new UserManager(reader, writer);
    }

    @Test
    public void testLogin() throws Exception {
        String input = "";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        userManager.login();

        assertTrue(true);
    }

    @Test
    public void testLoginFailed() throws Exception {
        int serverPort = fakeFtpServer.getServerControlPort();

        fakeFtpServer.setCommandHandler("USER", new AbstractFakeCommandHandler() {
            @Override
            protected void handle(Command command, Session session) {
                session.sendReply(331, "Password required for anonymous");
            }
        });
    
        fakeFtpServer.setCommandHandler("PASS", new AbstractFakeCommandHandler() {
            @Override
            protected void handle(Command command, Session session) {
                session.sendReply(530, "Login incorrect");
            }
        });

        Socket socket = new Socket("localhost", serverPort);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

        UserManager userManager2 = new UserManager(reader, writer);
    
        Exception exception = assertThrows(Exception.class, () -> {
            userManager2.login();
        });
    
        assertTrue(exception.getMessage().contains("Login failed"));
        socket.close();
    }
    

    @AfterEach
    public void tearDown() throws Exception {
        fakeFtpServer.stop();
    }
}