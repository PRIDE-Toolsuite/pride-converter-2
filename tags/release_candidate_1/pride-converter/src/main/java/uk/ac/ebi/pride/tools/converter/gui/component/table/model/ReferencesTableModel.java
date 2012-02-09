package uk.ac.ebi.pride.tools.converter.gui.component.table.model;

import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.Reference;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author melih
 *         Date: 01/04/2011
 *         Time: 16:20
 */
public class ReferencesTableModel extends BaseTableModel<Reference> {

    public ReferencesTableModel() {
        super();

        String refLine = resourceBundle.getString("NewReferenceDialog.referenceLineField.text");
        String pubmed = resourceBundle.getString("NewReferenceDialog.pubMedIDField.text");
        String doi = resourceBundle.getString("NewReferenceDialog.doiIDField.text");

        /**
         * column name, editable
         * 0 [ row number, false ]
         * 1 [ refline, false ]
         * 2 [ pubmed, false ]
         * 3 [ doi, false ]
         * 4 [ delete action, true ]
         * 5 [ data, false]
         */
        columnNames = new String[]{"", refLine, pubmed, doi, "", ""};

        columnEditable = new boolean[]{false, false, false, false, true, false};

        columnTypes = new Class<?>[]{
                String.class, String.class, String.class, String.class, String.class, Reference.class
        };

        dataColumnIndex = 5;
    }

    @Override
    protected Object[] getRowObjectArray(Reference reference) {
        String pubmedValue = "", doiValue = "";

        List<CvParam> cvParams = reference.getAdditional().getCvParam();
        for (CvParam cvParam : cvParams) {
            String cvLabel = cvParam.getCvLabel();
            if (cvLabel != null && "pubmed".equals(cvLabel.toLowerCase())) {
                pubmedValue = (cvParam.getAccession());
            }
            if (cvLabel != null && "doi".equals(cvLabel.toLowerCase())) {
                doiValue = (cvParam.getAccession());
            }
        }
        return new Object[]{"", reference.getRefLine(), pubmedValue, doiValue, "", reference};
    }


}
