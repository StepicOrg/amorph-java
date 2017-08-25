/*
 * This file is part of GumTree.
 *
 * GumTree is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GumTree is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GumTree.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2011-2015 Jean-Rémy Falleri <jr.falleri@gmail.com>
 * Copyright 2011-2015 Floréal Morandat <florealm@gmail.com>
 */

package org.stepik.amorph.actions;

import org.stepik.amorph.actions.model.*;

import java.util.List;

public class ActionUtil {
    public static void apply(Action a) {
        if (a instanceof InsertAction) {
            InsertAction action = ((InsertAction) a);
            action.getParent().insertChild(action.getNode(), action.getPosition());
        } else if (a instanceof UpdateAction) {
            UpdateAction action = ((UpdateAction) a);
            action.getNode().setValue(action.getValue());
        } else if (a instanceof MoveAction) {
            MoveAction action = ((MoveAction) a);
            action.getNode().getParent().getChildren().remove(action.getNode());
            action.getParent().insertChild(action.getNode(), action.getPosition());
        } else if (a instanceof DeleteAction) {
            DeleteAction action = ((DeleteAction) a);
            action.getNode().getParent().getChildren().remove(action.getNode());
        } else throw new RuntimeException("No such action: " + a );
    }

    public static void apply(List<Action> actions) {
        for (Action a: actions) {
            apply(a);
        }
    }
}
