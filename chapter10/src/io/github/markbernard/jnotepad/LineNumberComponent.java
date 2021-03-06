/**
 * 
 */
package io.github.markbernard.jnotepad;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * @author Mark Bernard
 *
 */
public class LineNumberComponent extends JComponent {
    private static final long serialVersionUID = -193612599154053362L;
    private JTextPane document;
    private JScrollPane pane;
    private TextDocument textDocument;

    /**
     * @param textDocument 
     * @param scrollbar 
     * @param pane 
     */
    public LineNumberComponent(TextDocument textDocument, JScrollBar scrollbar, JScrollPane pane) {
        this.textDocument = textDocument;
        this.document = textDocument.getTextArea();
        this.pane = pane;
        setFont(document.getFont());
        scrollbar.addAdjustmentListener((event) -> repaint());
    }

    public Dimension getPreferredSize() {
        FontMetrics fm = document.getFontMetrics(document.getFont());
        int lines = document.getText().split("\\r\\n").length;
        if (lines == 0) {
            lines = 1;
        }
        int digits = ((int)Math.log10(lines)) + 1;
        
        return new Dimension(digits * fm.stringWidth("W"), 30);
    }
    
    protected void paintComponent(Graphics g) {
        Color currentColor = g.getColor();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        int colWidth = getWidth();
        Rectangle viewRect = pane.getViewport().getViewRect();
        Point currentPoint = viewRect.getLocation();
        Dimension size = viewRect.getSize();
        FontMetrics fm = document.getFontMetrics(document.getFont());
        int topOffset = fm.getAscent() + fm.getLeading();
        int lineHeight = fm.getHeight();
        int physicalRasterLine = 0;
        int lastLineNumber = 0;
        int yOffset = (currentPoint.y % lineHeight);
        currentPoint.y += 3;
        while (physicalRasterLine < size.height) {
            try {
                int lineNumber = textDocument.calculatePosition(document.viewToModel(currentPoint)).x;
                if (lastLineNumber != lineNumber) {
                    lastLineNumber = lineNumber;
                    String label = String.valueOf(lineNumber);
                    int xPosition = colWidth - (fm.stringWidth(label));
                    g.drawString(label, xPosition, topOffset + physicalRasterLine - yOffset);
                }
            } catch (Exception e) {}
            physicalRasterLine += lineHeight;
            currentPoint.y += lineHeight;
        }
        g.setColor(currentColor);
    }
}
