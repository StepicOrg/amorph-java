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

import org.stepik.amorph.utils.HungarianAlgorithm;
import org.stepik.amorph.matchers.MappingStore;
import org.stepik.amorph.matchers.MultiMappingStore;
import org.stepik.amorph.tree.ITree;

import java.util.*;

public class HungarianSubtreeMatcher extends AbstractSubtreeMatcher {

    public HungarianSubtreeMatcher(ITree src, ITree dst, MappingStore store) {
        super(src, dst, store);
    }

    public void filterMappings(MultiMappingStore multiMappings) {
        List<MultiMappingStore> ambiguousList = new ArrayList<>();
        Set<ITree> ignored = new HashSet<>();
        for (ITree src: multiMappings.getSrcs())
            if (multiMappings.isSrcUnique(src))
                addMappingRecursively(src, multiMappings.getDst(src).iterator().next());
            else if (!ignored.contains(src)) {
                MultiMappingStore ambiguous = new MultiMappingStore();
                Set<ITree> adsts = multiMappings.getDst(src);
                Set<ITree> asrcs = multiMappings.getSrc(multiMappings.getDst(src).iterator().next());
                for (ITree asrc : asrcs)
                    for (ITree adst: adsts)
                        ambiguous.link(asrc ,adst);
                ambiguousList.add(ambiguous);
                ignored.addAll(asrcs);
            }

        Collections.sort(ambiguousList, new MultiMappingComparator());

        for (MultiMappingStore ambiguous: ambiguousList) {
            System.out.println("hungarian try.");
            List<ITree> lstSrcs = new ArrayList<>(ambiguous.getSrcs());
            List<ITree> lstDsts = new ArrayList<>(ambiguous.getDsts());
            double[][] matrix = new double[lstSrcs.size()][lstDsts.size()];
            for (int i = 0; i < lstSrcs.size(); i++)
                for (int j = 0; j < lstDsts.size(); j++)
                    matrix[i][j] = cost(lstSrcs.get(i), lstDsts.get(j));

            HungarianAlgorithm hgAlg = new HungarianAlgorithm(matrix);
            int[] solutions = hgAlg.execute();
            for (int i = 0; i < solutions.length; i++) {
                int dstIdx = solutions[i];
                if (dstIdx != -1) addMappingRecursively(lstSrcs.get(i), lstDsts.get(dstIdx));
            }
        }
    }

    private double cost(ITree src, ITree dst) {
        return 111D - sim(src, dst);
    }

    private class MultiMappingComparator implements Comparator<MultiMappingStore> {

        @Override
        public int compare(MultiMappingStore m1, MultiMappingStore m2) {
            return Integer.compare(impact(m1), impact(m2));
        }

        public int impact(MultiMappingStore m) {
            int impact = 0;
            for (ITree src: m.getSrcs()) {
                int pSize = src.getParents().size();
                if (pSize > impact) impact = pSize;
            }
            for (ITree src: m.getDsts()) {
                int pSize = src.getParents().size();
                if (pSize > impact) impact = pSize;
            }
            return impact;
        }

    }

}
