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

package org.stepik.amorph.tree.hash;

import java.util.HashMap;
import java.util.Map;

import org.stepik.amorph.tree.ITree;

public abstract class RollingHashGenerator implements HashGenerator {

    public void hash(ITree t) {
        for (ITree n: t.postOrder())
            if (n.isLeaf())
                n.setHash(leafHash(n));
            else
                n.setHash(innerNodeHash(n));
    }

    public abstract int hashFunction(String s);

    public int leafHash(ITree t) {
        return HashUtils.BASE * hashFunction(HashUtils.inSeed(t)) + hashFunction(HashUtils.outSeed(t));
    }

    public int innerNodeHash(ITree t) {
        int size = t.getSize() * 2 - 1;
        int hash = hashFunction(HashUtils.inSeed(t)) * HashUtils.fpow(HashUtils.BASE, size);

        for (ITree c: t.getChildren()) {
            size = size - c.getSize() * 2;
            hash += c.getHash() * HashUtils.fpow(HashUtils.BASE, size);
        }

        hash += hashFunction(HashUtils.outSeed(t));
        return hash;
    }

    public static class JavaRollingHashGenerator extends RollingHashGenerator {

        @Override
        public int hashFunction(String s) {
            return s.hashCode();
        }

    }

    public static class Md5RollingHashGenerator extends RollingHashGenerator {

        @Override
        public int hashFunction(String s) {
            return HashUtils.md5(s);
        }

    }

    public static class RandomRollingHashGenerator extends RollingHashGenerator {

        private static final Map<String, Integer> digests = new HashMap<>();

        @Override
        public int hashFunction(String s) {
            return rdmHash(s);
        }

        public static int rdmHash(String s) {
            if (!digests.containsKey(s)) {
                int digest = (int) (Math.random() * (Integer.MAX_VALUE - 1));
                digests.put(s, digest);
                return digest;
            } else return digests.get(s);
        }

    }

}
