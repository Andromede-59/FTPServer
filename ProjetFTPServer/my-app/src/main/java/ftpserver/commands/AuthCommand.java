package ftpserver.commands;

import java.io.PrintWriter;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Auth command
 * Handles the auth command
 */
public class AuthCommand extends FtpCommand {
    public AuthCommand(FtpSession session) {
        super(session);
    }

    /**
     * Execute the command
     * @param writer the writer
     * @param args the arguments
     */
    @Override
    public void execute(PrintWriter writer, String[] args) {
        this.writeInConsole(writer, ResponseCode.AUTH_COMMAND_OK);
    }

}
