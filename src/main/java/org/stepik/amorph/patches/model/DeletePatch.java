package org.stepik.amorph.patches.model;

public class DeletePatch extends RangePatch {
    public DeletePatch(int start, int stop) {
        this.start = start;
        this.stop = stop;
    }
}

