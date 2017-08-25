package org.stepik.amorph.patches.model;

public class UpdatePatch extends RangePatch {
    private String value;

    public UpdatePatch(int start, int stop, String value) {
        this.start = start;
        this.stop = stop;
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
