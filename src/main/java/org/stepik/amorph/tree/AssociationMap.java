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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

public class AssociationMap {
    // FIXME or not, should we inline this class ? or use Entry to only have one list ? ... or both
    ArrayList<Object> values = new ArrayList<>();
    ArrayList<String> keys = new ArrayList<>();

    public Object get(String key) {
        int idx = keys.indexOf(key);
        if (idx == -1)
            return null;
        return values.get(idx);
    }

    /**
     * set metadata `key` with `value` and returns the previous value
     * This method won't remove if value == null
     */
    public Object set(String key, Object value) {
        int idx = keys.indexOf(key);
        if (idx == -1) {
            keys.add(key);
            values.add(value);
            return null;
        }
        return values.set(idx, value);
    }

    public Object remove(String key) {
        int idx = keys.indexOf(key);
        if (idx == -1)
            return null;
        if (idx == keys.size() - 1) {
            keys.remove(idx);
            return values.remove(idx);
        }
        keys.set(idx, keys.remove(keys.size() - 1));
        return values.set(idx, values.remove(values.size() - 1));
    }

    public Iterator<Entry<String, Object>> iterator() {
        return new Iterator<Entry<String, Object>>() {
            int currentPos = 0;
            @Override
            public boolean hasNext() {
                return currentPos < keys.size();
            }

            @Override
            public Entry<String, Object> next() {
                Entry<String, Object> e = new AbstractMap.SimpleEntry<>(keys.get(currentPos), values.get(currentPos));
                currentPos++;
                return e;
            }
        };
    }
}