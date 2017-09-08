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

package org.stepik.amorph.actions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.stepik.amorph.actions.model.Action;
import org.stepik.amorph.matchers.Mapping;
import org.stepik.amorph.matchers.MappingStore;
import org.stepik.amorph.matchers.Matcher;
import org.stepik.amorph.tree.ITree;
import org.stepik.amorph.tree.TreeContext;

public abstract class TreeClassifier {

    protected Set<ITree> srcUpdTrees;

    protected Set<ITree> dstUpdTrees;

    protected Set<ITree> srcMvTrees;

    protected Set<ITree> dstMvTrees;

    protected Set<ITree> srcDelTrees;

    protected Set<ITree> dstAddTrees;

    protected TreeContext src;

    protected TreeContext dst;

    protected MappingStore mappings;

    protected List<Action> actions;

    public TreeClassifier(TreeContext src, TreeContext dst, Set<Mapping> rawMappings, List<Action> actions) {
        this(src, dst, rawMappings);
        this.actions = actions;
        classify();
    }

    public TreeClassifier(TreeContext src, TreeContext dst, Matcher m) {
        this(src, dst, m.getMappingSet());
        ActionGenerator g = new ActionGenerator(src.getRoot(), dst.getRoot(), m.getMappings());
        g.generate();
        this.actions = g.getActions();
        classify();
    }

    private TreeClassifier(TreeContext src, TreeContext dst, Set<Mapping> rawMappings) {
        this.src = src;
        this.dst = dst;
        this.mappings = new MappingStore(rawMappings);
        this.srcDelTrees = new HashSet<>();
        this.srcMvTrees = new HashSet<>();
        this.srcUpdTrees = new HashSet<>();
        this.dstMvTrees = new HashSet<>();
        this.dstAddTrees = new HashSet<>();
        this.dstUpdTrees = new HashSet<>();
    }

    public abstract void classify();

    public Set<ITree> getSrcUpdTrees() {
        return srcUpdTrees;
    }

    public Set<ITree> getDstUpdTrees() {
        return dstUpdTrees;
    }

    public Set<ITree> getSrcMvTrees() {
        return srcMvTrees;
    }

    public Set<ITree> getDstMvTrees() {
        return dstMvTrees;
    }

    public Set<ITree> getSrcDelTrees() {
        return srcDelTrees;
    }

    public Set<ITree> getDstAddTrees() {
        return dstAddTrees;
    }

}
