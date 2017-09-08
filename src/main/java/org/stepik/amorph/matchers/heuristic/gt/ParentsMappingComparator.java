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
import org.stepik.amorph.tree.ITree;
import org.stepik.amorph.utils.StringAlgorithms;

import java.util.*;

public final class ParentsMappingComparator extends AbstractMappingComparator {

    public ParentsMappingComparator(List<Mapping> ambiguousMappings, MappingStore mappings, int maxTreeSize) {
        super(ambiguousMappings, mappings, maxTreeSize);
        for (Mapping ambiguousMapping: ambiguousMappings)
            similarities.put(ambiguousMapping, similarity(ambiguousMapping.getFirst(), ambiguousMapping.getSecond()));
    }

    protected double similarity(ITree src, ITree dst) {
        return 100D * parentsJaccardSimilarity(src, dst)
                + 10D * posInParentSimilarity(src, dst) + numberingSimilarity(src , dst);
    }

    protected double parentsJaccardSimilarity(ITree src, ITree dst) {
        List<ITree> srcParents = src.getParents();
        List<ITree> dstParents = dst.getParents();
        double numerator = (double) StringAlgorithms.lcss(srcParents, dstParents).size();
        double denominator = (double) srcParents.size() + (double) dstParents.size() - numerator;
        return numerator / denominator;
    }

}