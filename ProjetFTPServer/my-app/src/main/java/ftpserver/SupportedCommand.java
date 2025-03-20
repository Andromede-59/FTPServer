package ftpserver;

/**
 * SupportedCommand for FTP server
 */
public enum SupportedCommand {
    LIST, QUIT, USER, PASS, PWD, RETR, STOR, PASV, TYPE, CWD, CDUP, AUTH, UNKNOWN, SYST, DELE, MKD, RMD, RNFR, RNTO, PORT, SIZE
}