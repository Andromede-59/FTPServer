package ftpserver.commands;

import java.io.PrintWriter;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;
import ftpserver.TransferType;

/**
 * Type command
 * Handles the type command
 */
public class TypeCommand extends FtpCommand {
    public TypeCommand(FtpSession session) {
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
            this.writeInConsole(writer, ResponseCode.SYNTAX_ERROR, "TYPE command requires a transfer mode (A or I)");
            return;
        }

        String mode = args[0].toUpperCase();
        switch (mode) {
            case "A":
                session.setTransferType(TransferType.ASCII);
                this.writeInConsole(writer, ResponseCode.TYPE_SET, "ASCII");
                break;
            case "I":
                session.setTransferType(TransferType.BINARY);
                this.writeInConsole(writer, ResponseCode.TYPE_SET, "Binary");
                break;
            default:
                this.writeInConsole(writer, ResponseCode.SYNTAX_ERROR, "Unsupported transfer mode: " + mode);
                break;
        }
    }
}
