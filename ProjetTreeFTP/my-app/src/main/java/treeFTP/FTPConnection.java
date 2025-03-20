package treeFTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Represents a connection to an FTP server
 */
public class FTPConnection implements Connection {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Socket dataSocket;
    private String host;
    private int port;

    /**
     * Creates a new FTP connection
     * @param ftpAddress the address of the FTP server
     * @param port the port of the FTP server
     * @throws Exception if an error occurs while creating the socket
     */
    public FTPConnection(String ftpAddress, int port) throws Exception {
        this.host = ftpAddress;
        this.port = port;
        this.socket = new Socket(ftpAddress, port);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public BufferedReader getReader() {
        return reader;
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }

    /**
     * Closes the connection with the server
     */
    @Override
    public void close() throws Exception {
        reader.close();
        writer.close();
        if (dataSocket != null && !dataSocket.isClosed()) {
            dataSocket.close();
        }
        socket.close();
    }

    @Override
    public Socket getDataSocket() {
        return dataSocket;
    }

    public void reconnect() throws IOException {
        System.out.println("Attempting to reconnect to FTP server...");
        
        // Fermer la connexion précédente si elle est encore active
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (dataSocket != null && !dataSocket.isClosed()) {
            dataSocket.close();
        }

        // Réinitialiser les flux
        this.socket = new Socket(host, port);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream(), true);

        // Lire la réponse du serveur après connexion
        String response = reader.readLine();
        if (response.startsWith("220")) {
            System.out.println("Reconnected successfully to FTP server.");
        } else {
            throw new IOException("Failed to reconnect: " + response);
        }
    }


    /**
     * Enters server passive mode
     * @throws RuntimeException if an error occurs while entering passive mode
     */
    public void enterPassiveMode() {
        writer.println("PASV");
        try {
            String pasvResponse = reader.readLine();
            while(!pasvResponse.startsWith("227")) {
                pasvResponse = reader.readLine();
            }
            String pasvData = pasvResponse.substring(pasvResponse.indexOf('(') + 1, pasvResponse.indexOf(')'));
            String[] pasvParts = pasvData.split(",");    
            String ip = pasvParts[0] + "." + pasvParts[1] + "." + pasvParts[2] + "." + pasvParts[3];
            int port = (Integer.parseInt(pasvParts[4]) * 256) + Integer.parseInt(pasvParts[5]);
            this.dataSocket =  new Socket(ip, port);
        } catch (Exception e) {
            throw new RuntimeException("Error entering passive mode: " + e.getMessage(), e);
        }
    }
    
}
