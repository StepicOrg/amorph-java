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

import java.util.*;

import org.stepik.amorph.tree.ITree;

public class MappingStore implements Iterable<Mapping> {

    private Map<ITree, ITree> srcs;
    private Map<ITree, ITree> dsts;

    public MappingStore(Set<Mapping> mappings) {
        this();
        for (Mapping m: mappings) link(m.getFirst(), m.getSecond());
    }

    public MappingStore() {
        srcs = new  HashMap<>();
        dsts = new HashMap<>();
    }

    public Set<Mapping> asSet() {
        return new AbstractSet<Mapping>() {

            @Override
            public Iterator<Mapping> iterator() {
                Iterator<ITree> it = srcs.keySet().iterator();
                return new Iterator<Mapping>() {
                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public Mapping next() {
                        ITree src = it.next();
                        if (src == null) return null;
                        return new Mapping(src, srcs.get(src));
                    }
                };
            }

            @Override
            public int size() {
                return srcs.keySet().size();
            }
        };
    }

    public MappingStore copy() {
        return new MappingStore(asSet());
    }

    public void link(ITree src, ITree dst) {
        srcs.put(src, dst);
        dsts.put(dst, src);
    }

    public void unlink(ITree src, ITree dst) {
        srcs.remove(src);
        dsts.remove(dst);
    }

    public ITree firstMappedSrcParent(ITree src) {
        ITree p = src.getParent();
        if (p == null) return null;
        else {
            while (!hasSrc(p)) {
                p = p.getParent();
                if (p == null) return p;
            }
            return p;
        }
    }

    public ITree firstMappedDstParent(ITree dst) {
        ITree p = dst.getParent();
        if (p == null) return null;
        else {
            while (!hasDst(p)) {
                p = p.getParent();
                if (p == null) return p;
            }
            return p;
        }
    }

    public ITree getDst(ITree src) {
        return srcs.get(src);
    }

    public ITree getSrc(ITree dst) {
        return dsts.get(dst);
    }

    public boolean hasSrc(ITree src) {
        return srcs.containsKey(src);
    }

    public boolean hasDst(ITree dst) {
        return dsts.containsKey(dst);
    }

    public boolean has(ITree src, ITree dst) {
        return srcs.get(src) == dst;
    }

    /**
     * Indicate whether or not a tree is mappable to another given tree.
     * @return true if both trees are not mapped and if the trees have the same type, false either.
     */
    public boolean isMatchable(ITree src, ITree dst) {
        return src.hasSameType(dst) && !(srcs.containsKey(src)  || dsts.containsKey(dst));
    }

    @Override
    public Iterator<Mapping> iterator() {
        return asSet().iterator();
    }

    @Override
    public String toString() {
        return asSet().toString();
    }

}
