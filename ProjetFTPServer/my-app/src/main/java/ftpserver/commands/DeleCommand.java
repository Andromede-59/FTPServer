package ftpserver.commands;

import java.io.File;
import java.io.PrintWriter;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Dele command
 * Deletes a file
 */
public class DeleCommand extends FtpCommand {
    public DeleCommand(FtpSession session) {
        super(session);
    }

    /**
     * Create a file
     * @param parent the parent directory
     * @param fileName the file name
     * @return the file
     */
    protected File createFile(File parent, String fileName) {
        return new File(parent, fileName);
    }

    /**
     * Execute the command
     * @param writer the writer
     * @param arguments the arguments
     */
    @Override
    public void execute(PrintWriter writer, String[] arguments) {
        if (arguments.length == 0) {
            writer.println(ResponseCode.SYNTAX_ERROR + "DELE requires a file name");
            return;
        }

        File currentDir = session.getCurrentDirectory();
        File file = createFile(currentDir, arguments[0]);

        if (!file.exists() || !file.isFile()) {
            writer.println(ResponseCode.FILE_NOT_FOUND + "File not found: " + arguments[0]);
            return;
        }

        boolean deleted = file.delete();
        if (deleted) {
            writer.println(ResponseCode.FILE_ACTION_SUCCESSFUL + "File deleted successfully");
        } else {
            writer.println(ResponseCode.FILE_ACTION_NOT_TAKEN + " File deletion failed");
        }
    }
}
