package ftpserver.commands;

import java.io.PrintWriter;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Syst command
 * Handles the syst command
 */
public class SystCommand extends FtpCommand {
    public SystCommand(FtpSession session) {
        super(session);
    }
    

    /**
     * Execute the command
     * @param writer the writer
     * @param args the arguments
     */
    @Override
    public void execute(PrintWriter writer, String[] args) {
        String osName = System.getProperty("os.name").toUpperCase();
        String response;

        if (osName.contains("WINDOWS")) {
            response = "Windows NT";
        } else if (osName.contains("LINUX")) {
            response = "UNIX Type: L8";
        } else if (osName.contains("MAC")) {
            response = "MacOS";
        } else {
            response = "UNKNOWN";
        }

        this.writeInConsole(writer, ResponseCode.SYST_COMMAND_OK, response);
    }
}
