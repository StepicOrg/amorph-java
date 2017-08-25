package org.stepik.amorph.patches.minimize;

public class DeleteUnit extends Unit {
    public int getStart() {
        return nodes.get(0).getPos();
    }

    public int getStop() {
        return nodes.get(nodes.size() - 1).getEndPos();
    }
}
