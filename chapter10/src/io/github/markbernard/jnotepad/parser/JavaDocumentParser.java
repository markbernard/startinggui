/**
 * 
 */
package io.github.markbernard.jnotepad.parser;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import io.github.markbernard.jnotepad.ApplicationPreferences;

/**
 * @author mxb122
 *
 */
public class JavaDocumentParser {
    private static final String DEFAULT_STYLE = "default";
    private static final String KEYWORD_STYLE = "keyword";
    private static final String COMMENT_STYLE = "comment";
    private static final String STRING_STYLE = "string";
    private static final String NUMBER_STYLE = "number";
    private static final String[] STYLES = {DEFAULT_STYLE, KEYWORD_STYLE, COMMENT_STYLE, STRING_STYLE, NUMBER_STYLE};
    
    private static final String KEYWORDS_PATTERN = "(abstract|continue|for|new|switch|assert|default|goto|package|synchronized|boolean|do|if|private|"
            + "this|break|double|implements|protected|throw|byte|else|import|public|throws|case|enum|instanceof|return|transient|catch|extends|int|"
            + "short|try|char|final|interface|static|void|class|finally|long|strictfp|volatile|const|float|native|super|while)\\W{1}";
    
    StringBuilder debugBuffer = new StringBuilder();
    private Map<String, AttributeSet> styleMap;
    
