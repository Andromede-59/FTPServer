package ftpserver.commands;

import java.io.PrintWriter;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Ftp command
 * Abstract class for all the commands
 */
public abstract class FtpCommand {
    protected FtpSession session;
    
    public FtpCommand(FtpSession session) {
        this.session = session;
    }

    /**
     * Execute the command
     * @param writer the writer
     * @param args the arguments
     */
    public abstract void execute(PrintWriter writer, String[] args);

    /**
     * Write in the console
     * @param writer the writer
     * @param responseCode the response code
     * @param args the arguments
     */
    public void writeInConsole(PrintWriter writer, ResponseCode responseCode, String... args) {
        writer.print(responseCode.toString() + String.join(" ", args) + "\r\n");
        writer.flush();
    }

    /**
     * Write the content
     * @param writer the writer
     * @param content the content
     */
    public void writeContent(PrintWriter writer, String content) {
        writer.print(content + "\r\n");
        writer.flush();
    }
}
