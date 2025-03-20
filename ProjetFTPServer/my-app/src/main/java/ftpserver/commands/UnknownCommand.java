package ftpserver.commands;

import java.io.PrintWriter;

import ftpserver.ResponseCode;

/**
 * Unknown command
 * Handles the unknown command
 */
public class UnknownCommand extends FtpCommand {
    public UnknownCommand() {
        super(null);
    }
    
    /**
     * Execute the command
     * @param writer the writer
     * @param args the arguments
     */
    @Override
    public void execute(PrintWriter writer, String[] args) {
        this.writeInConsole(writer, ResponseCode.UNKNOWN_COMMAND);
    }
    
}
