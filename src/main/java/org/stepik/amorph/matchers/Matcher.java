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

package org.stepik.amorph.matchers;

import org.stepik.amorph.tree.ITree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public abstract class Matcher {

    public static final Logger LOGGER = Logger.getLogger("com.github.gumtreediff.matchers");

    protected final ITree src;

    protected final ITree dst;

    protected final MappingStore mappings;

    public Matcher(ITree src, ITree dst, MappingStore mappings) {
        this.src = src;
        this.dst = dst;
        this.mappings = mappings;
    }

    public abstract void match();

    public MappingStore getMappings() {
        return mappings;
    }

    public Set<Mapping> getMappingSet() {
        return mappings.asSet();
    }

    public ITree getSrc() {
        return src;
    }

    public ITree getDst() {
        return dst;
    }

    protected void addMapping(ITree src, ITree dst) {
        mappings.link(src, dst);
    }

    protected void addMappingRecursively(ITree src, ITree dst) {
        List<ITree> srcTrees = src.getTrees();
        List<ITree> dstTrees = dst.getTrees();
        for (int i = 0; i < srcTrees.size(); i++) {
            ITree currentSrcTree = srcTrees.get(i);
            ITree currentDstTree = dstTrees.get(i);
            addMapping(currentSrcTree, currentDstTree);
        }
    }

    protected double chawatheSimilarity(ITree src, ITree dst) {
        int max = Math.max(src.getDescendants().size(), dst.getDescendants().size());
        return (double) numberOfCommonDescendants(src, dst) / (double) max;
    }

    protected double diceSimilarity(ITree src, ITree dst) {
        double c = (double) numberOfCommonDescendants(src, dst);
        return (2D * c) / ((double) src.getDescendants().size() + (double) dst.getDescendants().size());
    }

    protected double jaccardSimilarity(ITree src, ITree dst) {
        double num = (double) numberOfCommonDescendants(src, dst);
        double den = (double) src.getDescendants().size() + (double) dst.getDescendants().size() - num;
        return num / den;
    }

    protected int numberOfCommonDescendants(ITree src, ITree dst) {
        Set<ITree> dstDescandants = new HashSet<>(dst.getDescendants());
        int common = 0;

        for (ITree t : src.getDescendants()) {
            ITree m = mappings.getDst(t);
            if (m != null && dstDescandants.contains(m))
                common++;
        }

        return common;
    }

    public boolean isMappingAllowed(ITree src, ITree dst) {
        return src.hasSameType(dst) && !(mappings.hasSrc(src) || mappings.hasDst(dst));
    }
}
