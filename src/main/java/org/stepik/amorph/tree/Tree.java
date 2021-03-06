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

package org.stepik.amorph.tree;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;


public class Tree extends AbstractTree implements ITree {
    private String type;
    private String value;

    // Begin position of the tree in terms of absolute character index and length
    private int pos;
    private int length;
    // End position

    private AssociationMap metadata;

    public Tree(ParseTree parseTree, TokenStream stream) {
        this(parseTree, stream, null);
    }

    public Tree(ParseTree parseTree, TokenStream stream, Tree parent) {
        generatePk();

        Interval interval = parseTree.getSourceInterval();

        int start = stream.get(interval.a).getStartIndex();
        int stop = stream.get(interval.b).getStopIndex();

        setText(stream.getText(interval));
        setPos(start);
        setLength(stop - start + 1);

        String ruleName = parseTree.getClass().getSimpleName().replace("Context", "");
        setType(Character.toLowerCase(ruleName.charAt(0)) + ruleName.substring(1));

        if (parseTree.getChildCount() == 0)
            setValue(((Token) parseTree.getPayload()).getText());
        else
            setValue("");

        setParent(parent);
        children = new ArrayList<>();

        for (int i = 0; i < parseTree.getChildCount(); i++) {
            ParseTree child = parseTree.getChild(i);
            ParseTree nextNode = getBranchOrLeaf(child);

            children.add(new Tree(nextNode, stream, this));
        }
    }

    private static ParseTree getBranchOrLeaf(ParseTree node) {
        if (node.getPayload() instanceof Token || node.getChildCount() != 1)
            return node;

        return getBranchOrLeaf(node.getChild(0));
    }

    /**
     * Constructs a new node. If you need type labels corresponding to the integer
     */
    public Tree(String pk, String type, String value) {
        this.pk = pk;
        this.type = type;
        this.value = (value == null) ? NO_VALUE : value;
        this.text = "";
        this.id = NO_ID;
        this.depth = NO_FIELD_VALUE;
        this.hash = NO_FIELD_VALUE;
        this.height = NO_FIELD_VALUE;
        this.depth = NO_FIELD_VALUE;
        this.size = NO_FIELD_VALUE;
        this.pos = NO_FIELD_VALUE;
        this.length = NO_FIELD_VALUE;
        this.children = new ArrayList<>();
    }

    // Only used for cloning ...
    private Tree(Tree other) {
        this.pk = other.getPk();
        this.type = other.getType();
        this.value = other.getValue();
        this.text = other.getText();
        this.id = other.getId();
        this.pos = other.getPos();
        this.length = other.getLength();
        this.height = other.getHeight();
        this.parent = other.getParent();
        this.size = other.getSize();
        this.depth = other.getDepth();
        this.hash = other.getHash();
        this.depth = other.getDepth();
        this.children = new ArrayList<>();
        this.metadata = other.metadata;
    }

    @Override
    public int hashCode() {
        return pk.hashCode();
    }

    @Override
    public void addChild(ITree t) {
        children.add(t);
        t.setParent(this);
    }

    @Override
    public void insertChild(ITree t, int position) {
        children.add(position, t);
        t.setParent(this);
    }

    @Override
    public Tree copy() {
        return new Tree(this);
    }

    @Override
    public Tree deepCopy() {
        Tree copy = this.copy();
        for (ITree child : getChildren())
            copy.addChild(child.deepCopy());
        return copy;
    }

    @Override
    public List<ITree> getChildren() {
        return children;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public ITree getParent() {
        return parent;
    }

    @Override
    public int getPos() {
        return pos;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setChildren(List<ITree> children) {
        this.children = children;
        for (ITree c : children)
            c.setParent(this);
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public void setParent(ITree parent) {
        this.parent = parent;
    }

    @Override
    public void setParentAndUpdateChildren(ITree parent) {
        if (this.parent != null)
            this.parent.getChildren().remove(this);
        this.parent = parent;
        if (this.parent != null)
            parent.getChildren().add(this);
    }

    @Override
    public void setPos(int pos) {
        this.pos = pos;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Object getMetadata(String key) {
        if (metadata == null)
            return null;
        return metadata.get(key);
    }

    @Override
    public Object setMetadata(String key, Object value) {
        if (value == null) {
            if (metadata == null)
                return null;
            else
                return metadata.remove(key);
        }
        if (metadata == null)
            metadata = new AssociationMap();
        return metadata.set(key, value);
    }

    @Override
    public Iterator<Entry<String, Object>> getMetadata() {
        if (metadata == null)
            return new EmptyEntryIterator();
        return metadata.iterator();
    }
}
