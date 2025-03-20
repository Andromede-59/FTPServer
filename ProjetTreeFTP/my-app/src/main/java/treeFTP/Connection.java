package treeFTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Interface representing a connection to a server
 */
public interface Connection {
    BufferedReader getReader();
    PrintWriter getWriter();
    void close() throws Exception;
    Socket getDataSocket();
    void reconnect() throws IOException;
    void enterPassiveMode();
}

