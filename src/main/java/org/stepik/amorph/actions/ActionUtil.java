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

import org.stepik.amorph.actions.model.*;
import org.stepik.amorph.tree.ITree;

import java.util.List;

public class ActionUtil {
    public static void apply(Action a) {
        ITree node = a.getNode();
        
        if (a instanceof InsertAction) {
            InsertAction action = ((InsertAction) a);
            action.getParent().insertChild(node, action.getPosition());
        } else if (a instanceof UpdateAction) {
            UpdateAction action = ((UpdateAction) a);
            node.setValue(action.getValue());
        } else if (a instanceof MoveAction) {
            MoveAction action = ((MoveAction) a);
            node.getParent().getChildren().remove(node);
            action.getParent().insertChild(node, action.getPosition());
        } else if (a instanceof DeleteAction) {
            node.getParent().getChildren().remove(node);
        } else
            throw new RuntimeException("No such action: " + a );
    }

    public static void apply(List<Action> actions) {
        for (Action a: actions) {
            apply(a);
        }
    }
}
