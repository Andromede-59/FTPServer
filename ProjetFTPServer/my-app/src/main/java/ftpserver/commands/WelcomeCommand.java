package ftpserver.commands;

import java.io.PrintWriter;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Welcome command
 * Sends the welcome message to the client
 */
public class WelcomeCommand extends FtpCommand {
    public WelcomeCommand(FtpSession session) {
        super(session);
    }
    
    /**
     * Execute the command
     * @param writer the writer
     * @param args the arguments
     */
    @Override
    public void execute(PrintWriter writer, String[] args) {
        this.writeInConsole(writer, ResponseCode.WELCOME);
    }
    
}
