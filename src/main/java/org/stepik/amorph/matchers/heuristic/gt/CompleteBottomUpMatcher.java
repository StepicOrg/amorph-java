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

import org.stepik.amorph.matchers.MappingStore;
import org.stepik.amorph.tree.ITree;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Match the nodes using a bottom-up approach. It browse the nodes of the source and destination trees
 * using a post-order traversal, testing if the two selected trees might be mapped. The two trees are mapped 
 * if they are mappable and have a dice coefficient greater than SIM_THRESHOLD. Whenever two trees are mapped
 * a exact ZS algorithm is applied to look to possibly forgotten nodes.
 */
public class CompleteBottomUpMatcher extends AbstractBottomUpMatcher {

    public CompleteBottomUpMatcher(ITree src, ITree dst, MappingStore store) {
        super(src, dst, store);
    }

    public void match() {
        for (ITree t: src.postOrder())  {
            if (t.isRoot()) {
                addMapping(t, this.dst);
                lastChanceMatch(t, this.dst);
                break;
            } else if (!(isSrcMatched(t) || t.isLeaf())) {
                List<ITree> srcCandidates = t.getParents().stream()
                        .filter(p -> Objects.equals(p.getType(), t.getType()))
                        .collect(Collectors.toList());

                List<ITree> dstCandidates = getDstCandidates(t);
                ITree srcBest = null;
                ITree dstBest = null;
                double max = -1D;
                for (ITree srcCand: srcCandidates) {
                    for (ITree dstCand: dstCandidates) {

                        double sim = jaccardSimilarity(srcCand, dstCand);
                        if (sim > max && sim >= SIM_THRESHOLD) {
                            max = sim;
                            srcBest = srcCand;
                            dstBest = dstCand;
                        }
                    }
                }

                if (srcBest != null) {
                    lastChanceMatch(srcBest, dstBest);
                    addMapping(srcBest, dstBest);
                }
            }
        }
    }
}
