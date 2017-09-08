/*
 * MIT License
 *
 * Copyright (c) 2017 Nikita Lapkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
