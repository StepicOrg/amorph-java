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
import org.stepik.amorph.matchers.MultiMappingStore;
import org.stepik.amorph.tree.ITree;

import java.util.*;

public class GreedySubtreeMatcher extends AbstractSubtreeMatcher {

    public GreedySubtreeMatcher(ITree src, ITree dst, MappingStore store) {
        super(src, dst, store);
    }

    public void filterMappings(MultiMappingStore multiMappings) {
        // Select unique mappings first and extract ambiguous mappings.
        List<Mapping> ambiguousList = new LinkedList<>();
        Set<ITree> ignored = new HashSet<>();
        for (ITree src: multiMappings.getSrcs()) {
            if (multiMappings.isSrcUnique(src))
                addMappingRecursively(src, multiMappings.getDst(src).iterator().next());
            else if (!ignored.contains(src)) {
                Set<ITree> adsts = multiMappings.getDst(src);
                Set<ITree> asrcs = multiMappings.getSrc(multiMappings.getDst(src).iterator().next());
                for (ITree asrc : asrcs)
                    for (ITree adst: adsts)
                        ambiguousList.add(new Mapping(asrc, adst));
                ignored.addAll(asrcs);
            }
        }

        // Rank the mappings by score.
        Set<ITree> srcIgnored = new HashSet<>();
        Set<ITree> dstIgnored = new HashSet<>();
        Collections.sort(ambiguousList, new SiblingsMappingComparator(ambiguousList, mappings, getMaxTreeSize()));

        // Select the best ambiguous mappings
        retainBestMapping(ambiguousList, srcIgnored, dstIgnored);
    }

}
