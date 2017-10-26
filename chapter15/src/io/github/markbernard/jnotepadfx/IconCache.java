/**
 * 
 */
package io.github.markbernard.jnotepadfx;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

/**
 * @author Mark Bernard
 *
 */
public class IconCache {
    private static final Map<String, Image> cache = new HashMap<>();

    /**
     * Load an image and cache it for later use.
     * 
     * @param reference
     * @return The requested image
     */
    public static Image loadImage(String reference) {
        Image result = cache.get(reference);

        if (result == null) {
            result = new Image(IconCache.class.getResourceAsStream("/res/icons/JNotepadIconSmall.png"));
            cache.put(reference, result);
        }
        
        return result;
    }
}
