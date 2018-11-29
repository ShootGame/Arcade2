/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RefFinder {
    public List<Ref<?>> find(Object source) throws InvocationTargetException {
        if (source instanceof Ref<?>) {
            return Collections.singletonList((Ref<?>) source);
        }

        List<Ref<?>> results = new ArrayList<>();
        for (Method method : source.getClass().getMethods()) {
            int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers) && !Modifier.isStatic(modifiers)) {
                results.addAll(this.find(source, method));
            }
        }

        return results;
    }

    public List<Ref<?>> find(Object... sources) throws InvocationTargetException {
        return this.find(Arrays.asList(sources));
    }

    public List<Ref<?>> find(Iterable<?> sources) throws InvocationTargetException {
        List<Ref<?>> results = new ArrayList<>();
        for (Object source : sources) {
            results.addAll(this.find(source));
        }

        return results;
    }

    public List<Ref<?>> find(Object source, Method method) throws InvocationTargetException {
        int modifiers = method.getModifiers();
        if (Modifier.isAbstract(modifiers) || Modifier.isStatic(modifiers)) {
            return Collections.emptyList();
        } else if (method.getParameterCount() != 0) {
            return Collections.emptyList();
        }

        return this.find0(source, method);
    }

    private List<Ref<?>> find0(Object source, Method method) throws InvocationTargetException {
        Class<?> returnType = method.getReturnType();
        try {
            method.setAccessible(true);
            if (Ref.class.isAssignableFrom(returnType)) {
                return this.findForRef((Ref<?>) method.invoke(source));
            } else if (Ref[].class.isAssignableFrom(returnType)) {
                return this.findForRefArray((Ref<?>[]) method.invoke(source));
            } else if (Iterable.class.isAssignableFrom(returnType)) {
                return this.findForIterable((Iterable<?>) method.invoke(source));
            }
        } catch (IllegalAccessException shouldNeverHappen) {
            throw new Error(shouldNeverHappen);
        }

        return Collections.emptyList();
    }

    private List<Ref<?>> findForRef(Ref<?> ref) {
        if (ref != null) {
            return Collections.singletonList(ref);
        }

        return Collections.emptyList();
    }

    private List<Ref<?>> findForRefArray(Ref<?>[] refArray) {
        if (refArray != null) {
            return Arrays.asList(refArray);
        }

        return Collections.emptyList();
    }

    private List<Ref<?>> findForIterable(Iterable<?> iterable) throws InvocationTargetException {
        if (iterable != null) {
            List<Ref<?>> results = new ArrayList<>();
            for (Object object : iterable) {
                results.addAll(this.find(object));
            }

            return results;
        }

        return Collections.emptyList();
    }
}
