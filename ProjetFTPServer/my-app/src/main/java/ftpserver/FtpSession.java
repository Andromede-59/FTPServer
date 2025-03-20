package ftpserver;

import java.io.File;
import java.io.IOException;

/**
 * FTP session
 * Manages the state of the FTP session
 */
public class FtpSession {
    private boolean isAuthenticated = false;
    private File currentDirectory;
    private final File rootDirectory;
    private TransferType transferType = TransferType.ASCII;
    private DataConnection dataConnection;
    private File renameFile;

    public FtpSession(File rootDirectory) {
        this.rootDirectory = rootDirectory;
        this.currentDirectory = rootDirectory;
        this.dataConnection = new DataConnection();
    }

    /**
     * Resolve the file path
     * @param path the path
     * @return the file
     * @throws SecurityException if access is denied
     */
    public File resolveFile(String path) throws SecurityException {
        File targetFile = new File(currentDirectory, path);
        if (!isWithinRoot(targetFile)) {
            throw new SecurityException("Access denied to: " + path);
        }
        return targetFile;
    }

    /**
     * Check if the file is within the root directory
     * @param file the file
     * @return true if the file is within the root directory
     */
    private boolean isWithinRoot(File file) {
        try {
            String canonicalPath = file.getCanonicalPath();
            return canonicalPath.startsWith(rootDirectory.getCanonicalPath());
        } catch (IOException e) {
            return false;
        }
    }

    // Getters and setters
    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    public boolean isAuthenticated() {
        System.out.println("isAuthenticated: " + isAuthenticated);
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.isAuthenticated = authenticated;
    }

    public void setCurrentDirectory(File directory) throws SecurityException {
        if (!isWithinRoot(directory)) {
            throw new SecurityException("Access denied");
        }
        this.currentDirectory = directory;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferType type) {
        this.transferType = type;
    }

    public DataConnection getDataConnection() {
        return dataConnection;
    }

    public File getRenameFile() {
        return renameFile;
    }

    public void setRenameFile(File renameFile) {
        this.renameFile = renameFile;
    }
}