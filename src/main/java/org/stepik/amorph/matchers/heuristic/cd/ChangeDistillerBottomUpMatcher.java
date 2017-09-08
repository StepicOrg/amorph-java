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

package org.stepik.amorph.matchers.heuristic.cd;

import org.stepik.amorph.matchers.MappingStore;
import org.stepik.amorph.matchers.Matcher;
import org.stepik.amorph.tree.ITree;
import org.stepik.amorph.tree.TreeUtils;

import java.util.List;

public class ChangeDistillerBottomUpMatcher extends Matcher {

    public static final double STRUCT_SIM_THRESHOLD_1 = 0.6D;

    public static final double STRUCT_SIM_THRESHOLD_2 = 0.4D;

    public ChangeDistillerBottomUpMatcher(ITree src, ITree dst, MappingStore store) {
        super(src, dst, store);
    }

    @Override
    public void match() {
        List<ITree> poDst = TreeUtils.postOrder(dst);
        for (ITree src: this.src.postOrder()) {
            int l = numberOfLeafs(src);
            for (ITree dst: poDst) {
                if (isMappingAllowed(src, dst) && !(src.isLeaf() || dst.isLeaf())) {
                    double sim = chawatheSimilarity(src, dst);
                    if ((l > 4 && sim >= STRUCT_SIM_THRESHOLD_1) || (l <= 4 && sim >= STRUCT_SIM_THRESHOLD_2)) {
                        addMapping(src, dst);
                        break;
                    }
                }
            }
        }
    }

    private int numberOfLeafs(ITree root) {
        int l = 0;
        for (ITree t : root.getDescendants())
            if (t.isLeaf())
                l++;
        return l;
    }
}
