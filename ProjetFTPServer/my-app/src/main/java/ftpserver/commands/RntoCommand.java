package ftpserver.commands;

import java.io.File;
import java.io.PrintWriter;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Rnto command
 * Handles the rnto command
 */
public class RntoCommand extends FtpCommand {
    public RntoCommand(FtpSession session) {
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
            this.writeInConsole(writer, ResponseCode.SYNTAX_ERROR, "RNTO requires a new file name");
            return;
        }

        File oldFile = session.getRenameFile();
        if (oldFile == null) {
            this.writeInConsole(writer, ResponseCode.BAD_SEQUENCE, "RNFR must be sent before RNTO");
            return;
        }

        File newFile = new File(oldFile.getParent(), args[0]);

        if (oldFile.renameTo(newFile)) {
            this.writeInConsole(writer, ResponseCode.FILE_ACTION_SUCCESSFUL, "File renamed successfully");
        } else {
            this.writeInConsole(writer, ResponseCode.FILE_ACTION_NOT_TAKEN, "Rename failed");
        }

        session.setRenameFile(null);
    }
}
