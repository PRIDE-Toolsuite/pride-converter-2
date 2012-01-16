/*
 * Created by JFormDesigner on Mon Mar 14 16:00:10 GMT 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.component;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.component.table.ParamTable;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.ComboValueCvParamDialog;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.CvParamDialog;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.UserParamDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Rui Wang
 */
public class AddTermButton extends JButton {

    private static final Logger logger = Logger.getLogger(AddTermButton.class);

    private JPopupMenu menu;
    private ParamTable owner;
    private boolean showUserParam = false;
    private boolean enabled = true;
    private Set<String> suggestedCVs;
    private Collection<String> comboBoxValues;

    public AddTermButton() {
        initComponents();
    }

    public AddTermButton(boolean showUserParam) {
        this.showUserParam = showUserParam;
        initComponents();
    }

    public void setOwner(ParamTable owner) {
        this.owner = owner;
    }

    public void setSuggestedCVs(Set<String> suggestedCVs) {
        this.suggestedCVs = suggestedCVs;
    }

    public void setComboBoxValues(Collection<String> comboBoxValues) {
        this.comboBoxValues = comboBoxValues;
    }

    private void initComponents() {
        ResourceBundle bundle = ResourceBundle.getBundle("messages");

        setText(bundle.getString("AddTermButton.this.text"));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (menu == null) {
                    menu = new AddTermPopupMenu();
                }
                Component component = e.getComponent();
                Point p = component.getLocation();
                Dimension cDim = component.getSize();
                if (enabled) {
                    menu.show(component, 0, (int) cDim.getHeight());
                }
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        super.setEnabled(enabled);
    }

    public boolean isShowUserParam() {
        return showUserParam;
    }

    public void setShowUserParam(boolean showUserParam) {
        this.showUserParam = showUserParam;
    }

    /**
     * @author Rui Wang
     */
    public class AddTermPopupMenu extends JPopupMenu {
        private JMenuItem cvParamMenuItem;
        private JMenuItem userParamMenuItem;

        public AddTermPopupMenu() {
            initComponents();
        }

        private void cvParamMenuItemActionPerformed(ActionEvent e) {

            logger.debug("cvparam");
            if (comboBoxValues != null) {
                ComboValueCvParamDialog cvParamDialog = new ComboValueCvParamDialog(NavigationPanel.getInstance(), owner, suggestedCVs, comboBoxValues);
                cvParamDialog.setVisible(true);
            } else {
                CvParamDialog cvParamDialog = new CvParamDialog(NavigationPanel.getInstance(), owner, suggestedCVs);
                cvParamDialog.setVisible(true);
            }
        }

        private void userParamMenuItemActionPerformed(ActionEvent e) {

            logger.debug("userparam");
            UserParamDialog userParamDialog = new UserParamDialog(NavigationPanel.getInstance(), owner);
            userParamDialog.setVisible(true);

        }

        private void initComponents() {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            cvParamMenuItem = new JMenuItem();
            userParamMenuItem = new JMenuItem();

            //---- cvParamMenuItem ----
            cvParamMenuItem.setText(bundle.getString("AddTermPopupMenu.cvParamMenuItem.text"));
            cvParamMenuItem.setFont(cvParamMenuItem.getFont().deriveFont(cvParamMenuItem.getFont().getSize() + 2f));
            cvParamMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cvParamMenuItemActionPerformed(e);
                }
            });
            add(cvParamMenuItem);
            if (showUserParam) {
                //---- userParamMenuItem ----
                userParamMenuItem.setText(bundle.getString("AddTermPopupMenu.userParamMenuItem.text"));
                userParamMenuItem.setFont(userParamMenuItem.getFont().deriveFont(userParamMenuItem.getFont().getSize() + 2f));
                userParamMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        userParamMenuItemActionPerformed(e);
                    }
                });
                add(userParamMenuItem);
            }
        }
    }

}
