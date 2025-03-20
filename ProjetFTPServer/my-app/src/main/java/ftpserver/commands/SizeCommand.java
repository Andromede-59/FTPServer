package ftpserver.commands;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

/**
 * Size command
 * Handles the size command
 */
public class SizeCommand extends FtpCommand {
    public SizeCommand(FtpSession session) {
        super(session);
    }

    /**
     * Execute the command
     * @param writer the writer
     * @param args the arguments
     */
    @Override
    public void execute(PrintWriter writer, String[] args) {
        if (!session.isAuthenticated()) {
            this.writeInConsole(writer, ResponseCode.FILE_ACTION_NOT_TAKEN, "User not logged in.");
            return;
        }

        if (args.length < 1) {
            this.writeInConsole(writer, ResponseCode.SYNTAX_ERROR, "Usage: SIZE <filename>");
            return;
        }

        String filePath = args[0];
        try {
            File file = session.resolveFile(filePath);
            if (!file.exists() || !file.isFile()) {
                this.writeInConsole(writer, ResponseCode.FILE_ACTION_NOT_TAKEN, "File not found or not a regular file.");
                return;
            }

            long fileSize = Files.size(file.toPath());
            this.writeInConsole(writer, ResponseCode.FILE_STATUS, "File size: " + fileSize);

        } catch (IOException e) {
            this.writeInConsole(writer, ResponseCode.FILE_ACTION_NOT_TAKEN, "Error retrieving file size.");
        } catch (SecurityException e) {
            this.writeInConsole(writer, ResponseCode.ACCESS_DENIED);
        }
    }
}