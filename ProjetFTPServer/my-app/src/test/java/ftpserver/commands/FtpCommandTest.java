package ftpserver.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FtpCommandTest {

    private FtpSession ftpSession;
    private Path tempDirectory;

    @BeforeAll
    void setUp() throws Exception {
        tempDirectory = Files.createTempDirectory("ftpserver_test");
        ftpSession = new FtpSession(tempDirectory.toFile());
        ftpSession.setCurrentDirectory(tempDirectory.toFile());
        ftpSession.setAuthenticated(true);
    }

    @AfterAll
    void tearDown() throws Exception {
        Files.walk(tempDirectory)
             .sorted((a, b) -> b.compareTo(a))
             .forEach(path -> {
                 try {
                     Files.delete(path);
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             });
    }

    Stream<Arguments> provideCommands() {
        return Stream.of(
            Arguments.of(new QuitCommand(ftpSession), ResponseCode.GOODBYE.toString(), new String[0]),
            Arguments.of(new UsernameCommand(ftpSession), ResponseCode.USERNAME_OK.toString(), new String[] {"anonymous"}),
            Arguments.of(new PasswordCommand(ftpSession), ResponseCode.PASSWORD_CORRECT.toString(), new String[] {"anonymous"}),
            Arguments.of(new AuthCommand(ftpSession), ResponseCode.AUTH_COMMAND_OK.toString(), new String[0]),
            Arguments.of(new MkdCommand(ftpSession), ResponseCode.FILE_ACTION_SUCCESSFUL.toString() + "Directory created successfully", new String[] {"new_dir"}),
            Arguments.of(new MkdCommand(ftpSession), ResponseCode.FILE_ACTION_NOT_TAKEN.toString() + "Directory already exists", new String[] {"new_dir"}),
            Arguments.of(new SizeCommand(ftpSession), ResponseCode.SYNTAX_ERROR.toString() + "Usage: SIZE <filename>", new String[0]),
            Arguments.of(new SizeCommand(ftpSession), ResponseCode.FILE_ACTION_NOT_TAKEN.toString() + "File not found or not a regular file.", new String[] {"file.txt"}),
            Arguments.of(new RnfrCommand(ftpSession), ResponseCode.FILE_ACTION_PENDING.toString() + "RNFR accepted, waiting for RNTO", new String[] {"new_dir"}),
            Arguments.of(new RntoCommand(ftpSession), ResponseCode.FILE_ACTION_SUCCESSFUL.toString() + "File renamed successfully", new String[] {"renamed_dir"}),
            Arguments.of(new CwdCommand(ftpSession), ResponseCode.DIRECTORY_SUCCESSFULLY_CHANGED.toString() + "/renamed_dir", new String[] {"renamed_dir"}),
            Arguments.of(new CdupCommand(ftpSession), ResponseCode.DIRECTORY_SUCCESSFULLY_CHANGED.toString() + "/", new String[0]),
            Arguments.of(new CdupCommand(ftpSession), ResponseCode.ACCESS_DENIED.toString(), new String[0]),
            Arguments.of(new PwdCommand(ftpSession), ResponseCode.PRINT_WORKING_DIRECTORY.toString() + "/", new String[0]),
            Arguments.of(new RmdCommand(ftpSession), ResponseCode.FILE_ACTION_SUCCESSFUL.toString() + "Directory deleted successfully", new String[] {"renamed_dir"}),
            Arguments.of(new RmdCommand(ftpSession), ResponseCode.SYNTAX_ERROR.toString() + "RMD requires a directory name", new String[0]),
            Arguments.of(new RmdCommand(ftpSession), ResponseCode.FILE_NOT_FOUND.toString() + "Directory not found: renamed_dir", new String[] {"renamed_dir"}),
            Arguments.of(new TypeCommand(ftpSession), ResponseCode.TYPE_SET.toString() + "ASCII", new String[] {"A"}),
            Arguments.of(new FeatCommand(ftpSession), ResponseCode.FEAT_COMMAND_OK.toString() + "\r\n" + ResponseCode.FEAT_COMMAND_ARGS.toString() + " UTF8\r\n PASV\r\n EPSV\r\n MLST\r\n MLSD\r\n REST STREAM\r\n SIZE\r\n MDTM\r\n TVFS\r\n" + ResponseCode.FEAT_COMMAND_END.toString(), new String[0]),
            Arguments.of(new OptsCommand(ftpSession), ResponseCode.OPTS_COMMAND_OK.toString() + "UTF-8 mode enabled.", new String[] {"UTF8", "ON"}),
            Arguments.of(new SystCommand(ftpSession), ResponseCode.SYST_COMMAND_OK.toString() + "UNIX Type: L8", new String[0]),
            Arguments.of(new UnknownCommand(), ResponseCode.UNKNOWN_COMMAND.toString(), new String[0]),
            Arguments.of(new WelcomeCommand(ftpSession), ResponseCode.WELCOME.toString(), new String[0])
        );
    }

    @ParameterizedTest
    @MethodSource("provideCommands")
    void testCommands(FtpCommand command, String expectedResponse, String[] args) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter, true);

        command.execute(writer, args);

        assertEquals(expectedResponse, stringWriter.toString().trim(), "Test failed for command: " + command.getClass().getSimpleName());
    }
}