package org.stepik.amorph.patches.minimize;

import org.stepik.amorph.tree.ITree;
import org.stepik.amorph.tree.TreeUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class DescendantsReducer {
    private Set<ITree> nodes;
    private Set<ITree> roots;

    public DescendantsReducer() {
        // 2 types of patched nodes are maintained
        // nodes is a set of all patched nodes.
        // roots is a subset of nodes containing only
        // roots of patched subtrees
        nodes = new LinkedHashSet<>();
        roots = new LinkedHashSet<>();
    }

    public void addNode(ITree node, ITree parent) {
        nodes.add(node);
        if (!nodes.contains(parent)) {
            roots.add(node);
            roots.removeAll(node.getChildren());
        }
    }

    public void addTree(ITree root, ITree parent, Function<ITree, List<ITree>> order) {
        List<ITree> descendants = order.apply(root);
        nodes.addAll(descendants);

        // all descendants are now not patched roots by definition
        // as they are under patched node
        roots.removeAll(descendants);

        // add possible root
        addNode(root, parent);
    }

    public void removeNode(ITree node) {
        nodes.remove(node);
        roots.remove(node);
    }

    public void removeTree(ITree root) {
        for (ITree node : TreeUtils.postOrder(root)) {
            removeNode(node);
        }
    }

    public boolean hasNode(ITree node) {
        return nodes.contains(node);
    }

    public Set<ITree> getNodes() {
        return nodes;
    }

    public Set<ITree> getRoots() {
        return roots;
    }
}
