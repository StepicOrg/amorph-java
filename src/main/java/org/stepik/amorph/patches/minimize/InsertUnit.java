package org.stepik.amorph.patches.minimize;


import org.stepik.amorph.tree.ITree;

public class InsertUnit extends Unit {
    private int pos = -1;

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getText() {
        if (nodes.size() == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (ITree node : nodes) {
            builder.append(node.getText());
        }

        return builder.toString();
    }
}
