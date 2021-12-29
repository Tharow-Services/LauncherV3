package net.tharow.tantalum.ui.controls;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public record SimpleDocumentListener(Runnable runnable) implements DocumentListener {
    /**
     * A Simple Document Listener
     *
     * @param runnable what to run during Doc events
     */
    public SimpleDocumentListener {}

    /**
     * Gives notification that there was an insert into the document.  The
     * range given by the DocumentEvent bounds the freshly inserted region.
     *
     * @param e the document event
     */
    @Override
    public void insertUpdate(DocumentEvent e) {
        this.runnable.run();
    }

    /**
     * Gives notification that a portion of the document has been
     * removed.  The range is given in terms of what the view last
     * saw (that is, before updating sticky positions).
     *
     * @param e the document event
     */
    @Override
    public void removeUpdate(DocumentEvent e) {
        this.runnable.run();
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
    @Override
    public void changedUpdate(DocumentEvent e) {
        this.runnable.run();
    }
}
