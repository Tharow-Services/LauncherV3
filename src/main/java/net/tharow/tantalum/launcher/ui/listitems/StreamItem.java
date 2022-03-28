

package net.tharow.tantalum.launcher.ui.listitems;

public class StreamItem  {
    private final String text;
    private final String stream;

    public StreamItem(String text, String stream) {
        this.text = text;
        this.stream = stream;
    }

    public String getStream() { return stream; }
    public String toString() { return text; }
}
