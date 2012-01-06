package uk.ac.ebi.pride.tools.converter.gui;

import uk.ac.ebi.pride.tools.converter.conversion.PrideConverter;
import uk.ac.ebi.pride.tools.converter.gui.forms.*;
import uk.ac.ebi.pride.tools.converter.gui.model.OutputFormat;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 18/08/11
 * Time: 09:26
 */
public class ConverterApplicationSelector {
    public static void main(String[] args) {

        if (args.length == 0) {
            NavigationPanel panel = NavigationPanel.getInstance();
            panel.registerForm(new DataTypeForm());
            panel.registerForm(new FileSelectionForm(OutputFormat.PRIDE_XML));
            panel.registerForm(new ExperimentDetailForm());
            panel.registerForm(new ContactForm());
            panel.registerForm(new ReferenceForm());
            panel.registerForm(new SampleForm());
            panel.registerForm(new ProtocolForm());
            panel.registerForm(new InstrumentForm());
            panel.registerForm(new SoftwareProcessingForm());
            panel.registerForm(new DatabaseMappingForm());
            panel.registerForm(new PTMForm());
            panel.registerForm(new AnnotationDoneForm());
            panel.registerForm(new FileExportForm());
            panel.registerForm(new ReportForm());
            panel.reset();

        } else if (args.length == 1 && args[0].equals("--mztab")) {

            NavigationPanel panel = NavigationPanel.getInstance();
            panel.registerForm(new DataTypeForm());
            panel.registerForm(new FileSelectionForm(OutputFormat.MZTAB));
            panel.registerForm(new MzTabOptionForm());
            panel.registerForm(new MzTabReportForm());

        } else {
            PrideConverter.main(args);
        }

    }
}
