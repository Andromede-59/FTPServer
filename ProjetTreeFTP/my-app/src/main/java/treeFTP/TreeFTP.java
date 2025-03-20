package treeFTP;

import java.io.*;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Represents a tree structure of a FTP server
 */
public class TreeFTP {
    private Connection ftpConnection;
    private final OutputHandler outputHandler;
    private int maxDepth = -1;

    /**
     * Initializes a new TreeFTP
     * @param connection gives the connection to the FTP server
     * @param outputHandler gives the output handler
     * @param useDepth indicates if the depth should be used
     */
    public TreeFTP(Connection connection, OutputHandler outputHandler, boolean useDepth) {
        this.ftpConnection = connection;
        this.outputHandler = outputHandler;

        if (useDepth) {
            this.askMaxDepth();
        }
    }

    /**
     * Asks the user for the maximum depth of the tree
     * @throws RuntimeException if an error occurs while reading the input
     */
    private void askMaxDepth() {
        System.out.println("Enter the maximum depth of the tree (enter -1 for no limit): ");
        try {
            maxDepth = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
        } catch (IOException e) {
            throw new RuntimeException("Error reading depth input: " + e.getMessage(), e);
        }
    }

    /**
     * Generates the tree structure of the FTP server
     * @param rootDir the root directory of the tree
     */
    public void generateTree(String rootDir) {
        System.out.println("Generating ...");
        Stack<Boolean> isLastDirectoryStack = new Stack<>();
        listAllFiles(rootDir, 0, isLastDirectoryStack, ".");

        if (outputHandler instanceof JsonOutputHandler) {
            ((JsonOutputHandler) outputHandler).saveToFile();
        }
    }

    /**
     * recursively lists all files in the current directory by calling listFiles or changing directory
     * @param currentDir the current directory
     * @param depth the depth of the current directory
     * @param isLastDirectoryStack the stack of booleans indicating if the directory is the last one
     */
    private void listAllFiles(String currentDir, int depth, Stack<Boolean> isLastDirectoryStack, String parentDirectory) {
        String formatedDirName = currentDir.split("/")[currentDir.split("/").length - 1];

        try {
            this.changeDirectory(currentDir);
            if (!currentDir.equals(".")) {
                outputHandler.handleOutput(TreeDisplay.ANSI_BLUE + formatedDirName + TreeDisplay.ANSI_RESET, depth, isLastDirectoryStack, parentDirectory);
            }
        } catch (PermissionDeniedException e) {
            outputHandler.handleOutput(TreeDisplay.ANSI_RED + "[Permission Denied] " + formatedDirName + TreeDisplay.ANSI_RESET, depth, isLastDirectoryStack, parentDirectory);
            return;
        } catch (IOException ex) {
            listAllFiles(currentDir, depth, isLastDirectoryStack, parentDirectory); // Reprise
            return;
        }

        // if maxDepth is set and the current depth is greater than the maxDepth, return
        if (maxDepth != -1 && depth > maxDepth) {
            return;
        }

        // iterates over all files in the current directory
        ArrayList<String> fileList = this.listFiles();
        for (int i = 0; i < fileList.size(); i++) {
            String file = fileList.get(i);
            boolean isDirectory = file.startsWith("d");
            String name = file.split(" ")[file.split(" ").length - 1];
            boolean isLast = (i == fileList.size() - 1);

            isLastDirectoryStack.push(isLast);
            if (!isDirectory) {
                outputHandler.handleOutput(TreeDisplay.ANSI_GREEN + name + TreeDisplay.ANSI_RESET, depth, isLastDirectoryStack, formatedDirName);
            }

            if (isDirectory && !name.equals(".") && !name.equals("..")) {
                listAllFiles((currentDir.charAt(0) == '.' ? currentDir.replace(".","") : currentDir) + "/" + name, depth + 1, isLastDirectoryStack, formatedDirName);
            }

            isLastDirectoryStack.pop();
        }
    }

    /**
     * Changes the current directory
     * @param directory the directory to change to
     * @throws PermissionDeniedException if the permission is denied
     * @throws IOException if an I/O error occurs
     */
    private void changeDirectory(String directory) throws PermissionDeniedException, IOException {
        BufferedReader reader = this.ftpConnection.getReader();
        PrintWriter writer = this.ftpConnection.getWriter();
        writer.println("CWD " + directory);
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.matches("^\\d{3}.*") && line.startsWith("250")) {
                    break;
                } else if (line.startsWith("550")) {
                    throw new PermissionDeniedException("Permission denied to access " + directory);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error changing directory: " + e.getMessage(), e);
        } 
    }

    /**
     * Lists all files in the current directory
     * @return the list of files
     */
    private ArrayList<String> listFiles() {
        BufferedReader reader = this.ftpConnection.getReader();
        PrintWriter writer = this.ftpConnection.getWriter();
        ArrayList<String> lines = new ArrayList<>();
        this.ftpConnection.enterPassiveMode();
        try {
            writer.println("LIST");
            reader.readLine();
            // Read data from data socket
            try (BufferedReader dataReader = new BufferedReader(new InputStreamReader(this.ftpConnection.getDataSocket().getInputStream()))) {
                String currentLine;
                while ((currentLine = dataReader.readLine()) != null) {
                    lines.add(currentLine);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file list: " + e.getMessage());
            throw new RuntimeException("Error reading file list: " + e.getMessage(), e);
        }
        return lines;
    }
}
