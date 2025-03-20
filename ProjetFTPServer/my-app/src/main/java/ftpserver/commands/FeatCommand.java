package ftpserver.commands;

import java.io.PrintWriter;

import ftpserver.FtpSession;
import ftpserver.ResponseCode;

/**
 * Feat command
 * Handles the feat command
 */
public class FeatCommand extends FtpCommand {
    public FeatCommand(FtpSession session) {
        super(session);
    }

    /**
     * Execute the command
     * @param writer the writer
     * @param args the arguments
     */
    @Override
    public void execute(PrintWriter writer, String[] args) {
        this.writeInConsole(writer, ResponseCode.FEAT_COMMAND_OK);
        this.writeInConsole(writer, ResponseCode.FEAT_COMMAND_ARGS, " UTF8");
        this.writeInConsole(writer, ResponseCode.FEAT_COMMAND_ARGS, " PASV");
        this.writeInConsole(writer, ResponseCode.FEAT_COMMAND_ARGS, " EPSV");
        this.writeInConsole(writer, ResponseCode.FEAT_COMMAND_ARGS, " MLST");
        this.writeInConsole(writer, ResponseCode.FEAT_COMMAND_ARGS, " MLSD");
        this.writeInConsole(writer, ResponseCode.FEAT_COMMAND_ARGS, " REST STREAM");
        this.writeInConsole(writer, ResponseCode.FEAT_COMMAND_ARGS, " SIZE");
        this.writeInConsole(writer, ResponseCode.FEAT_COMMAND_ARGS, " MDTM");
        this.writeInConsole(writer, ResponseCode.FEAT_COMMAND_ARGS, " TVFS");
        this.writeInConsole(writer, ResponseCode.FEAT_COMMAND_END);
    }
}
