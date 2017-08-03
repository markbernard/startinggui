/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Mark Bernard
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in the 
 * Software without restriction, including without limitation the rights to use, copy, 
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.markbernard.jnotepad;

import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * @author Mark Bernard
 */
public class NumberDocumentFilter extends DocumentFilter {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+$");
    
    @Override
    public void insertString(FilterBypass fb, int offset, 
            String string, AttributeSet attr) throws BadLocationException {
        if (containsOnlyNumeric(string)) {
            fb.insertString(offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, 
            String text, AttributeSet attrs) throws BadLocationException {
        if (containsOnlyNumeric(text)) {
            fb.replace(offset, length, text, attrs);
        }
    }

    private boolean containsOnlyNumeric(String text) {
        return NUMBER_PATTERN.matcher(text).find();
    }
}
