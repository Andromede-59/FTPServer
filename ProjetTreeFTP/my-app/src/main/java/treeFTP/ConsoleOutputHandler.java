package treeFTP;

import java.util.Stack;

/**
 * Represents an output handler for the tree structure (console output)
 */
public class ConsoleOutputHandler implements OutputHandler {

    /**
     * Handles the output of the tree structure
     * @param message the message to output
     * @param depth the depth of the message
     * @param isLastDirectoryStack the stack of booleans indicating if the directory is the last one
     */
    @Override
    public void handleOutput(String message, int depth, Stack<Boolean> isLastDirectoryStack, String parentDirectory) {
        StringBuilder prefix = new StringBuilder();

        for (int i = 0; i < isLastDirectoryStack.size() - 1; i++) {
            if (isLastDirectoryStack.get(i)) {
                prefix.append("    ");
            } else {
                prefix.append("│   ");
            }
        }

        if (!isLastDirectoryStack.isEmpty()) {
            prefix.append(isLastDirectoryStack.peek() ? "└── " : "├── ");
        }

        System.out.println(prefix + message);
    }
}
