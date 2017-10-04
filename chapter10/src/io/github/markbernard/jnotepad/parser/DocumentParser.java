/**
 * 
 */
package io.github.markbernard.jnotepad.parser;

import java.io.IOException;
import java.io.Reader;

import javax.swing.text.Document;

/**
 * @author mxb122
 *
 */
public interface DocumentParser {
    /**
     * Load the document with the contents of the stream with appropriate styling added.
     * 
     * @param document
     * @param reader
     * @throws IOException 
     */
    public void parseStream(Document document, Reader reader) throws IOException;
    /**
     * Reparse the entire document
     */
    public void reparse();
}
