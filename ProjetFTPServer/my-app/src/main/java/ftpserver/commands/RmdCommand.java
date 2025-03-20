package ftpserver.commands;

import java.io.File;
import java.io.PrintWriter;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Rmd command
 * Handles the rmd command
 */
public class RmdCommand extends FtpCommand {
    public RmdCommand(FtpSession session) {
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
            this.writeInConsole(writer, ResponseCode.SYNTAX_ERROR, "RMD requires a directory name");
            return;
        }

        File dir = args[0].startsWith("/") ? new File(session.getRootDirectory(), args[0]) : new File(session.getCurrentDirectory(), args[0]);

        if (!dir.exists() || !dir.isDirectory()) {
            this.writeInConsole(writer, ResponseCode.FILE_NOT_FOUND, "Directory not found: " + args[0]);
            return;
        }

        if (deleteDirectory(dir)) {
            this.writeInConsole(writer, ResponseCode.FILE_ACTION_SUCCESSFUL, "Directory deleted successfully");
        } else {
            this.writeInConsole(writer, ResponseCode.FILE_ACTION_NOT_TAKEN, "Directory deletion failed");
        }
    }

    /**
     * Delete a directory and all its contents
     * @param dir the directory
     * @return true if the directory was deleted successfully
     */
    private boolean deleteDirectory(File dir) {
        File[] contents = dir.listFiles();
        if (contents != null) {
            for (File file : contents) {
                if (!deleteDirectory(file)) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}