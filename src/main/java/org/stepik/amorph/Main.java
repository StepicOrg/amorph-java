package org.stepik.amorph;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.stepik.amorph.actions.ActionGenerator;
import org.stepik.amorph.actions.model.*;
import org.stepik.amorph.actions.model.serialize.DeleteSerializer;
import org.stepik.amorph.actions.model.serialize.InsertSerializer;
import org.stepik.amorph.actions.model.serialize.MoveSerializer;
import org.stepik.amorph.actions.model.serialize.UpdateSerializer;
import org.stepik.amorph.matchers.Matcher;
import org.stepik.amorph.matchers.Matchers;
import org.stepik.amorph.tree.ASTNode;
import org.stepik.amorph.tree.ITree;
import org.stepik.amorph.tree.TreeUtils;
import spark.HaltException;
import spark.Request;
import spark.Spark;

import java.text.MessageFormat;
import java.util.List;

import static spark.Spark.halt;
import static spark.Spark.post;

public class Main {
    private static Gson gson;

    static {
        gson = new GsonBuilder()
                .registerTypeAdapter(Delete.class, new DeleteSerializer())
                .registerTypeAdapter(Insert.class, new InsertSerializer())
                .registerTypeAdapter(Move.class, new MoveSerializer())
                .registerTypeAdapter(Update.class, new UpdateSerializer())
                .create();
    }

    public static ITree treeFromJsonOrHalt(String param, Request request) throws HaltException {
        String json = request.queryParamOrDefault(param, "");

        if ("".equals(json))
            throw halt(400, MessageFormat.format("Empty JSON passed for ''{0}''", param));

        ASTNode astRoot;
        try {
            astRoot = gson.fromJson(json, ASTNode.class);
        } catch (JsonSyntaxException e) {
            throw halt(400, MessageFormat.format("Invalid JSON passed for ''{0}''", param));
        }

        ITree treeRoot;
        try {
            treeRoot = astRoot.toTree();
        } catch (IllegalStateException e) {
            throw halt(400, MessageFormat.format("Invalid AST structure passed for ''{0}''", param));
        }

        treeRoot.refresh();
        TreeUtils.postOrderNumbering(treeRoot);
        return treeRoot;
    }

    public static void main(String[] args) {
        Spark.exception(Exception.class, (exception, request, response) -> {
            String srcJson = request.queryParamOrDefault("src", "");
            String dstJson = request.queryParamOrDefault("dst", "");
            System.err.println(srcJson);
            System.err.println(dstJson);
            exception.printStackTrace();
        });

        post("/api/diff", (request, response) -> {
            ITree src = treeFromJsonOrHalt("src", request);
            ITree dst = treeFromJsonOrHalt("dst", request);

            Matcher m = Matchers.getInstance().getMatcher(src, dst);
            m.match();
            ActionGenerator g = new ActionGenerator(src, dst, m.getMappings());
            g.generate();
            List<Action> actions = g.getActions();

            response.type("application/json");
            return gson.toJson(actions);
        });
    }
}
