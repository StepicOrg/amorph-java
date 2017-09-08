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

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractMappingComparator implements Comparator<Mapping> {

    protected List<Mapping> ambiguousMappings;

    protected Map<Mapping, Double> similarities = new HashMap<>();

    protected int maxTreeSize;

    protected MappingStore mappings;

    public AbstractMappingComparator(List<Mapping> ambiguousMappings, MappingStore mappings, int maxTreeSize) {
        this.maxTreeSize = maxTreeSize;
        this.mappings = mappings;
        this.ambiguousMappings = ambiguousMappings;
    }

    public int compare(Mapping m1, Mapping m2) {
        return Double.compare(similarities.get(m2), similarities.get(m1));
    }

    protected abstract double similarity(ITree src, ITree dst);

    protected double posInParentSimilarity(ITree src, ITree dst) {
        int posSrc = (src.isRoot()) ? 0 : src.getParent().getChildPosition(src);
        int posDst = (dst.isRoot()) ? 0 : dst.getParent().getChildPosition(dst);
        int maxSrcPos =  (src.isRoot()) ? 1 : src.getParent().getChildren().size();
        int maxDstPos =  (dst.isRoot()) ? 1 : dst.getParent().getChildren().size();
        int maxPosDiff = Math.max(maxSrcPos, maxDstPos);
        return 1D - ((double) Math.abs(posSrc - posDst) / (double) maxPosDiff);
    }

    protected double numberingSimilarity(ITree src, ITree dst) {
        return 1D - ((double) Math.abs(src.getId() - dst.getId())
                / (double) maxTreeSize);
    }

}
