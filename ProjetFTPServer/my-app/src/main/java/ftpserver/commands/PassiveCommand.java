package ftpserver.commands;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Passive command
 * Handles the passive command
 */
public class PassiveCommand extends FtpCommand {

    public PassiveCommand(FtpSession session) {
        super(session);
    }
    
    /**
     * Execute the command
     * @param writer the writer
     * @param args the arguments
     */
    @Override
    public void execute(PrintWriter writer, String[] args) {
        try {
            int port = session.getDataConnection().startPassiveMode();

            String ip = "127.0.0.1";  
            String[] ipParts = ip.split("\\.");
            int p1 = port / 256;
            int p2 = port % 256;

            String response = String.format("(%s,%s,%s,%s,%d,%d)", ipParts[0], ipParts[1], ipParts[2], ipParts[3], p1, p2);
            this.writeInConsole(writer, ResponseCode.ENTER_PASSIVE_MODE, response);

            Socket dataSocket = session.getDataConnection().acceptPassiveConnection();
            session.getDataConnection().setDataSocket(dataSocket);

        } catch (IOException e) {
            this.writeInConsole(writer, ResponseCode.CANNOT_ENTER_PASSIVE_MODE, "");
        }
    }
}
