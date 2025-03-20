package ftpserver.commands;

import java.io.*;
import java.net.Socket;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;
import ftpserver.TransferType;

/**
 * Retr command
 * Handles the retr command
 */
public class RetrCommand extends FtpCommand {

    public RetrCommand(FtpSession session) {
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
            this.writeInConsole(writer, ResponseCode.SYNTAX_ERROR, "RETR command requires a file name as argument");
            return;
        }

        String fileName = args[0];
        File file = new File(session.getCurrentDirectory(), fileName);

        if (!file.exists() || !file.isFile()) {
            this.writeInConsole(writer, ResponseCode.FILE_NOT_FOUND, fileName);
            return;
        }

        this.writeInConsole(writer, ResponseCode.OPENING_DATA_CONNECTION);

        try (Socket dataSocket = session.getDataConnection().getDataSocket();
             InputStream fileStream = new FileInputStream(file);
             OutputStream dataOut = dataSocket.getOutputStream();
             BufferedReader asciiReader = new BufferedReader(new InputStreamReader(fileStream, "UTF-8"));
             BufferedWriter asciiWriter = new BufferedWriter(new OutputStreamWriter(dataOut, "UTF-8"))) {

            if (session.getTransferType() == TransferType.ASCII) {
                String line;
                while ((line = asciiReader.readLine()) != null) {
                    asciiWriter.write(line + "\r\n");
                }
                asciiWriter.flush();

            } else {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileStream.read(buffer)) != -1) {
                    dataOut.write(buffer, 0, bytesRead);
                }
                dataOut.write("\r\n".getBytes());
                dataOut.flush();
            }

            this.writeInConsole(writer, ResponseCode.RETR_SUCCESSFUL);

        } catch (IOException e) {
            this.writeInConsole(writer, ResponseCode.CONNECTION_CLOSED, "Error during file transfer: " + e.getMessage());
        } finally {
            session.getDataConnection().clearDataSocket();
        }
    }
}