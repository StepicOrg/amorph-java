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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.stepik.amorph.tree.ITree;

public class HashUtils {

    private HashUtils() {}

    public static final int BASE = 33;

    public static final HashGenerator DEFAULT_HASH_GENERATOR = new RollingHashGenerator.Md5RollingHashGenerator();

    public static int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
    }

    public static int standardHash(ITree t) {
        return t.getType().hashCode() + HashUtils.BASE * t.getValue().hashCode();
    }

    public static String inSeed(ITree t) {
        return ITree.OPEN_SYMBOL + t.getValue() + ITree.SEPARATE_SYMBOL + t.getType();
    }

    public static String outSeed(ITree t) {
        return  t.getType() + ITree.SEPARATE_SYMBOL + t.getValue() + ITree.CLOSE_SYMBOL;
    }

    public static int md5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(s.getBytes());
            return byteArrayToInt(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ITree.NO_FIELD_VALUE;
    }

    public static int fpow(int a, int b) {
        if (b == 1)
            return a;
        int result = 1;
        while (b > 0) {
            if ((b & 1) != 0)
                result *= a;
            b >>= 1;
            a *= a;
        }
        return result;
    }

}
