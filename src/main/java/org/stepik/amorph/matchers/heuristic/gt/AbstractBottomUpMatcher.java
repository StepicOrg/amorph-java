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

package org.stepik.amorph.matchers.heuristic.gt;

import org.stepik.amorph.matchers.Mapping;
import org.stepik.amorph.matchers.MappingStore;
import org.stepik.amorph.matchers.Matcher;
import org.stepik.amorph.matchers.optimal.zs.ZsMatcher;
import org.stepik.amorph.tree.ITree;

import java.util.*;

public abstract class AbstractBottomUpMatcher extends Matcher {
    public static int SIZE_THRESHOLD =
            Integer.parseInt(System.getProperty("gt.bum.szt", "1000"));
    public static final double SIM_THRESHOLD =
            Double.parseDouble(System.getProperty("gt.bum.smt", "0.5"));

    protected org.stepik.amorph.tree.TreeMap srcIds;
    protected org.stepik.amorph.tree.TreeMap dstIds;

    protected org.stepik.amorph.tree.TreeMap mappedSrc;
    protected org.stepik.amorph.tree.TreeMap mappedDst;

    public AbstractBottomUpMatcher(ITree src, ITree dst, MappingStore store) {
        super(src, dst, store);
        srcIds = new org.stepik.amorph.tree.TreeMap(src);
        dstIds = new org.stepik.amorph.tree.TreeMap(dst);

        mappedSrc = new org.stepik.amorph.tree.TreeMap();
        mappedDst = new org.stepik.amorph.tree.TreeMap();
        for (Mapping m : store.asSet()) {
            mappedSrc.putTrees(m.getFirst());
            mappedDst.putTrees(m.getSecond());
        }
    }

    protected List<ITree> getDstCandidates(ITree src) {
        List<ITree> seeds = new ArrayList<>();
        for (ITree c: src.getDescendants()) {
            ITree m = mappings.getDst(c);
            if (m != null) seeds.add(m);
        }
        List<ITree> candidates = new ArrayList<>();
        Set<ITree> visited = new HashSet<>();
        for (ITree seed: seeds) {
            while (seed.getParent() != null) {
                ITree parent = seed.getParent();
                if (visited.contains(parent))
                    break;
                visited.add(parent);
                if (Objects.equals(parent.getType(), src.getType()) && !isDstMatched(parent) && !parent.isRoot())
                    candidates.add(parent);
                seed = parent;
            }
        }

        return candidates;
    }

    //FIXME checks if it is better or not to remove the already found mappings.
    protected void lastChanceMatch(ITree src, ITree dst) {
        ITree cSrc = src.deepCopy();
        ITree cDst = dst.deepCopy();
        removeMatched(cSrc, true);
        removeMatched(cDst, false);

        if (cSrc.getSize() < AbstractBottomUpMatcher.SIZE_THRESHOLD
                || cDst.getSize() < AbstractBottomUpMatcher.SIZE_THRESHOLD) {
            Matcher m = new ZsMatcher(cSrc, cDst, new MappingStore());
            m.match();
            for (Mapping candidate: m.getMappings()) {
                ITree left = srcIds.getTree(candidate.getFirst().getId());
                ITree right = dstIds.getTree(candidate.getSecond().getId());

                if (left.getId() == src.getId() || right.getId() == dst.getId()) {
//                    System.err.printf("Trying to map already mapped source node (%d == %d || %d == %d)\n",
//                            left.getId(), src.getId(), right.getId(), dst.getId());
                    continue;
                } else if (!isMappingAllowed(left, right)) {
//                    System.err.printf("Trying to map incompatible nodes (%s, %s)\n",
//                            left.toShortString(), right.toShortString());
                    continue;
                } else if (!left.getParent().hasSameType(right.getParent())) {
//                    System.err.printf("Trying to map nodes with incompatible parents (%s, %s)\n",
//                            left.getParent().toShortString(), right.getParent().toShortString());
                    continue;
                } else
                    addMapping(left, right);
            }
        }

        mappedSrc.putTrees(src);
        mappedDst.putTrees(dst);
    }

    /**
     * Remove mapped nodes from the tree. Be careful this method will invalidate
     * all the metrics of this tree and its descendants. If you need them, you need
     * to recompute them.
     */
    public ITree removeMatched(ITree tree, boolean isSrc) {
        for (ITree t: tree.getTrees()) {
            if ((isSrc && isSrcMatched(t)) || ((!isSrc) && isDstMatched(t))) {
                if (t.getParent() != null) t.getParent().getChildren().remove(t);
                t.setParent(null);
            }
        }
        tree.refresh();
        return tree;
    }

    @Override
    public boolean isMappingAllowed(ITree src, ITree dst) {
        return src.hasSameType(dst)
                && !(isSrcMatched(src) || isDstMatched(dst));
    }

    protected void addMapping(ITree src, ITree dst) {
        mappedSrc.putTree(src);
        mappedDst.putTree(dst);
        super.addMapping(src, dst);
    }

    boolean isSrcMatched(ITree tree) {
        return mappedSrc.contains(tree);
    }

    boolean isDstMatched(ITree tree) {
        return mappedDst.contains(tree);
    }
}
