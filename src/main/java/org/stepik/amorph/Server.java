package org.stepik.amorph;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.stepik.amorph.actions.ActionGenerator;
import org.stepik.amorph.actions.model.Action;
import org.stepik.amorph.matchers.Mapping;
import org.stepik.amorph.matchers.Matcher;
import org.stepik.amorph.matchers.Matchers;
import org.stepik.amorph.parse.Python3Lexer;
import org.stepik.amorph.parse.Python3Parser;
import org.stepik.amorph.patches.PatchUtil;
import org.stepik.amorph.patches.model.DeletePatch;
import org.stepik.amorph.patches.model.InsertPatch;
import org.stepik.amorph.patches.model.Patch;
import org.stepik.amorph.patches.model.UpdatePatch;
import org.stepik.amorph.patches.model.serialize.DeletePatchSerializer;
import org.stepik.amorph.patches.model.serialize.InsertPatchSerializer;
import org.stepik.amorph.patches.model.serialize.UpdatePatchSerializer;
import org.stepik.amorph.tree.ITree;
import org.stepik.amorph.tree.Tree;
import org.stepik.amorph.tree.TreeUtils;
import spark.HaltException;
import spark.Request;
import spark.Spark;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.halt;
import static spark.Spark.post;

public class Server {
    private static Gson gson;

    static {
        gson = new GsonBuilder()
                .registerTypeAdapter(DeletePatch.class, new DeletePatchSerializer())
                .registerTypeAdapter(InsertPatch.class, new InsertPatchSerializer())
                .registerTypeAdapter(UpdatePatch.class, new UpdatePatchSerializer())
                .create();
    }

    public static String treeToASCII(ITree tree) {
        StringBuilder builder = new StringBuilder();

        List<ITree> firstStack = new ArrayList<>();
        firstStack.add(tree);

        List<List<ITree>> childListStack = new ArrayList<>();
        childListStack.add(firstStack);

        while (!childListStack.isEmpty()) {
            List<ITree> childStack = childListStack.get(childListStack.size() - 1);

            if (childStack.isEmpty()) {
                childListStack.remove(childListStack.size() - 1);
            }
            else {
                tree = childStack.remove(0);
                String caption;

                if (!"".equals(tree.getValue())) {
                    caption = "'" + tree.getValue() + "'";
                }
                else {
                    caption = tree.getType();
                }

                StringBuilder indent = new StringBuilder();
                for (int i = 0; i < childListStack.size() - 1; i++) {
                    indent.append((childListStack.get(i).size() > 0) ? "|  " : "   ");
                }

                builder.append(indent)
                        .append(childStack.isEmpty() ? "'- " : "|- ")
                        .append(caption)
                        .append("\n");

                if (tree.getChildren().size() > 0) {
                    List<ITree> children = new ArrayList<>();
                    children.addAll(tree.getChildren());
                    childListStack.add(children);
                }
            }
        }

        return builder.toString();
    }

    public static ITree treeFromCode(String param, Request request) throws HaltException {
        String code = request.queryParamOrDefault(param, "") + "\n";

        CharStream stream = CharStreams.fromString(code);

        Python3Lexer lexer = new Python3Lexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Python3Parser parser = new Python3Parser(tokens);
        parser.setErrorHandler(new BailErrorStrategy());

        ParseTree parseTree;
        try {
            parseTree = parser.file_input();
        } catch (ParseCancellationException e) {
            throw halt(400, MessageFormat.format("Invalid syntax for ''{0}''", param));
        }

        Tree tree = new Tree(parseTree, tokens);
        tree.refresh();
        TreeUtils.postOrderNumbering(tree);

        return tree;
    }

    public static void main(String[] args) {
        Spark.exception(Exception.class, (exception, request, response) -> {
            String src = request.queryParamOrDefault("src", "");
            String dst = request.queryParamOrDefault("dst", "");
            System.err.println(src);
            System.err.println(dst);
            exception.printStackTrace();
        });

        Spark.staticFileLocation("static");

        post("/api/diff", (request, response) -> {
            ITree srcTree = treeFromCode("src", request);
            ITree dstTree = treeFromCode("dst", request);

            Matcher m = Matchers.getInstance().getMatcher(srcTree, dstTree);
            m.match();

            ActionGenerator g = new ActionGenerator(srcTree, dstTree, m.getMappings());
            g.generate();

            List<Action> actions = g.getActions();
            List<Patch> patches = PatchUtil.patchesFromActions(actions);

            response.header("Access-Control-Allow-Origin", "*");
            response.type("application/json");
            return gson.toJson(patches);
        });
    }
}
