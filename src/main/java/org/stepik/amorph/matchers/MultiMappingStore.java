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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.stepik.amorph.tree.ITree;

public class MultiMappingStore implements Iterable<Mapping> {

    private Map<ITree, Set<ITree>> srcs;

    private Map<ITree, Set<ITree>> dsts;

    public MultiMappingStore(Set<Mapping> mappings) {
        this();
        for (Mapping m: mappings) link(m.getFirst(), m.getSecond());
    }

    public MultiMappingStore() {
        srcs = new  HashMap<>();
        dsts = new HashMap<>();
    }

    public Set<Mapping> getMappings() {
        Set<Mapping> mappings = new HashSet<>();
        for (ITree src : srcs.keySet())
            for (ITree dst: srcs.get(src))
                mappings.add(new Mapping(src, dst));
        return mappings;
    }

    public void link(ITree src, ITree dst) {
        if (!srcs.containsKey(src)) srcs.put(src, new HashSet<ITree>());
        srcs.get(src).add(dst);
        if (!dsts.containsKey(dst)) dsts.put(dst, new HashSet<ITree>());
        dsts.get(dst).add(src);
    }

    public void unlink(ITree src, ITree dst) {
        srcs.get(src).remove(dst);
        dsts.get(dst).remove(src);
    }

    public Set<ITree> getDst(ITree src) {
        return srcs.get(src);
    }

    public Set<ITree> getSrcs() {
        return srcs.keySet();
    }

    public Set<ITree> getDsts() {
        return dsts.keySet();
    }

    public Set<ITree> getSrc(ITree dst) {
        return dsts.get(dst);
    }

    public boolean hasSrc(ITree src) {
        return srcs.containsKey(src);
    }

    public boolean hasDst(ITree dst) {
        return dsts.containsKey(dst);
    }

    public boolean has(ITree src, ITree dst) {
        return srcs.get(src).contains(dst);
    }

    public boolean isSrcUnique(ITree src) {
        return srcs.get(src).size() == 1 && dsts.get(srcs.get(src).iterator().next()).size() == 1;
    }

    @Override
    public Iterator<Mapping> iterator() {
        return getMappings().iterator();
    }

}
