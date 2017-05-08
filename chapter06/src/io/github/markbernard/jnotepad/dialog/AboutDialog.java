/**
 * 
 */
package io.github.markbernard.jnotepad.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author mxb122
 *
 */
public class AboutDialog extends BasicDialog {
    private static final long serialVersionUID = -7298025013614141428L;

    /**
     * @param owner
     */
    public AboutDialog(Frame owner) {
        super(owner, "About JNotepad", true);
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        add(mainPanel, BorderLayout.CENTER);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        JPanel topTitlePanel = new JPanel();
        mainPanel.add(topTitlePanel, BorderLayout.NORTH);
        JLabel titleLabel = new JLabel("<html><h1>JNotepad</h1></html>");
        topTitlePanel.add(titleLabel);

        JPanel messagePanel = new JPanel();
        mainPanel.add(messagePanel, BorderLayout.CENTER);
        messagePanel.add(new JLabel(loadTextFromResource("/res/text/about.html")));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        JButton okButton = new JButton("OK");
        buttonPanel.add(okButton);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        addEscapeToActionMap(okButton);
        pack();
        centerDialog();
    }
}
