//package org.stepik.amorph;
//
//import org.antlr.v4.runtime.*;
//import org.antlr.v4.runtime.misc.Interval;
//import org.antlr.v4.runtime.tree.ParseTree;
//import org.stepik.amorph.actions.ActionGenerator;
//import org.stepik.amorph.actions.ActionUtil;
//import org.stepik.amorph.actions.InsertUnit;
//import org.stepik.amorph.actions.model.*;
//import org.stepik.amorph.matchers.Matcher;
//import org.stepik.amorph.matchers.Matchers;
//import org.stepik.amorph.parse.Python3Lexer;
//import org.stepik.amorph.parse.Python3Parser;
//import org.stepik.amorph.tree.ITree;
//import org.stepik.amorph.tree.Tree;
//import org.stepik.amorph.tree.TreeUtils;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.*;
//
//public class Main {
//    public static String treeToASCII(ParseTree tree) {
//        StringBuilder builder = new StringBuilder();
//
//        List<ParseTree> firstStack = new ArrayList<>();
//        firstStack.add(tree);
//
//        List<List<ParseTree>> childListStack = new ArrayList<>();
//        childListStack.add(firstStack);
//
//        while (!childListStack.isEmpty()) {
//            List<ParseTree> childStack = childListStack.get(childListStack.size() - 1);
//
//            if (childStack.isEmpty()) {
//                childListStack.remove(childListStack.size() - 1);
//            }
//            else {
//                tree = childStack.remove(0);
//                String caption;
//
//                if (tree.getPayload() instanceof Token) {
//                    Token token = (Token) tree.getPayload();
//                    caption = String.format("TOKEN[type: %s, text: %s]",
//                            token.getType(), token.getText().replace("\n", "\\n"));
//                }
//                else {
//                    String ruleName = tree.getClass().getSimpleName().replace("Context", "");
//                    caption = Character.toLowerCase(ruleName.charAt(0)) + ruleName.substring(1);
//                }
//
//                StringBuilder indent = new StringBuilder();
//                for (int i = 0; i < childListStack.size() - 1; i++) {
//                    indent.append((childListStack.get(i).size() > 0) ? "|  " : "   ");
//                }
//
//                builder.append(indent)
//                        .append(childStack.isEmpty() ? "'- " : "|- ")
//                        .append(caption)
//                        .append("\n");
//
//                if (tree.getChildCount() > 0) {
//                    List<ParseTree> children = new ArrayList<>();
//                    for (int i = 0; i < tree.getChildCount(); i++)
//                        children.add(tree.getChild(i));
//                    childListStack.add(children);
//                }
//            }
//        }
//
//        return builder.toString();
//    }
//
//    public static ITree treeFromCode(String input) {
//        //System.err.println(input.replace("\n", "\\n"));
//        CharStream stream = CharStreams.fromString(input);
//
//        Python3Lexer lexer = new Python3Lexer(stream);
//        CommonTokenStream tokens = new CommonTokenStream(lexer);
//        Python3Parser parser = new Python3Parser(tokens);
//        parser.setErrorHandler(new BailErrorStrategy());
//
//        ParseTree parseTree = parser.file_input();
//
//        Tree tree = new Tree(parseTree, tokens);
//        tree.refresh();
//        TreeUtils.postOrderNumbering(tree);
//
//        //System.err.println(treeToASCII(parseTree));
//        //System.err.println(tokens.getTokens());
//
//        return tree;
//    }
//
//    public static String readTestCase(String filename) throws IOException {
//        File file = new File(filename);
//        FileInputStream fis = new FileInputStream(file);
//        byte[] data = new byte[(int) file.length()];
//        fis.read(data);
//        fis.close();
//
//        return new String(data, "UTF-8") + '\n';
//    }
//
//    public static void main(String[] args) throws IOException {
//        String src = readTestCase("samples/src.py");
//        String dst = readTestCase("samples/dst.py");
//
//        ITree srcTree = treeFromCode(src);
//        ITree dstTree = treeFromCode(dst);
//
////        System.out.println(srcTree);
////        System.out.println(dstTree);
//
//        Matcher matcher = Matchers.getInstance().getMatcher(srcTree, dstTree);
//        matcher.match();
//        ActionGenerator generator = new ActionGenerator(srcTree, dstTree, matcher.getMappings());
//        generator.generate();
//
//        Set<String> insertNodes = new HashSet<>();
//        List<String> insertRoots = new ArrayList<>();
//        LinkedHashSet<String> deleteNodes = new LinkedHashSet<>();
//        for (Action action : generator.getActions()) {
//            ITree node = action.getNode();
//
//            if (action instanceof UpdatePatch) {
//                System.out.printf(
//                        "UpdatePatch '%s' value in range [%d, %d) to '%s'%n",
//                        node.getText(),
//                        node.getPos(),
//                        node.getEndPos(),
//                        ((UpdatePatch) action).getValue()
//                );
//            }
//
//            if (action instanceof DeletePatch) {
//                deleteNodes.add(node.getPk());
//            }
//
//            if (action instanceof MoveAction) {
//                MoveAction move = (MoveAction) action;
//                ITree parent = move.getParent();
//
//                deleteNodes.add(node.getPk());
//
//                // need to create node copy because pk must be unique
//                node = node.deepCopy();
//                node.generatePk();
//                InsertPatch insert = new InsertPatch(node, parent, move.getPosition());
//
//                insertNodes.add(node.getPk());
//                if (!insertNodes.contains(parent.getPk()))
//                    insertRoots.add(node.getPk());
//
//                ActionUtil.apply(insert);
//            }
//
//            if (action instanceof InsertPatch) {
//                InsertPatch insert = (InsertPatch) action;
//                ITree parent = insert.getParent();
//
//                insertNodes.add(node.getPk());
//                if (!insertNodes.contains(parent.getPk()))
//                    insertRoots.add(node.getPk());
//
//                ActionUtil.apply(insert);
//            }
//        }
//
//        Map<String, ITree> srcFlatten = TreeUtils.flattenTree(srcTree);
//
//        // Process inserts
//        List<InsertUnit> units = new ArrayList<>();
//        Map<String, InsertUnit> pkToUnit = new HashMap<>();
//        for (String nodePk : insertRoots) {
//            ITree node = srcFlatten.get(nodePk);
//            ITree parent = node.getParent();
//
//            InsertUnit tmp = new InsertUnit();
//            tmp.getNodes().add(node);
//
//            // inserting tree root
//            if (parent == null) {
//                // FIXME: is it always zero?
//                tmp.setPos(0);
//                units.add(tmp);
//                pkToUnit.put(nodePk, tmp);
//                continue;
//            }
//
//            int pos = parent.getChildPosition(node);
//            if (pos > 0) {
//                ITree sibling = parent.getChild(pos - 1);
//
//                if (pkToUnit.containsKey(sibling.getPk())) {
//                    InsertUnit unit = pkToUnit.get(sibling.getPk());
//
//                    unit.getNodes().add(node);
//                    pkToUnit.put(node.getPk(), unit);
//                } else {
//                    tmp.setPos(sibling.getEndPos());
//                    units.add(tmp);
//                    pkToUnit.put(nodePk, tmp);
//                }
//            } else {
//                tmp.setPos(parent.getPos());
//                units.add(tmp);
//                pkToUnit.put(nodePk, tmp);
//            }
//        }
//
//        for (InsertUnit unit : units) {
//            System.out.printf("InsertPatch '%s' to pos %d%n", unit.getText(), unit.getPos());
//        }
//
//        // Process deletes
//        Set<String> redundantDeletes = new HashSet<>();
//        for (String nodePk : deleteNodes) {
//            ITree node = srcFlatten.get(nodePk);
//
//            for (ITree child : node.getChildren()) {
//                if (deleteNodes.contains(child.getPk())) {
//                    redundantDeletes.add(child.getPk());
//                }
//            }
//        }
//
//        for (String nodePk : deleteNodes) {
//            if (redundantDeletes.contains(nodePk))
//                continue;
//
//            ITree node = srcFlatten.get(nodePk);
//            System.out.printf("DeletePatch chars in range [%d, %d) ('%s')%n", node.getPos(), node.getEndPos(), node.getText());
//        }
//    }
//}
