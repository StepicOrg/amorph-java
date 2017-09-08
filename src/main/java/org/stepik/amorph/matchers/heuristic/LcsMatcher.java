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

import org.stepik.amorph.utils.StringAlgorithms;
import org.stepik.amorph.matchers.MappingStore;
import org.stepik.amorph.matchers.Matcher;
import org.stepik.amorph.matchers.Register;
import org.stepik.amorph.tree.ITree;
import org.stepik.amorph.tree.TreeUtils;

import java.util.List;

@Register(id = "lcs")
public class LcsMatcher extends Matcher {

    public LcsMatcher(ITree src, ITree dst, MappingStore store) {
        super(src, dst, store);
    }

    @Override
    public void match() {
        List<ITree> srcSeq = TreeUtils.preOrder(src);
        List<ITree> dstSeq = TreeUtils.preOrder(dst);
        List<int[]> lcs = StringAlgorithms.lcss(srcSeq, dstSeq);
        System.out.println(lcs.size());
        for (int[] x: lcs) {

            ITree t1 = srcSeq.get(x[0]);
            ITree t2 = dstSeq.get(x[1]);
            addMapping(t1, t2);
        }
    }
}
