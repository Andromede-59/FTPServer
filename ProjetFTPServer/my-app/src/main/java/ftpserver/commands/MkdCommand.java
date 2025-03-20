package ftpserver.commands;

import java.io.File;
import java.io.PrintWriter;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Mkd command
 * Handles the mkd command
 */
public class MkdCommand extends FtpCommand {
    public MkdCommand(FtpSession session) {
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
            this.writeInConsole(writer, ResponseCode.SYNTAX_ERROR, "MKD requires a directory name");
            return;
        } else if (args[0].equals(".") || args[0].equals("..") || args[0].contains("/") || args[0].equals("ftpserver_folder")) {
            this.writeInConsole(writer, ResponseCode.FILE_ACTION_NOT_TAKEN, "Invalid directory name");
            return;
        }

        File dir = new File(session.getCurrentDirectory(), args[0]);

        if (dir.exists()) {
            this.writeInConsole(writer, ResponseCode.FILE_ACTION_NOT_TAKEN, "Directory already exists");
            return;
        }

        if (dir.mkdir()) {
            this.writeInConsole(writer, ResponseCode.FILE_ACTION_SUCCESSFUL, "Directory created successfully");
        } else {
            this.writeInConsole(writer, ResponseCode.FILE_ACTION_NOT_TAKEN, "Directory creation failed");
        }
    }
}