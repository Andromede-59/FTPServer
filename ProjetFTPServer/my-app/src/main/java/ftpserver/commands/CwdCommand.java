package ftpserver.commands;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Cwd command
 * Handles the cwd command
 */
public class CwdCommand extends FtpCommand {
    private File rootDirectory;
    private File currentDirectory;

    public CwdCommand(FtpSession session) {
        super(session);
        this.rootDirectory = session.getRootDirectory();
        this.currentDirectory = session.getCurrentDirectory();
    }

    /**
     * Execute the command
     * @param writer the writer
     * @param args the arguments
     */
    @Override
    public void execute(PrintWriter writer, String[] args) {
        this.currentDirectory = this.session.getCurrentDirectory();
        if (args.length != 1) {
            throw new RuntimeException("CWD command requires one argument");
        }

        String newPath = args[0];
        File newDirectory = newPath.startsWith("/") ? new File(rootDirectory, newPath) : new File(currentDirectory, newPath).getAbsoluteFile();

        try {
            if (!newDirectory.getCanonicalPath().startsWith(rootDirectory.getCanonicalPath()) 
                || !newDirectory.exists() || !newDirectory.isDirectory()) {
                this.writeInConsole(writer, ResponseCode.ACCESS_DENIED);
                return;
            }

            if (!newDirectory.getCanonicalPath().equals(currentDirectory.getCanonicalPath())) {
                this.currentDirectory = newDirectory;
                this.session.setCurrentDirectory(currentDirectory);
            }

            Path currentPath = currentDirectory.getCanonicalFile().toPath();
            Path rootPath = rootDirectory.getCanonicalFile().toPath();
            String relativePath = currentPath.equals(rootPath) ? "/" 
                    : "/" + rootPath.relativize(currentPath).toString().replace("\\", "/");

            this.writeInConsole(writer, ResponseCode.DIRECTORY_SUCCESSFULLY_CHANGED, relativePath);

        } catch (IOException e) {
            this.writeInConsole(writer, ResponseCode.INTERNAL_ERROR);
        }
    }

    /**
     * Get the current directory
     * @return the current directory
     */
    public File getCurrentDirectory() {
        return currentDirectory;
    }
}
