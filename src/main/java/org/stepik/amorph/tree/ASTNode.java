//package org.stepik.amorph.tree;
//
//import com.google.gson.annotations.SerializedName;
//
//import java.util.List;
//import java.util.Map;
//
//public class ASTNode {
//    String pk;
//    @SerializedName("ast_type") String astType;
//    Map<String, Object> props;
//    List<ASTNode> children;
//
//    boolean isValid() {
//        return pk != null && !"".equals(pk) &&
//                astType != null && !"".equals(astType) &&
//                props != null && children != null;
//    }
//
//    public ITree toTree() {
//        if (!isValid())
//            throw new IllegalStateException("Invalid ASTNode");
//
//        Tree node = new Tree(pk, astType, props);
//        for (ASTNode child : children) {
//            node.addChild(child.toTree());
//        }
//        return node;
//    }
//}
