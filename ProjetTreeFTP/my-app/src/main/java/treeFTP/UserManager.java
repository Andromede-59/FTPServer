package treeFTP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Manage a user login
 */
public class UserManager {
    private BufferedReader reader;
    private PrintWriter writer;

    /**
     * Initializes a new UserManager
     * @param reader gives the reader
     * @param writer gives the writer
     */
    public UserManager(BufferedReader reader, PrintWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    /**
     * Logs in the user
     * @throws Exception if an error occurs while logging in
     */
    public void login() throws Exception {
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        
        // username
        System.out.print("Please enter your username (default: anonymous): ");
        System.out.flush(); 
        String username = consoleReader.readLine();
        if (username == null || username.trim().isEmpty()) {
            username = "anonymous";
        }

        writer.println("USER " + username);
        // protection if the server responds with a 220 code
        if(reader.readLine().startsWith("220")) {
            reader.readLine();
        }

        // password (will be hidden if console is available)
        String password = getPasswordFromConsole("Please enter your password (default: anonymous): ");
        if (password == null || password.trim().isEmpty()) {
            password = "anonymous";
        }

        writer.println("PASS " + password);
        String passResponse = reader.readLine();

        // check if login was successful
        if (passResponse.startsWith("530")) {
            throw new Exception("Login failed");
        }

        System.out.println("Login successful");
    }

    /**
     * Gets the password from the console
     * @param prompt gives the prompt to display
     * @return the password entered by the user
     * @throws Exception if an error occurs while reading the password
     */
    private String getPasswordFromConsole(String prompt) throws Exception {
        if (System.console() != null) {
            char[] passwordChars = System.console().readPassword(prompt);
            return new String(passwordChars);
        } else {
            System.out.print(prompt);
            System.out.flush();
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            return consoleReader.readLine();
        }
    }
}