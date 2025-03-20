package ftpserver.commands;

import ftpserver.FtpSession;
import ftpserver.SupportedCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandFactoryTest {

    private FtpSession ftpSession;
    private CommandFactory commandFactory;

    @BeforeEach
    void setUp() {
        // Initialisation du mock de FtpSession
        ftpSession = mock(FtpSession.class);

        // Initialisation de la CommandFactory
        commandFactory = new CommandFactory(ftpSession);
    }

    @Test
    void testGetCommandKnownCommand() {
        // Test pour une commande connue (USER)
        FtpCommand command = commandFactory.getCommand("USER");
        assertTrue(command instanceof UsernameCommand, "Expected UsernameCommand, but got: " + command.getClass().getSimpleName());

        // Test pour une autre commande connue (LIST)
        command = commandFactory.getCommand("LIST");
        assertTrue(command instanceof ListCommand, "Expected ListCommand, but got: " + command.getClass().getSimpleName());
    }

    @Test
    void testGetCommandUnknownCommand() {
        // Test pour une commande inconnue
        FtpCommand command = commandFactory.getCommand("UNKNOWN");
        assertTrue(command instanceof UnknownCommand, "Expected UnknownCommand, but got: " + command.getClass().getSimpleName());
    }

    @Test
    void testGetCommandCaseInsensitive() {
        // Test pour vérifier que la commande est insensible à la casse
        FtpCommand command = commandFactory.getCommand("user"); // minuscules
        assertTrue(command instanceof UsernameCommand, "Expected UsernameCommand, but got: " + command.getClass().getSimpleName());

        command = commandFactory.getCommand("LiSt"); // mélange de majuscules et minuscules
        assertTrue(command instanceof ListCommand, "Expected ListCommand, but got: " + command.getClass().getSimpleName());
    }

    @Test
    void testGetCommandAllSupportedCommands() {
        for (SupportedCommand supportedCommand : SupportedCommand.values()) {
            FtpCommand command = commandFactory.getCommand(supportedCommand.name());
            assertNotNull(command, "Command should not be null for: " + supportedCommand.name());
        }
    }
}