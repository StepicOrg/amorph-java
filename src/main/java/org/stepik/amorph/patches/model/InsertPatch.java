package org.stepik.amorph.patches.model;

public class InsertPatch extends Patch {
    private int pos;
    private String text;

    public InsertPatch(int pos, String text) {
        this.pos = pos;
        this.text = text;
    }

    public int getPos() {
        return pos;
    }

    public String getText() {
        return text;
    }
}
