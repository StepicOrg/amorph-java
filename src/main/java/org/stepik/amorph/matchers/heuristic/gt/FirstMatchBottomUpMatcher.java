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

/**
 * Match the nodes using a bottom-up approach. It browse the nodes of the source and destination trees
 * using a post-order traversal, testing if the two selected trees might be mapped. The two trees are mapped 
 * if they are mappable and have a dice coefficient greater than SIM_THRESHOLD. Whenever two trees are mapped
 * a exact ZS algorithm is applied to look to possibly forgotten nodes.
 */
public class FirstMatchBottomUpMatcher extends AbstractBottomUpMatcher {

    public FirstMatchBottomUpMatcher(ITree src, ITree dst, MappingStore store) {
        super(src, dst, store);
    }

    public void match() {
        match(removeMatched(src, true), removeMatched(dst, false));
    }

    private void match(ITree src, ITree dst) {
        for (ITree s: src.postOrder())  {
            for (ITree d: dst.postOrder()) {
                if (isMappingAllowed(s, d) && !(s.isLeaf() || d.isLeaf())) {
                    double sim = jaccardSimilarity(s, d);
                    if (sim >= SIM_THRESHOLD || (s.isRoot() && d.isRoot()) ) {
                        if (!(areDescendantsMatched(s, true) || areDescendantsMatched(d, false)))
                            lastChanceMatch(s, d);
                        addMapping(s, d);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Indicate whether or not all the descendants of the trees are already mapped.
     */
    public boolean areDescendantsMatched(ITree tree, boolean isSrc) {
        for (ITree c: tree.getDescendants())
            if (!((isSrc && isSrcMatched(c)) || (!isSrc && isDstMatched(tree)))) // FIXME ugly but this class is unused
                return false;
        return true;
    }

}
