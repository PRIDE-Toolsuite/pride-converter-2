package uk.ac.ebi.pride.tools.converter.gui.util.error;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 01/09/11
 * Time: 10:00
 */
public class ErrorDialogHandler {

    public static void showErrorDialog(Component parent, Level level, String shortErrorMessage, String detailledErrorMessage, String moduleName, Throwable e) {

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        if (detailledErrorMessage != null) {
            detailledErrorMessage = new StringBuilder(detailledErrorMessage).append('\n').append(sw.toString()).append('\n').append("MODULE: ").append(moduleName).toString();
        } else {
            detailledErrorMessage = new StringBuilder().append(sw.toString()).append('\n').append("MODULE: ").append(moduleName).toString();
        }
        ErrorInfo ei = new ErrorInfo("An error occurred", shortErrorMessage, detailledErrorMessage, moduleName, e, level, null);
        JXErrorPane pane = new JXErrorPane();
        pane.setErrorInfo(ei);
        if (ErrorLevel.FATAL.equals(level)) {
            pane.setErrorReporter(new EmailErrorReporter(pane));
        }
        JXErrorPane.showDialog(parent, pane);

    }


}
