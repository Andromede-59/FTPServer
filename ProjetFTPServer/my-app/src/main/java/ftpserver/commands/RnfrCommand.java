package ftpserver.commands;

import java.io.File;
import java.io.PrintWriter;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Rnfr command
 * Handles the rnfr command
 */
public class RnfrCommand extends FtpCommand {
    public RnfrCommand(FtpSession session) {
        super(session);
    }

    /**
     * Execute the command
     * @param writer the writer
     * @param args the arguments
     */
    @Override
    public void execute(PrintWriter writer, String[] args) {
        if (args.length != 1) {
            this.writeInConsole(writer, ResponseCode.SYNTAX_ERROR, "RNFR requires a file or directory name");
            return;
        }

        File file = new File(session.getCurrentDirectory(), args[0]);

        if (!file.exists()) {
            this.writeInConsole(writer, ResponseCode.FILE_NOT_FOUND, "File not found: " + args[0]);
            return;
        }

        session.setRenameFile(file);
        this.writeInConsole(writer, ResponseCode.FILE_ACTION_PENDING, "RNFR accepted, waiting for RNTO");
    }
}
