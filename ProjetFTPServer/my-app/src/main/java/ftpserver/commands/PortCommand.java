package ftpserver.commands;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Port command
 * Handles the port command
 */
public class PortCommand extends FtpCommand {

    public PortCommand(FtpSession session) {
        super(session);
    }

    /**
     * Execute the command
     * @param writer the writer
     * @param args the arguments
     */
    @Override
    public void execute(PrintWriter writer, String[] args) {
        if (args.length != 1) {
            this.writeInConsole(writer, ResponseCode.SYNTAX_ERROR, "PORT command requires an address and port");
            return;
        }

        try {
            // Parse l'adresse IP et le port à partir de la commande PORT
            String[] parts = args[0].split(",");
            if (parts.length != 6) {
                this.writeInConsole(writer, ResponseCode.SYNTAX_ERROR, "Invalid address format");
                return;
            }

            String ip = String.join(".", parts[0], parts[1], parts[2], parts[3]);
            int port = (Integer.parseInt(parts[4]) * 256) + Integer.parseInt(parts[5]);

            // Ferme toute connexion précédente avant d'établir la nouvelle
            session.getDataConnection().clearDataSocket();

            // Établit la connexion active vers le client
            Socket dataSocket = createSocket();
            dataSocket.connect(new InetSocketAddress(InetAddress.getByName(ip), port), 5000);
            session.getDataConnection().setDataSocket(dataSocket);

            this.writeInConsole(writer, ResponseCode.COMMAND_PORT_OK, "Active data connection established to " + ip + ":" + port);

        } catch (IOException | NumberFormatException e) {
            this.writeInConsole(writer, ResponseCode.CONNECTION_CLOSED, "Failed to establish active data connection: " + e.getMessage());
        }
    }

    protected Socket createSocket() throws IOException {
        return new Socket();
    }
}
