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

import java.util.List;
import java.util.Set;

import org.stepik.amorph.actions.model.*;
import org.stepik.amorph.actions.model.MoveAction;
import org.stepik.amorph.matchers.Mapping;
import org.stepik.amorph.matchers.Matcher;
import org.stepik.amorph.tree.TreeContext;

public class RootsClassifier extends TreeClassifier {

    public RootsClassifier(TreeContext src, TreeContext dst, Set<Mapping> rawMappings, List<Action> script) {
        super(src, dst, rawMappings, script);
    }

    public RootsClassifier(TreeContext src, TreeContext dst, Matcher m) {
        super(src, dst, m);
    }

    public void classify() {
        for (Action a: actions) {
            if (a instanceof DeleteAction) srcDelTrees.add(a.getNode());
            else if (a instanceof InsertAction)
                dstAddTrees.add(a.getNode());
            else if (a instanceof UpdateAction) {
                srcUpdTrees.add(a.getNode());
                dstUpdTrees.add(mappings.getDst(a.getNode()));
            } else if (a instanceof MoveAction) {
                srcMvTrees.add(a.getNode());
                dstMvTrees.add(mappings.getDst(a.getNode()));
            }
        }
    }
}
