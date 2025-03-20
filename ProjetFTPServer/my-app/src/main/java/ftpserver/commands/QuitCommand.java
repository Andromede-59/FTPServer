package ftpserver.commands;

import java.io.PrintWriter;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Quit command
 * Handles the quit command
 */
public class QuitCommand extends FtpCommand {
    public QuitCommand(FtpSession session) {
        super(session);
    }

    /**
     * Execute the command
     * @param writer the writer
     * @param args the arguments
     */
    @Override
    public void execute(PrintWriter writer, String[] args) {
        this.writeInConsole(writer,ResponseCode.GOODBYE);
    }
}