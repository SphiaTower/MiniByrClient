package com.example.qingunext.app.page_thread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UBBNode {
    static final String TEXT = "<text>";

    String tag = null;
    String[] attr = null;
    String img = null;
    UBBNode parent = null;
    List<UBBNode> children = null;
    boolean closed = false;
    boolean isEmpty = false;

    UBBNode(UBBNode parent, String tag, String[] attr, String img,
            boolean isEmpty) {
        this.parent = parent;
        this.tag = tag.toLowerCase();
        this.attr = attr;
        this.img = img;
        this.isEmpty = isEmpty;
        this.closed = isEmpty;
        this.children = isEmpty ? null : new ArrayList<UBBNode>();
    }

    UBBNode(UBBNode parent, String text) {
        String[] text1 = {text};
        this.parent = parent;
        this.tag = TEXT;
        this.attr = text1;
        this.img = text;
        this.closed = true;
        this.isEmpty = true;
    }

    final void addChild(UBBNode child) {
        children.add(child);
    }

    /**
     *
     */
    public final String toString() {
        return "[tag=\"" + tag + "\",attr=\"" + Arrays.toString(attr) + "\",closed=" + closed
                + ",children="
                + (children != null ? "" + children.size() : "null") + "]";
    }

    /**
     * @param i
     * @return
     */
    final String toString(int i) {
        StringBuilder buf = new StringBuilder();
        for (int j = i; --j >= 0; ) {
            buf.append(' ');
        }
        buf.append(toString()).append("\n");
        if (children != null && children.size() > 0) {
            for (UBBNode aChildren : children) {
                buf.append(aChildren.toString(i + 2));
            }
        }
        return buf.toString();
    }

}
