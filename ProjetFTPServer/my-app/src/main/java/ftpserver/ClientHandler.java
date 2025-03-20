package ftpserver;

import java.io.*;
import java.net.Socket;
import ftpserver.commands.CommandFactory;
import ftpserver.commands.FtpCommand;
import ftpserver.commands.UnknownCommand;
import ftpserver.commands.WelcomeCommand;

/**
 * Client handler
 * Handles the client connection
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final FtpSession session;
    private final CommandFactory commandFactory;
    
    public ClientHandler(Socket clientSocket, File rootDirectory) {
        this.clientSocket = clientSocket;
        this.session = new FtpSession(rootDirectory);
        this.commandFactory = new CommandFactory(session);
    }

    /**
     * Run the client handler
     */
    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            sendWelcomeMessage(writer);
            handleCommands(reader, writer);

        } catch (IOException e) {
            // Log error
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    /**
     * Handle the client commands
     * @param reader the reader
     * @param writer the writer
     * @throws IOException if an error occurs
     */
    private void handleCommands(BufferedReader reader, PrintWriter writer) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ", 2);
            String commandStr = parts[0];
            String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];

            FtpCommand command = commandFactory.getCommand(commandStr);
            try {
                command.execute(writer, args);
            } catch (Exception e) {
                handleCommandException(e, writer);
            }
        }
    }

    /**
     * H andle the command exception
     * @param e the exception
     * @param writer the writer
     */ 
    private void handleCommandException(Exception e, PrintWriter writer) {
        (new UnknownCommand()).writeInConsole(writer, ResponseCode.INTERNAL_ERROR);
    }

    /**
     * Send the welcome message
     * @param writer the writer
     */
    private void sendWelcomeMessage(PrintWriter writer) {
        new WelcomeCommand(session).execute(writer, new String[0]);
    }

    /**
     * Close the connection
     */
    private void closeConnection() {
        try {
            session.getDataConnection().close();
            clientSocket.close();
        } catch (IOException e) {
            // Log error
        }
    }

    /**
     * Get the session
     * @return the session
     */
    public FtpSession getSession() {
        return session;
    }
}
