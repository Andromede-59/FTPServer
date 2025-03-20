package treeFTP;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Represents an output handler for the tree structure (JSON output)
 */
public class JsonOutputHandler implements OutputHandler {
    private final JSONObject treeStructure;
    private final Map<String, JSONObject> directoryMap;
    private final String outputFilePath;

    /**
     * Creates a new JSON output handler
     * @param outputFilePath the path of the output file
     */
    @SuppressWarnings("unchecked")
    public JsonOutputHandler(String outputFilePath) {
        this.outputFilePath = outputFilePath;
        this.treeStructure = new JSONObject();
        this.treeStructure.put("name", ".");
        this.treeStructure.put("children", new JSONArray());

        this.directoryMap = new HashMap<>();
        directoryMap.put(".", treeStructure);
    }

    /**
     * Handles the output of the tree structure
     * @param message the message to output (file or directory name)
     * @param depth the depth of the message
     * @param isLastDirectoryStack the stack of booleans indicating if the directory is the last one
     * @param parentDirectory the parent directory of the message
     */
    @SuppressWarnings("unchecked")
    @Override
    public void handleOutput(String message, int depth, Stack<Boolean> isLastDirectoryStack, String parentDirectory) {
        JSONObject node = new JSONObject();
        String cleanedName = message.replaceAll("\u001b\\[[;\\d]*m", "");
        node.put("name", cleanedName);
        node.put("permissionDenied", message.startsWith("[Permission Denied]"));
        node.put("children", new JSONArray());
        node.put("parent", parentDirectory);

        // Vérifier si le parent existe dans la map
        JSONObject parent = directoryMap.getOrDefault(parentDirectory, treeStructure);
        JSONArray children = (JSONArray) parent.get("children");
        children.add(node);

        // Si c'est un dossier valide, l'ajouter à la map
        if (!cleanedName.startsWith("[Permission Denied]")) {
            directoryMap.put(cleanedName, node);
        }
    }

    /**
     * Saves the tree structure to a JSON file
     */
    public void saveToFile() {
        try (FileWriter writer = new FileWriter(outputFilePath)) {
            writer.write(treeStructure.toJSONString());
            System.out.println("Tree generated in " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
