/**
 * 
 */
package io.github.markbernard.jnotepad.parser;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * @author mxb122
 *
 */
public class JavaDocumentParser implements DocumentParser {
    private static final String DEFAULT_STYLE = "default";
    private static final String KEYWORD_STYLE = "keyword";
    private static final String COMMENT_STYLE = "comment";
    private static final String STRING_STYLE = "string";
    private static final String NUMBER_STYLE = "number";
    
    private static final Map<String, AttributeSet> STYLE_MAP = new HashMap<>();
    private static final String KEYWORDS_PATTERN = "(abstract|continue|for|new|switch|assert|default|goto|package|synchronized|boolean|do|if|private|"
            + "this|break|double|implements|protected|throw|byte|else|import|public|throws|case|enum|instanceof|return|transient|catch|extends|int|"
            + "short|try|char|final|interface|static|void|class|finally|long|strictfp|volatile|const|float|native|super|while)\\W{1}";
    
    StringBuilder debugBuffer = new StringBuilder();
    static {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, "Courier New");
        StyleConstants.setFontSize(style, 14);
        StyleConstants.setBold(style, false);
        StyleConstants.setItalic(style, false);
        StyleConstants.setForeground(style, Color.BLACK);
        STYLE_MAP.put(DEFAULT_STYLE, style);
        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, "Courier New");
        StyleConstants.setFontSize(style, 14);
        StyleConstants.setBold(style, false);
        StyleConstants.setItalic(style, false);
        StyleConstants.setForeground(style, new Color(127, 0, 85));
        STYLE_MAP.put(KEYWORD_STYLE, style);
        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, "Courier New");
        StyleConstants.setFontSize(style, 14);
        StyleConstants.setBold(style, false);
        StyleConstants.setItalic(style, false);
        StyleConstants.setForeground(style, new Color(63, 127, 95));
        STYLE_MAP.put(COMMENT_STYLE, style);
        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, "Courier New");
        StyleConstants.setFontSize(style, 14);
        StyleConstants.setBold(style, false);
        StyleConstants.setItalic(style, false);
        StyleConstants.setForeground(style, new Color(42, 0, 255));
        STYLE_MAP.put(STRING_STYLE, style);
        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, "Courier New");
        StyleConstants.setFontSize(style, 14);
        StyleConstants.setBold(style, false);
        StyleConstants.setItalic(style, false);
        StyleConstants.setForeground(style, new Color(255, 0, 0));
        STYLE_MAP.put(NUMBER_STYLE, style);
    }
    
    @Override
    public void parseStream(Document document, Reader reader) throws IOException {
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
                        document.insertString(offset, content, STYLE_MAP.get(DEFAULT_STYLE));
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
                        document.insertString(offset, content.substring(0, content.length() - 1), STYLE_MAP.get(KEYWORD_STYLE));
                        offset += content.length() - 1;
                        document.insertString(offset, content.substring(content.length() - 1), STYLE_MAP.get(DEFAULT_STYLE));
                        offset++;
                        buffer.setLength(0);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                } else if (content.matches("\\d+\\W{1}")) {
                    try {
                        document.insertString(offset, content.substring(0, content.length() - 1), STYLE_MAP.get(NUMBER_STYLE));
                        offset += content.length() - 1;
                        document.insertString(offset, content.substring(content.length() - 1), STYLE_MAP.get(DEFAULT_STYLE));
                        offset++;
                        buffer.setLength(0);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                } else if (content.matches("\\w+\\W{1}")) {
                    try {
                        document.insertString(offset, content, STYLE_MAP.get(DEFAULT_STYLE));
                        buffer.setLength(0);
                        offset += content.length();
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                } else if (content.matches(".+\\W")) {
                    try {
                        document.insertString(offset, content, STYLE_MAP.get(DEFAULT_STYLE));
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
        return parseToString(buffer, in, document, offset, "\n", STYLE_MAP.get(COMMENT_STYLE));
    }
    
    private int parseMultiLineComment(StringBuilder buffer, Reader in, Document document, int offset) throws IOException {
        return parseToString(buffer, in, document, offset, "*/", STYLE_MAP.get(COMMENT_STYLE));
    }

    private int parseDoubleQuote(StringBuilder buffer, Reader in, Document document, int offset) throws IOException {
        return parseToString(buffer, in, document, offset, "\"", STYLE_MAP.get(STRING_STYLE));
    }

    private int parseSingleQuote(StringBuilder buffer, Reader in, Document document, int offset) throws IOException {
        return parseToString(buffer, in, document, offset, "'", STYLE_MAP.get(STRING_STYLE));
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

    @Override
    public void reparse() {
    }
}
