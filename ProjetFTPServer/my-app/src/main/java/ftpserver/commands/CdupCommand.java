package ftpserver.commands;

import java.io.PrintWriter;
import java.io.File;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Cdup command
 * Handles the cdup command
 */
public class CdupCommand extends FtpCommand {
    private File currentDirectory;
    private File rootDirectory;

    public CdupCommand(FtpSession session){
        super(session);
        this.currentDirectory = session.getCurrentDirectory();
        this.rootDirectory = session.getRootDirectory();
    }

    /**
     * Execute the command
     * @param writer the writer
     * @param args the arguments
     */
    @Override
    public void execute(PrintWriter writer, String[] args) {
        this.currentDirectory = this.session.getCurrentDirectory();
        if (currentDirectory.equals(rootDirectory)) {
            this.writeInConsole(writer, ResponseCode.ACCESS_DENIED);
            return;
        }

        File parentDirectory = currentDirectory.getParentFile();
        try {
            if (parentDirectory == null || !parentDirectory.getCanonicalPath().startsWith(rootDirectory.getCanonicalPath())) {
                this.writeInConsole(writer, ResponseCode.ACCESS_DENIED);
                return;
            }
        } catch (Exception e) {
            this.writeInConsole(writer, ResponseCode.INTERNAL_ERROR, e.getMessage());
            return;
        }

        currentDirectory = parentDirectory;
        this.session.setCurrentDirectory(currentDirectory);
        String relativePath = currentDirectory.equals(rootDirectory) ? "/" 
                            : currentDirectory.getAbsolutePath().replace(rootDirectory.getAbsolutePath(), "");
        this.writeInConsole(writer, ResponseCode.DIRECTORY_SUCCESSFULLY_CHANGED, relativePath);
    }

    /**
     * Get the current directory
     * @return the current directory
     */
    public File getCurrentDirectory(){
        return currentDirectory;
    }
}