    /**
     * Parse the provided reader and place the styled document in the provided document interface.
     * 
     * @param document
     * @param reader
     * @throws IOException
     */
    public void parseStream(DefaultStyledDocument document, Reader reader) throws IOException {
        styleMap = new HashMap<>();
        createStyles(document);
        int offset = 0;
        StringBuilder buffer = new StringBuilder();
        BufferedReader in = new BufferedReader(reader);
        char[] buf = new char[1];
        int read = -1;
        debugBuffer.setLength(0);
        while ((read = in.read(buf)) > -1) {
            if (read > 0) { 
                buffer.append(buf[0]);
                debugBuffer.append(buf[0]);
                String content = buffer.toString();
                if (content.matches("\\s")) {
                    try {
                        document.insertString(offset, content, styleMap.get(DEFAULT_STYLE));
                        buffer.setLength(0);
                        offset += content.length();
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                } else if (content.startsWith("//")) {
                    offset += parseSingleLineComment(buffer, in, document, offset);
                } else if (content.startsWith("/*")) {
                    offset += parseMultiLineComment(buffer, in, document, offset);
                } else if (content.startsWith("\"")) {
                    offset += parseDoubleQuote(buffer, in, document, offset);
                } else if (content.startsWith("'")) {
                    offset += parseSingleQuote(buffer, in, document, offset);
                } else if (content.matches(KEYWORDS_PATTERN)) {
                    try {
                        document.insertString(offset, content.substring(0, content.length() - 1), styleMap.get(KEYWORD_STYLE));
                        offset += content.length() - 1;
                        document.insertString(offset, content.substring(content.length() - 1), styleMap.get(DEFAULT_STYLE));
                        offset++;
                        buffer.setLength(0);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                } else if (content.matches("\\d+\\W{1}")) {
                    try {
                        document.insertString(offset, content.substring(0, content.length() - 1), styleMap.get(NUMBER_STYLE));
                        offset += content.length() - 1;
                        document.insertString(offset, content.substring(content.length() - 1), styleMap.get(DEFAULT_STYLE));
                        offset++;
                        buffer.setLength(0);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                } else if (content.matches("\\w+\\W{1}")) {
                    try {
                        document.insertString(offset, content, styleMap.get(DEFAULT_STYLE));
                        buffer.setLength(0);
                        offset += content.length();
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                } else if (content.matches(".+\\W")) {
                    try {
                        document.insertString(offset, content, styleMap.get(DEFAULT_STYLE));
                        buffer.setLength(0);
                        offset += content.length();
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    private int parseSingleLineComment(StringBuilder buffer, Reader in, Document document, int offset) throws IOException {
        return parseToString(buffer, in, document, offset, "\n", styleMap.get(COMMENT_STYLE));
    }
    
    private int parseMultiLineComment(StringBuilder buffer, Reader in, Document document, int offset) throws IOException {
        return parseToString(buffer, in, document, offset, "*/", styleMap.get(COMMENT_STYLE));
    }

    private int parseDoubleQuote(StringBuilder buffer, Reader in, Document document, int offset) throws IOException {
        return parseToString(buffer, in, document, offset, "\"", styleMap.get(STRING_STYLE));
    }

    private int parseSingleQuote(StringBuilder buffer, Reader in, Document document, int offset) throws IOException {
        return parseToString(buffer, in, document, offset, "'", styleMap.get(STRING_STYLE));
    }

    private int parseToString(StringBuilder buffer, Reader in, Document document, int offset, String stringToMatch, AttributeSet style) throws IOException {
        int counter = 0;
        
        char[] buf = new char[1];
        while ((in.read(buf)) > 0) {
            buffer.append(buf[0]);
            debugBuffer.append(buf[0]);
            String content = buffer.toString();
            if (content.endsWith(stringToMatch)) {
                try {
                    document.insertString(offset, content, style);
                    buffer.setLength(0);
                    counter += content.length();
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                break;
            }
        }        
        
        return counter;
    }
    
    private void createStyles(DefaultStyledDocument document) {
        Font currentFont = ApplicationPreferences.getCurrentFont();
        Style style = document.addStyle(DEFAULT_STYLE, null);
        StyleConstants.setFontFamily(style, currentFont.getFamily());
        StyleConstants.setFontSize(style, currentFont.getSize());
        StyleConstants.setBold(style, false);
        StyleConstants.setItalic(style, false);
        StyleConstants.setForeground(style, Color.BLACK);
        styleMap.put(DEFAULT_STYLE, style);
        style = document.addStyle(KEYWORD_STYLE, null);
        StyleConstants.setFontFamily(style, currentFont.getFamily());
        StyleConstants.setFontSize(style, currentFont.getSize());
        StyleConstants.setBold(style, true);
        StyleConstants.setItalic(style, false);
        StyleConstants.setForeground(style, new Color(127, 0, 85));
        styleMap.put(KEYWORD_STYLE, style);
        style = document.addStyle(COMMENT_STYLE, null);
        StyleConstants.setFontFamily(style, currentFont.getFamily());
        StyleConstants.setFontSize(style, currentFont.getSize());
        StyleConstants.setBold(style, false);
        StyleConstants.setItalic(style, false);
        StyleConstants.setForeground(style, new Color(63, 127, 95));
        styleMap.put(COMMENT_STYLE, style);
        style = document.addStyle(STRING_STYLE, null);
        StyleConstants.setFontFamily(style, currentFont.getFamily());
        StyleConstants.setFontSize(style, currentFont.getSize());
        StyleConstants.setBold(style, true);
        StyleConstants.setItalic(style, false);
        StyleConstants.setForeground(style, new Color(42, 0, 255));
        styleMap.put(STRING_STYLE, style);
        style = document.addStyle(NUMBER_STYLE, null);
        StyleConstants.setFontFamily(style, currentFont.getFamily());
        StyleConstants.setFontSize(style, currentFont.getSize());
        StyleConstants.setBold(style, true);
        StyleConstants.setItalic(style, false);
        StyleConstants.setForeground(style, new Color(255, 0, 0));
        styleMap.put(NUMBER_STYLE, style);
    }
    
    /**
     * Update the provided document when the font changes.
     * 
     * @param document
     */
    public static void updateDocumentStyles(DefaultStyledDocument document) {
        Font currentFont = ApplicationPreferences.getCurrentFont();
        for (String name : STYLES) {
            Style style = document.getStyle(name);
            StyleConstants.setFontFamily(style, currentFont.getFamily());
            StyleConstants.setFontSize(style, currentFont.getSize());
        }
    }
}
