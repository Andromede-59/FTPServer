package ftpserver.commands;

import java.io.*;
import java.net.Socket;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;
import ftpserver.TransferType;

/**
 * Stor command
 * Handles the stor command
 */
public class StorCommand extends FtpCommand {

    public StorCommand(FtpSession session) {
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
            this.writeInConsole(writer, ResponseCode.SYNTAX_ERROR, "STOR command requires a file name as argument");
            return;
        }

        String fileName = args[0];
        File file = new File(session.getCurrentDirectory(), fileName);

        this.writeInConsole(writer, ResponseCode.OPENING_DATA_CONNECTION);

        try (Socket dataSocket = session.getDataConnection().getDataSocket();
             OutputStream fileStream = new FileOutputStream(file);
             InputStream dataIn = dataSocket.getInputStream();
             BufferedReader asciiReader = new BufferedReader(new InputStreamReader(dataIn, "UTF-8"));
             BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(fileStream, "UTF-8"))) {

            if (session.getTransferType() == TransferType.ASCII) {
                String line;
                while ((line = asciiReader.readLine()) != null) {
                    fileWriter.write(line);
                    fileWriter.newLine(); 
                }
                fileWriter.flush();
            } else {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = dataIn.read(buffer)) != -1) {
                    fileStream.write(buffer, 0, bytesRead);
                }
                fileStream.write("\r\n".getBytes());
                fileStream.flush();
            }

            this.writeInConsole(writer, ResponseCode.TRANSFER_COMPLETED);

        } catch (IOException e) {
            this.writeInConsole(writer, ResponseCode.CONNECTION_CLOSED, "Error during file upload: " + e.getMessage());
        } finally {
            session.getDataConnection().clearDataSocket();
        }
    }
}