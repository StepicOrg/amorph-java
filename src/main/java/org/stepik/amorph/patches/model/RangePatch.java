package org.stepik.amorph.patches.model;

public abstract class RangePatch extends Patch {
    protected int start;
    protected int stop;

    public int getStart() {
        return start;
    }

    public int getStop() {
        return stop;
    }
}
