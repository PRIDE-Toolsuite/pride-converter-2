package uk.ac.ebi.pride.tools.converter.gui.util.error;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.jdesktop.swingx.error.ErrorReporter;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class EmailErrorReporter extends JFrame implements ErrorReporter {

    private static final Logger log = Logger.getLogger(EmailErrorReporter.class);

    private final JXErrorPane errorPane;
    private ErrorInfo errorInfo;

    public EmailErrorReporter(JXErrorPane errorPane) {
        this.errorPane = errorPane;
    }

    public void reportError(ErrorInfo errorInfo) throws NullPointerException {
        this.errorInfo = errorInfo;
        //todo - get panel to display properly when called
        //setVisible(true);
        sendEmailButtonClicked(null);
    }

    private void sendEmailButtonClicked(ActionEvent event) {

        try {

//            String email = JOptionPane.showInputDialog(errorPane, "Please provide your email address below. This is optional, but may allow us to contact you to help you resolve your problem in a timely manner", null);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String host = "smtp.gmail.com";
            String username = "pride.converter@gmail.com";
            String password = "BkHz13wX";

            Properties props = new Properties();
            props.put("mail.smtp.starttls.enable", "true"); // added this line
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.user", username);
            props.put("mail.smtp.password", password);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");

            Session session = Session.getDefaultInstance(props, null);

            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("pride.converter@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("pride.converter@gmail.com"));
                message.setSubject("Pride Converter Error Report");

                StringBuilder msg = new StringBuilder(df.format(new Date()));
                msg.append("\n\n");
//                if (email  != null){
//                    msg.append("email: ").append(email).append("\n\n");
//                }
                msg.append(errorInfo.getDetailedErrorMessage());
                msg.append("\n\n");
                Map<String, String> state = errorInfo.getState();
                for (String key : state.keySet()) {
                    msg.append(key).append(" -> ").append(state.get(key)).append("\n");
                }
                msg.append("\n\n");
                msg.append(ConverterData.getInstance().toString());
                message.setText(msg.toString());

                Transport transport = session.getTransport("smtp");
                transport.connect(host, username, password);
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();

                if (errorInfo.getErrorLevel().equals(ErrorLevel.FATAL)) {
                    JOptionPane.showMessageDialog(errorPane, "Email sent to pride-support. Pride Converter will now exit");
                    System.exit(1);
                } else {
                    JOptionPane.showMessageDialog(errorPane, "Email sent to pride-support.");
                }

            } catch (MessagingException e) {
                log.error("Failed to send email error report", e);
                e.printStackTrace();
            }

        } catch (Exception e) {
            log.error("Failed to send email error report", e);
            e.printStackTrace();
        } finally {
            setVisible(false);
            dispose();
        }
    }

    private void cancelButtonClicked(ActionEvent e) {
        setVisible(false);
        dispose();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        nameField = new JTextField();
        label2 = new JLabel();
        emailField = new JTextField();
        label3 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();
        panel1 = new JPanel();
        button1 = new JButton();
        button2 = new JButton();

        //======== this ========
        Container contentPane = getContentPane();

        //---- label1 ----
        label1.setText("Name");

        //---- label2 ----
        label2.setText("E-Mail");

        //---- label3 ----
        label3.setText("Please provide your name and email addres below.");

        //---- label4 ----
        label4.setText("This is not required but may help us resolve your ");

        //---- label5 ----
        label5.setText("problem in a more timely fashion.");

        //======== panel1 ========
        {
            panel1.setLayout(new FlowLayout());

            //---- button1 ----
            button1.setText("Send Email");
            button1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendEmailButtonClicked(e);
                }
            });
            panel1.add(button1);

            //---- button2 ----
            button2.setText("Cancel");
            button2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cancelButtonClicked(e);
                }
            });
            panel1.add(button2);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(label3)
                                        .addComponent(label4)
                                        .addComponent(label5)
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addComponent(label1)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(nameField, GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE))
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addComponent(label2)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(emailField, GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE))
                                        .addComponent(panel1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(label3)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label4)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label5)
                                .addGap(18, 18, 18)
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label1))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(label2)
                                        .addComponent(emailField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JTextField nameField;
    private JLabel label2;
    private JTextField emailField;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JPanel panel1;
    private JButton button1;
    private JButton button2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
