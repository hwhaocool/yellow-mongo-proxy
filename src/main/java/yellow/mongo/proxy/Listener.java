package yellow.mongo.proxy;

import yellow.mongo.proxy.element.Document;

/**
 *
 * @author Thibault Debatty
 */
public interface Listener {

    /**
     *
     * @param doc BSON document reconstructed from the message.
     */
    void run(Document doc);
}
