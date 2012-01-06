package uk.ac.ebi.pride.tools.converter.gui.component.table.model;

import org.apache.commons.collections15.multimap.MultiHashMap;
import psidev.psi.tools.validator.ValidatorMessage;

import javax.swing.table.DefaultTableModel;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 09/08/11
 * Time: 16:18
 */
public class ValidatorMessageTableModel extends DefaultTableModel {

    private final Vector<String> columnIdentifiers = new Vector<String>();

    {
        columnIdentifiers.add("Severity");
        columnIdentifiers.add("Rule ID");
        columnIdentifiers.add("Message");
    }

    public ValidatorMessageTableModel(Collection<ValidatorMessage> msgs) {
        this(msgs, true);
    }

    public ValidatorMessageTableModel(Collection<ValidatorMessage> msgs, boolean collapseRules) {
        super();

        Vector<Vector<String>> rowData = new Vector<Vector<String>>();
        if (msgs != null) {

            MultiHashMap<String, ValidatorMessage> map = new MultiHashMap<String, ValidatorMessage>();
            for (ValidatorMessage msg : msgs) {

                String id = "UNDEFINED";
                if (msg.getRule() != null && msg.getRule().getId() != null) {
                    id = msg.getRule().getId();
                }
                map.put(id, msg);
            }

            for (String id : map.keySet()) {

                Collection<ValidatorMessage> msgColl = map.get(id);
                if (msgColl == null) {
                    msgColl = Collections.EMPTY_LIST;
                }

                if (collapseRules && msgColl.size() > 1 && !id.equals("UNDEFINED")) {

                    ValidatorMessage m = msgColl.iterator().next();
                    if (m != null) {

                        Vector<String> row = new Vector<String>();

                        if (m.getLevel() != null) {
                            row.add(m.getLevel().toString());
                        } else {
                            row.add("N/A");
                        }

                        if (m.getRule() != null) {
                            row.add(m.getRule().getId() + " [" + msgColl.size() + "]");
                        } else {
                            row.add("N/A [" + msgColl.size() + "]");
                        }

                        row.add("" + m.getMessage());
                        rowData.add(row);

                    }

                } else {

                    for (ValidatorMessage m : msgColl) {
                        Vector<String> row = new Vector<String>();

                        if (m.getLevel() != null) {
                            row.add(m.getLevel().toString());
                        } else {
                            row.add("N/A");
                        }

                        if (m.getRule() != null) {
                            row.add(m.getRule().getId());
                        } else {
                            row.add("N/A");
                        }

                        row.add("" + m.getMessage());
                        rowData.add(row);

                    }

                }

            }
        }
        setDataVector(rowData, columnIdentifiers);

    }

    Class[] columnTypes = new Class[]{
            String.class, String.class, String.class
    };

    boolean[] columnEditable = new boolean[]{
            false, false, false
    };

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnEditable[columnIndex];
    }

}
