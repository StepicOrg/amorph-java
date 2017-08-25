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

package org.stepik.amorph.actions.model;

import org.stepik.amorph.tree.ITree;

import java.util.Map;

public class UpdateAction extends Action {
    private String value;

    public UpdateAction(ITree node, String value) {
        super(node);
        this.value = value;
    }

    @Override
    public String getName() {
        return "UPD";
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return getName() + " " + node.toString() + " from " + node.getValue() + " to " + value;
    }

}
