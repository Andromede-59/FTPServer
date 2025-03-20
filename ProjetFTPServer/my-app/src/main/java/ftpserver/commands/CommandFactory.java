package ftpserver.commands;

import ftpserver.FtpSession;
import ftpserver.SupportedCommand;
import java.util.HashMap;
import java.util.Map;

/**
 * Command factory
 * Creates the command based on the command string
 */
public class CommandFactory {
    private final Map<SupportedCommand, FtpCommand> commands = new HashMap<>();
    private final FtpSession session;

    public CommandFactory(FtpSession session) {
        this.session = session;
        initializeCommands();
    }

    /**
     * Initialize the commands
     */
    private void initializeCommands() {
        commands.put(SupportedCommand.USER, new UsernameCommand(session));
        commands.put(SupportedCommand.PASS, new PasswordCommand(session));
        commands.put(SupportedCommand.LIST, new ListCommand(session));
        commands.put(SupportedCommand.PASV, new PassiveCommand(session));
        commands.put(SupportedCommand.PWD, new PwdCommand(session));
        commands.put(SupportedCommand.CWD, new CwdCommand(session));
        commands.put(SupportedCommand.CDUP, new CdupCommand(session));
        commands.put(SupportedCommand.TYPE, new TypeCommand(session));
        commands.put(SupportedCommand.RETR, new RetrCommand(session));
        commands.put(SupportedCommand.STOR, new StorCommand(session));
        commands.put(SupportedCommand.AUTH, new AuthCommand(session));
        commands.put(SupportedCommand.SYST, new SystCommand(session));
        commands.put(SupportedCommand.QUIT, new QuitCommand(session));
        commands.put(SupportedCommand.DELE, new DeleCommand(session));
        commands.put(SupportedCommand.MKD, new MkdCommand(session));
        commands.put(SupportedCommand.RMD, new RmdCommand(session));
        commands.put(SupportedCommand.RNFR, new RnfrCommand(session));
        commands.put(SupportedCommand.RNTO, new RntoCommand(session));
        commands.put(SupportedCommand.PORT, new PortCommand(session));
        commands.put(SupportedCommand.UNKNOWN, new UnknownCommand());
        commands.put(SupportedCommand.SIZE, new SizeCommand(session));
    }

    /**
     * Get the command
     * @param commandStr the command string
     * @return the command
     */
    public FtpCommand getCommand(String commandStr) {
        try {
            SupportedCommand cmd = SupportedCommand.valueOf(commandStr.toUpperCase());
            return commands.getOrDefault(cmd, new UnknownCommand());
        } catch (IllegalArgumentException e) {
            return new UnknownCommand();
        }
    }
}