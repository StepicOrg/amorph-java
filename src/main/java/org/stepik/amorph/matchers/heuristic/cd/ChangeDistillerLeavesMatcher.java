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

import org.simmetrics.StringMetrics;
import org.stepik.amorph.matchers.Mapping;
import org.stepik.amorph.matchers.MappingStore;
import org.stepik.amorph.matchers.Matcher;
import org.stepik.amorph.tree.ITree;
import org.stepik.amorph.tree.TreeUtils;

import java.util.*;

public class ChangeDistillerLeavesMatcher extends Matcher {

    public static final double LABEL_SIM_THRESHOLD = 0.5D;

    public ChangeDistillerLeavesMatcher(ITree src, ITree dst, MappingStore store) {
        super(src, dst, store);
    }

    @Override
    public void match() {
        List<ITree> dstLeaves = retainLeaves(TreeUtils.postOrder(dst));

        List<Mapping> leafMappings = new LinkedList<>();

        for (Iterator<ITree> srcLeaves = TreeUtils.leafIterator(
                TreeUtils.postOrderIterator(src)); srcLeaves.hasNext();) {
            for (ITree dstLeaf: dstLeaves) {
                ITree srcLeaf = srcLeaves.next();
                if (isMappingAllowed(srcLeaf, dstLeaf)) {
                    double sim = StringMetrics.qGramsDistance().compare(srcLeaf.getValue(), dstLeaf.getValue());
                    if (sim > LABEL_SIM_THRESHOLD) leafMappings.add(new Mapping(srcLeaf, dstLeaf));
                }
            }
        }

        Set<ITree> srcIgnored = new HashSet<>();
        Set<ITree> dstIgnored = new HashSet<>();
        Collections.sort(leafMappings, new LeafMappingComparator());
        while (leafMappings.size() > 0) {
            Mapping best = leafMappings.remove(0);
            if (!(srcIgnored.contains(best.getFirst()) || dstIgnored.contains(best.getSecond()))) {
                addMapping(best.getFirst(),best.getSecond());
                srcIgnored.add(best.getFirst());
                dstIgnored.add(best.getSecond());
            }
        }
    }

    public List<ITree> retainLeaves(List<ITree> trees) {
        Iterator<ITree> tIt = trees.iterator();
        while (tIt.hasNext()) {
            ITree t = tIt.next();
            if (!t.isLeaf()) tIt.remove();
        }
        return trees;
    }

    private class LeafMappingComparator implements Comparator<Mapping> {

        @Override
        public int compare(Mapping m1, Mapping m2) {
            return Double.compare(sim(m1), sim(m2));
        }

        public double sim(Mapping m) {

            return StringMetrics.qGramsDistance().compare(m.getFirst().getValue(), m.getSecond().getValue());
        }

    }
}
