package treeFTP;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -c -jar TreeFTP.jar ftpAddress");
            System.exit(1);
        }

        String ftpAddress = args[0];
        boolean useDepth = false;
        boolean outputJson = false;

        for (String arg : args) {
            if (arg.equals("-h") || arg.equals("--help")) {
                System.out.println("Usage: java -c -jar TreeFTP.jar ftpAddress");
                System.out.println("Options:");
                System.out.println("  -h, --help  Display this help message");
                System.out.println("  -d, --depth  Set the maximum depth of the tree");
                System.out.println("  -j, --json  Output the tree in JSON format in a separate file");
                System.exit(0);
            } else if (arg.equals("-d") || arg.equals("--depth")) {
                useDepth = true;
            } else if (arg.equals("-j") || arg.equals("--json")) {
                outputJson = true;
            }
        }

        try {
            FTPConnection connection = new FTPConnection(ftpAddress, 2121);
            OutputHandler outputHandler = outputJson
                ? new JsonOutputHandler("tree_output.json")
                : new ConsoleOutputHandler();
        
            UserManager userManager = new UserManager(connection.getReader(), connection.getWriter());
            userManager.login();

            TreeFTP treeFTP = new TreeFTP(connection, outputHandler, useDepth);
            treeFTP.generateTree(".");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
