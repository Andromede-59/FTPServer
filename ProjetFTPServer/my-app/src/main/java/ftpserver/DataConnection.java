package ftpserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * DataConnection for FTP server
 */
public class DataConnection implements AutoCloseable {
    private static final int MIN_PORT = 20000;
    private static final int MAX_PORT = 30000;
    
    private ServerSocket passiveSocket;
    private Socket dataSocket;

    /**
     * Démarre un socket en mode passif et retourne le port assigné.
     * @return le port assigné
     * @throws IOException si une erreur survient
     */
    public int startPassiveMode() throws IOException {
        close(); // Nettoie toute ancienne connexion avant de démarrer
        try {
            int port = findAvailablePort();
            passiveSocket = new ServerSocket(port);
            passiveSocket.setReuseAddress(true);
            return port;
        } catch (RuntimeException e) {
            throw new IOException(e);
        }
    }

    /**
     * Accepte la connexion du client en mode passif.
     * @return la connexion de données
     * @throws IOException si une erreur survient
     */
    public Socket acceptPassiveConnection() throws IOException {
        if (passiveSocket != null) {
            dataSocket = passiveSocket.accept();
            return dataSocket;
        }
        throw new IOException("Passive mode not initialized");
    }
    
    /**
     * Retourne la connexion de données active.
     */
    public Socket getDataSocket() {
        return dataSocket;
    }

    public void setDataSocket(Socket dataSocket) {
        this.dataSocket = dataSocket;   
    }

    /**
     * Ferme et libère la connexion de données.
     */
    public void clearDataSocket() {
        try {
            if (dataSocket != null) {
                dataSocket.close();
                dataSocket = null;
            }
        } catch (IOException ignored) {}
    }

    /**
     * Trouve un port disponible pour le mode passif.
     * @return le port disponible
     * @throws RuntimeException si aucun port n'est disponible
     */
    protected int findAvailablePort() {
        int attempts = 10;
        while (attempts-- > 0) {
            int port = MIN_PORT + (int)(Math.random() * (MAX_PORT - MIN_PORT));
            try (ServerSocket socket = new ServerSocket(port)) {
                return port;
            } catch (IOException ignored) {
                // Continue trying
            }
        }
        throw new RuntimeException("No available ports");
    }

    /**
     * Ferme la connexion de données.
     */
    @Override
    public void close() {
        try {
            clearDataSocket();
            if (passiveSocket != null) {
                passiveSocket.close();
                passiveSocket = null;
            }
        } catch (IOException ignored) {}
    }
}
