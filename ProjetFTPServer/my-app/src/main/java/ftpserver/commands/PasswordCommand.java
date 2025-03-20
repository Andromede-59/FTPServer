package ftpserver.commands;

import java.io.PrintWriter;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Password command
 * Handles the password command
 */
public class PasswordCommand extends FtpCommand {
    public PasswordCommand(FtpSession session) {
        super(session);
    }

    /**
     * Execute the command
     * @param writer the writer
     * @param args the arguments
     */
    @Override
    public void execute(PrintWriter writer, String[] args) {
        if(args.length != 1) {
            throw new RuntimeException("Invalid number of arguments");
        } else if (!args[0].equals("anonymous")) {
            this.writeInConsole(writer,ResponseCode.PASSWORD_INCORRECT);
            throw new RuntimeException(ResponseCode.PASSWORD_INCORRECT.toString());
        }
        this.writeInConsole(writer,ResponseCode.PASSWORD_CORRECT);
        session.setAuthenticated(true);
    }
}