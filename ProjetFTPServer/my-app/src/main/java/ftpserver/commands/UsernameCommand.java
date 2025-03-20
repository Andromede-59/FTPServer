package ftpserver.commands;

import java.io.PrintWriter;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Username command
 * Handles the username command
 */
public class UsernameCommand extends FtpCommand {
    public UsernameCommand(FtpSession session) {
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
            throw new RuntimeException("Username command must have exactly one argument");
        } else if (!args[0].equals("anonymous")) {
            this.writeInConsole(writer, ResponseCode.USERNAME_INCORRECT);
            throw new RuntimeException(ResponseCode.USERNAME_INCORRECT.toString());
        }
        this.writeInConsole(writer, ResponseCode.USERNAME_OK);
    }
}