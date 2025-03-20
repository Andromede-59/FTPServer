package ftpserver;

import java.io.IOException;

/**
 * Main application
 */
public class App {
    public static void main(String[] args) throws IOException {
        if(java.util.Arrays.asList(args).contains("-h") || java.util.Arrays.asList(args).contains("--help")) {
            System.out.println("Help Menu for FTP Server");
            System.out.println("Usage: java -jar ftpserver-1.0-SNAPSHOT.jar <port>");
            return;
        } else if(args.length != 1) {
            System.out.println("Usage: java -jar ftpserver-1.0-SNAPSHOT.jar <port>");
            return;
        } 

        FtpServer ftp = new FtpServer(Integer.parseInt(args[0]));
        ftp.start();
    }
}
