package ftpserver.commands;

import java.io.File;
import java.io.PrintWriter;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Pwd command
 * Handles the pwd command
 */
public class PwdCommand extends FtpCommand {
    private File currentDirectory;

    public PwdCommand(FtpSession session) {
        super(session);
        this.currentDirectory = session.getCurrentDirectory();
    }

    /**
     * Execute the command
     * @param writer the writer
     * @param args the arguments
     */
    @Override
    public void execute(PrintWriter writer, String[] args) {
        this.currentDirectory=this.session.getCurrentDirectory();
        String relativePath = currentDirectory.getName().equals(session.getRootDirectory().getName()) ? "/" 
                            : currentDirectory.getAbsolutePath().replace(session.getRootDirectory().getAbsolutePath(), "");
        this.writeInConsole(writer, ResponseCode.PRINT_WORKING_DIRECTORY, new String[]{relativePath});
    }
    
}
