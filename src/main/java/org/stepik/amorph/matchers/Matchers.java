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

package org.stepik.amorph.matchers;

import org.stepik.amorph.gen.Registry;
import org.stepik.amorph.tree.ITree;

public class Matchers extends Registry<String, Matcher, Register> {

    private static Matchers registry;
    private Factory<? extends Matcher> defaultMatcherFactory; // FIXME shouln't be removed and use priority instead ?

    public static Matchers getInstance() {
        if (registry == null)
            registry = new Matchers();
        return registry;
    }

    private Matchers() {
        install(CompositeMatchers.ClassicGumtree.class);
        install(CompositeMatchers.ChangeDistiller.class);
        install(CompositeMatchers.XyMatcher.class);
    }

    private void install(Class<? extends Matcher> clazz) {
        Register a = clazz.getAnnotation(Register.class);
        if (a == null)
            throw new RuntimeException("Expecting @Register annotation on " + clazz.getName());
        if (defaultMatcherFactory == null && a.defaultMatcher())
            defaultMatcherFactory = defaultFactory(clazz, ITree.class, ITree.class, MappingStore.class);
        install(clazz, a);
    }

    public Matcher getMatcher(String id, ITree src, ITree dst) {
        return get(id, src, dst, new MappingStore());
    }

    public Matcher getMatcher(ITree src, ITree dst) {
        return defaultMatcherFactory.instantiate(new Object[]{src, dst, new MappingStore()});
    }

    protected String getName(Register annotation, Class<? extends Matcher> clazz) {
        return annotation.id();
    }

    @Override
    protected Entry newEntry(Class<? extends Matcher> clazz, Register annotation) {
        return new Entry(annotation.id(), clazz,
                defaultFactory(clazz, ITree.class, ITree.class, MappingStore.class), annotation.priority()) {

            @Override
            protected boolean handle(String key) {
                return annotation.id().equals(key); // Fixme remove
            }
        };
    }
}
