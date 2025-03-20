package ftpserver;

/**
 * ResponseCode for FTP server
 */
public enum ResponseCode {
    AUTH_COMMAND_OK(234, "AUTH command ok"),
    COMMAND_NOT_IMPLEMENTED(502, "Command not implemented."),
    DIRECTORY_LISTING(150, "Here comes the directory listing."),
    DIRECTORY_SEND_OK(226, "Directory send OK."),
    DIRECTORY_LISTING_ERROR(550, "Error during directory listing."),
    DIRECTORY_SUCCESSFULLY_CHANGED(250, "Directory successfully changed to "),
    CONNECTION_CLOSED(426, "Connection closed; transfer aborted."),
    DIRECTORY_NOT_FOUND(550, "Directory not found."),
    ENTER_PASSIVE_MODE(227, "Entering Passive Mode "),
    CANNOT_ENTER_PASSIVE_MODE(425, "Can't open data connection."),
    NO_DATA_CONNECTION(425, "No data connection established."),
    PASSWORD_CORRECT(230, "Password correct, user logged in."),
    PASSWORD_INCORRECT(530, "Incorrect password."),
    PRINT_WORKING_DIRECTORY(257, ""),
    OPENING_DATA_CONNECTION(150, "File status okay; about to open data connection"),
    RETR_SUCCESSFUL(226, "Closing data connection. Requested file action successful"),
    TYPE_SET(200, "Type set to "),
    UNKNOWN_COMMAND(500, "Unknown command"),
    USERNAME_OK(331, "Username okay, need password."),
    USERNAME_INCORRECT(530, "Incorrect username."),
    WELCOME(220, "Welcome to the FTP server."),
    ACCESS_DENIED(550, "Access denied."),
    INTERNAL_ERROR(550, "Internal error."),
    SYST_COMMAND_OK(215, ""),
    FEAT_COMMAND_OK(211, "Features:"),
    FEAT_COMMAND_ARGS(2110, ""),
    FEAT_COMMAND_END(211, "End"),
    OPTS_COMMAND_OK(200, ""),
    OPTS_COMMAND_NOT_SUPPORTED(504, "Encoding not supported yet"),
    SYNTAX_ERROR(501, "Syntax error in parameters or arguments : "),
    FILE_NOT_FOUND(550, "File not found: "),
    TRANSFER_COMPLETED(226, "Transfer completed."),
    FILE_ACTION_SUCCESSFUL(250, "File action successful."),
    FILE_ACTION_NOT_TAKEN(450, "Requested file action not taken."),
    FILE_ACTION_PENDING(350, "Requested file action pending further information."),
    BAD_SEQUENCE(503, "Bad sequence of commands."),
    COMMAND_PORT_OK(200, "PORT command successful."),
    FILE_STATUS(213, ""),
    GOODBYE(221, "Goodbye.");


    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Returns the string representation of the ResponseCode with both the code and the message.
     * @return the string representation of the ResponseCode
     */
    @Override
    public String toString() {
        if (code == 2110) {
            return message;
        }
        return code + " " + message;
    }
}