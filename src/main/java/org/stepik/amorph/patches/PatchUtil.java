package org.stepik.amorph.patches;

import org.stepik.amorph.actions.ActionUtil;
import org.stepik.amorph.actions.model.*;
import org.stepik.amorph.patches.model.DeletePatch;
import org.stepik.amorph.patches.model.InsertPatch;
import org.stepik.amorph.patches.model.Patch;
import org.stepik.amorph.patches.model.UpdatePatch;
import org.stepik.amorph.patches.minimize.DeleteUnit;
import org.stepik.amorph.patches.minimize.DescendantsReducer;
import org.stepik.amorph.patches.minimize.InsertUnit;
import org.stepik.amorph.tree.ITree;
import org.stepik.amorph.tree.TreeUtils;

import java.util.*;

public class PatchUtil {
    public static List<Patch> patchesFromActions(List<Action> actions) {
        List<Patch> patches = new ArrayList<>();

        List<UpdateAction> updateActions = new ArrayList<>();
        DescendantsReducer insertReducer = new DescendantsReducer();
        DescendantsReducer deleteReducer = new DescendantsReducer();
        for (Action action : actions) {
            ITree node = action.getNode();

            if (action instanceof UpdateAction) {
                updateActions.add((UpdateAction) action);
                // need to apply as it can reference to inserted node
                ActionUtil.apply(action);
            }

            if (action instanceof DeleteAction) {
                deleteReducer.addNode(node, node.getParent());
                // no need to apply as we can get text position information from it later
            }

            // NOTE: insert action affects only one node without children
            if (action instanceof InsertAction) {
                InsertAction insert = (InsertAction) action;
                ITree parent = insert.getParent();
                insertReducer.addNode(node, parent);

                // need to apply insert to locate text positions from siblings later
                ActionUtil.apply(insert);
            }

            // move action is translated to delete and insert
            // NOTE: move action affects *whole* subtree with all children of node
            if (action instanceof MoveAction) {
                MoveAction move = (MoveAction) action;
                ITree parent = move.getParent();

                insertReducer.addTree(node, parent, TreeUtils::preOrder);

                ITree oldParent = node.getParent();
                int oldPos = oldParent.getChildPosition(node);

                // we want to leave deleted tree as it can give us
                // position information later so we are creating a copy
                // but with new pks as they must be unique
                ITree deleted = node.deepCopy();
                TreeUtils.regeneratePks(deleted);
                deleteReducer.addTree(deleted, oldParent, TreeUtils::postOrder);

                ActionUtil.apply(move);
                // insert tree copy to old place
                oldParent.insertChild(deleted, oldPos);
            }
        }

        // delete can reference to moved node.
        // in this case we should apply it and remove from list
        List<ITree> redundantTrees = new ArrayList<>();
        for (ITree root : deleteReducer.getRoots())
            if (insertReducer.hasNode(root)) {
                redundantTrees.add(root);

                for (ITree descendant : TreeUtils.postOrder(root)) {
                    ActionUtil.apply(new DeleteAction(descendant));
                }
            }

        for (ITree root : redundantTrees) {
            deleteReducer.removeTree(root);
        }

        // filter updates
        for (UpdateAction update : updateActions) {
            ITree node = update.getNode();

            // updates to inserted nodes cannot be displayed
            if (!insertReducer.hasNode(node))
                patches.add(new UpdatePatch(node.getPos(), node.getEndPos(), update.getValue()));
        }

        // filter inserts
        // patches count is reduced by displaying only insert roots.
        // if insertions are located nearby we cannot say exact text position for
        // all patches after first in group so we should combine them all in InsertUnit
        List<InsertUnit> insertUnits = new ArrayList<>();
        Map<ITree, InsertUnit> nodeToInsertUnit = new HashMap<>();
        for (ITree node : insertReducer.getRoots()) {
            ITree parent = node.getParent();

            InsertUnit tmp = new InsertUnit();
            tmp.addNode(node);

            // inserting tree root
            if (parent == null) {
                // FIXME: is it always zero?
                tmp.setPos(0);
                insertUnits.add(tmp);
                nodeToInsertUnit.put(node, tmp);
                continue;
            }

            int pos = parent.getChildPosition(node);
            // insertions are generated from left to right
            // so insert unit can be extended only from right side
            if (pos > 0) {
                ITree sibling = parent.getChild(pos - 1);

                // if left sibling is in insert unit, we can extend it
                if (nodeToInsertUnit.containsKey(sibling)) {
                    InsertUnit unit = nodeToInsertUnit.get(sibling);

                    unit.addNode(node);
                    nodeToInsertUnit.put(node, unit);
                } else {
                    tmp.setPos(sibling.getEndPos());
                    insertUnits.add(tmp);
                    nodeToInsertUnit.put(node, tmp);
                }
            } else {
                // first insertion in a group
                tmp.setPos(parent.getPos());
                insertUnits.add(tmp);
                nodeToInsertUnit.put(node, tmp);
            }
        }

        // now inserts are ready to be added
        for (InsertUnit unit : insertUnits) {
            patches.add(new InsertPatch(unit.getPos(), unit.getText()));
        }

        // filter deletes
        // same idea as with insert units
        List<DeleteUnit> deleteUnits = new ArrayList<>();
        Map<ITree, DeleteUnit> nodeToDeleteUnit = new HashMap<>();
        for (ITree node : deleteReducer.getRoots()) {
            ITree parent = node.getParent();

            DeleteUnit tmp = new DeleteUnit();
            tmp.addNode(node);

            // deleting tree root
            if (parent == null) {
                // FIXME: is it always zero?
                deleteUnits.add(tmp);
                nodeToDeleteUnit.put(node, tmp);
                continue;
            }

            int pos = parent.getChildPosition(node);
            // deletions are generated from left to right
            // so delete unit can be extended only from right side
            if (pos > 0) {
                ITree sibling = parent.getChild(pos - 1);

                // if left sibling is in delete unit, we can extend it
                if (nodeToDeleteUnit.containsKey(sibling)) {
                    DeleteUnit unit = nodeToDeleteUnit.get(sibling);

                    unit.addNode(node);
                    nodeToDeleteUnit.put(node, unit);
                } else {
                    deleteUnits.add(tmp);
                    nodeToDeleteUnit.put(node, tmp);
                }
            } else {
                // first deletion in a group
                deleteUnits.add(tmp);
                nodeToDeleteUnit.put(node, tmp);
            }
        }

        // now inserts are ready to be added
        for (DeleteUnit unit : deleteUnits) {
            patches.add(new DeletePatch(unit.getStart(), unit.getStop()));
        }

        return patches;
    }
}
