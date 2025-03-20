package ftpserver.commands;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.text.SimpleDateFormat;
import java.util.*;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * List command
 * Handles the list command
 */
public class ListCommand extends FtpCommand {

    public ListCommand(FtpSession session) {
        super(session);
    }

    /**
     * Execute the command
     * @param writer the writer
     * @param args the arguments
     */
    @Override
    public void execute(PrintWriter writer, String[] args) {
        Socket dataSocket = session.getDataConnection().getDataSocket();
        if (dataSocket == null) {
            this.writeInConsole(writer, ResponseCode.NO_DATA_CONNECTION);
            return;
        }

        File currentDirectory = session.getCurrentDirectory();
        File[] files = currentDirectory.listFiles();

        if (files == null) {
            this.writeInConsole(writer, ResponseCode.DIRECTORY_LISTING_ERROR);
            return;
        }

        this.writeInConsole(writer, ResponseCode.DIRECTORY_LISTING);

        try (PrintWriter dataWriter = new PrintWriter(dataSocket.getOutputStream(), true)) {
            for (File file : files) {
                this.writeContent(dataWriter, formatFileInfo(file));
            }
            this.writeInConsole(writer, ResponseCode.DIRECTORY_SEND_OK);
        } catch (IOException e) {
            this.writeInConsole(writer, ResponseCode.CONNECTION_CLOSED);
        } finally {
            try {
                dataSocket.close();
                session.getDataConnection().clearDataSocket();
            } catch (IOException ignored) {}
        }
    }

    /**
     * Format the file information
     * @param file the file
     * @return the formatted file information
     */
    private String formatFileInfo(File file) {
        StringBuilder fileInfo = new StringBuilder();
        fileInfo.append(file.isDirectory() ? "d" : "-");
        fileInfo.append(getPermissions(file));
        fileInfo.append(file.isDirectory() ? " 2" : " 1");
        try {
            Path path = file.toPath();
            UserPrincipal owner = Files.getOwner(path);
            PosixFileAttributes attrs = Files.readAttributes(path, PosixFileAttributes.class);
            GroupPrincipal group = attrs.group();
            
            fileInfo.append(" " + owner.getName()); 
            fileInfo.append(" " + group.getName()); 
        } catch (IOException e) {
            fileInfo.append(" ??? ???");
        }
        
        fileInfo.append(String.format(" " + file.length()));
        fileInfo.append(" " + getLastModifiedDate(file));
        fileInfo.append(" " + file.getName());
        return fileInfo.toString();
    }

    /**
     * Get the file permissions
     * @param file the file
     * @return the file permissions
     */
    private String getPermissions(File file) {
        StringBuilder permissions = new StringBuilder();
        try {
            Path path = file.toPath();
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(path);
            permissions.append(getPermissionString(perms, PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE));
            permissions.append(getPermissionString(perms, PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE, PosixFilePermission.GROUP_EXECUTE));
            permissions.append(getPermissionString(perms, PosixFilePermission.OTHERS_READ, PosixFilePermission.OTHERS_WRITE, PosixFilePermission.OTHERS_EXECUTE));
        } catch (IOException e) {
            permissions.append("???");
        }
        return permissions.toString();
    }

    /**
     * Get the permission string
     * @param perms the permissions
     * @param read the read permission
     * @param write the write permission
     * @param execute the execute permission
     * @return the permission string
     */
    private String getPermissionString(Set<PosixFilePermission> perms, PosixFilePermission read, PosixFilePermission write, PosixFilePermission execute) {
        return (perms.contains(read) ? "r" : "-") +
                (perms.contains(write) ? "w" : "-") +
                (perms.contains(execute) ? "x" : "-");
    }

    /**
     * Get the last modified date
     * @param file the file
     * @return the last modified date
     */
    private String getLastModifiedDate(File file) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm", Locale.ENGLISH);
        return dateFormat.format(new Date(file.lastModified()));
    }
}