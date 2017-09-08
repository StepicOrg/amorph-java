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

package org.stepik.amorph.matchers.heuristic;

import org.stepik.amorph.matchers.MappingStore;
import org.stepik.amorph.matchers.Matcher;
import org.stepik.amorph.tree.ITree;

import java.util.*;


/**
 * Match the nodes using a bottom-up approach. It browse the nodes of the source and destination trees
 * using a post-order traversal, testing if the two selected trees might be mapped. The two trees are mapped 
 * if they are mappable and have a dice coefficient greater than SIM_THRESHOLD. Whenever two trees are mapped
 * a exact ZS algorithm is applied to look to possibly forgotten nodes.
 */
public class XyBottomUpMatcher extends Matcher {

    private static final double SIM_THRESHOLD = Double.parseDouble(System.getProperty("gumtree.match.xy.sim", "0.5"));

    public XyBottomUpMatcher(ITree src, ITree dst, MappingStore store) {
        super(src, dst, store);
    }

    public void match() {
        for (ITree src: this.src.postOrder())  {
            if (src.isRoot()) {
                addMapping(src, this.dst);
                lastChanceMatch(src, this.dst);
            } else if (!(mappings.hasSrc(src) || src.isLeaf())) {
                Set<ITree> candidates = getDstCandidates(src);
                ITree best = null;
                double max = -1D;

                for (ITree cand: candidates ) {
                    double sim = jaccardSimilarity(src, cand);
                    if (sim > max && sim >= SIM_THRESHOLD) {
                        max = sim;
                        best = cand;
                    }
                }

                if (best != null) {
                    lastChanceMatch(src, best);
                    addMapping(src, best);
                }
            }
        }
    }

    private Set<ITree> getDstCandidates(ITree src) {
        Set<ITree> seeds = new HashSet<>();
        for (ITree c: src.getDescendants()) {
            ITree m = mappings.getDst(c);
            if (m != null) seeds.add(m);
        }
        Set<ITree> candidates = new HashSet<>();
        Set<ITree> visited = new HashSet<>();
        for (ITree seed: seeds) {
            while (seed.getParent() != null) {
                ITree parent = seed.getParent();
                if (visited.contains(parent))
                    break;
                visited.add(parent);
                if (Objects.equals(parent.getType(), src.getType()) && !mappings.hasDst(parent))
                    candidates.add(parent);
                seed = parent;
            }
        }

        return candidates;
    }

    private void lastChanceMatch(ITree src, ITree dst) {
        Map<String, List<ITree>> srcKinds = new HashMap<>();
        Map<String, List<ITree>> dstKinds = new HashMap<>();
        for (ITree c: src.getChildren()) {
            if (!srcKinds.containsKey(c.getType())) srcKinds.put(c.getType(), new ArrayList<>());
            srcKinds.get(c.getType()).add(c);
        }
        for (ITree c: dst.getChildren()) {
            if (!dstKinds.containsKey(c.getType())) dstKinds.put(c.getType(), new ArrayList<>());
            dstKinds.get(c.getType()).add(c);
        }

        for (String t: srcKinds.keySet())
            if (dstKinds.get(t) != null && srcKinds.get(t).size() == dstKinds.get(t).size() && srcKinds.get(t).size() == 1)
                addMapping(srcKinds.get(t).get(0), dstKinds.get(t).get(0));
    }
}
