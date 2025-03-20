package treeFTP;

import java.util.Stack;

/**
 * Represents an output handler for the tree structure
 */
public interface OutputHandler {
    void handleOutput(String message, int depth, Stack<Boolean> isLastDirectoryStack, String parentDirectory);
}
