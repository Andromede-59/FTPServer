package ftpserver.commands;

import java.io.PrintWriter;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Opts command
 * Handles the opts command
 */
public class OptsCommand extends FtpCommand {

    public OptsCommand(FtpSession session) {
        super(session);
    }

    /**
     * Execute the command
     * @param writer the writer
     * @param args the arguments
     */
    @Override
    public void execute(PrintWriter writer, String[] args) {
        if (args.length < 2 || !args[0].equalsIgnoreCase("UTF8")) {
            this.writeInConsole(writer, ResponseCode.OPTS_COMMAND_NOT_SUPPORTED);
            return;
        }

        String option = args[1].toUpperCase();
        if (option.equals("ON")) {
            this.writeInConsole(writer, ResponseCode.OPTS_COMMAND_OK, "UTF-8 mode enabled.");
        } else if (option.equals("OFF")) {
            this.writeInConsole(writer, ResponseCode.OPTS_COMMAND_OK, "UTF-8 mode remains enabled (forced).");
        } else {
            this.writeInConsole(writer, ResponseCode.OPTS_COMMAND_NOT_SUPPORTED);
        }
    }
}
