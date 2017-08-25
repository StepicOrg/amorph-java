package org.stepik.amorph.patches.minimize;

import org.stepik.amorph.tree.ITree;

import java.util.ArrayList;
import java.util.List;

public abstract class Unit {
    protected List<ITree> nodes = new ArrayList<>();

    public void addNode(ITree node) {
        nodes.add(node);
    }

    public List<ITree> getNodes() {
        return nodes;
    }
}
