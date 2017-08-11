/**
 * 
 */
package io.github.markbernard.jnotepad;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;

/**
 * @author Mark Bernard
 *
 */
public class LineNumberComponent extends JComponent {
    private static final long serialVersionUID = -193612599154053362L;
    private JTextArea document;
    private JScrollBar scrollbar;

    /**
     * 
     */
    public LineNumberComponent(JTextArea document, JScrollBar scrollbar) {
        this.document = document;
        this.scrollbar = scrollbar;
        setFont(document.getFont());
    }

    public Dimension getPreferredSize() {
        return new Dimension(30, 30);
    }
    
    protected void paintComponent(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        int viewHeight = getHeight();
        int position = scrollbar.getValue();
        FontMetrics fm = document.getFontMetrics(document.getFont());
        int lineHeight = fm.getHeight();
        int startLineNumber = position / lineHeight;
        int topOffset = fm.getAscent() + fm.getLeading();
        int colWidth = getWidth();
        for (int i=0;i<(viewHeight / lineHeight) + 1;i++) {
            String label = String.valueOf(startLineNumber + i + 1);
            int yPosition = colWidth - (fm.stringWidth(label));
            g.drawString(label, yPosition, topOffset + (i * lineHeight));
        }
        System.out.println(document.getLocation());
    }
}
