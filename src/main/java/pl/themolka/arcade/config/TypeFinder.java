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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TypeFinder<T> {
    private final Class<T> type;

    public TypeFinder(Class<T> type) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
    }

    public List<T> find(Object source) {
        if (this.isBlacklisted(source.getClass())) {
            return Collections.emptyList();
        } else if (Iterable.class.isAssignableFrom(source.getClass())) {
            return this.find((Iterable<?>) source);
        }

        List<T> results = new ArrayList<>();
        for (Method method : source.getClass().getMethods()) {
            int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers) && !Modifier.isStatic(modifiers)) {
                results.addAll(this.find(source, method));
            }
        }

        return results;
    }

    public List<T> find(Object... sources) {
        return this.find(Arrays.asList(sources));
    }

    public List<T> find(Iterable<?> sources) {
        List<T> results = new ArrayList<>();
        for (Object source : sources) {
            results.addAll(this.find(source));
        }

        return results;
    }

    public List<T> find(Object source, Method method) {
        if (this.isBlacklisted(source.getClass()) || ArrayUtils.contains(Object.class.getMethods(), method)) {
            // Never scan java.lang.Object
            return Collections.emptyList();
        }

        int modifiers = method.getModifiers();
        if (Modifier.isAbstract(modifiers) || Modifier.isStatic(modifiers)) {
            return Collections.emptyList();
        } else if (method.getParameterCount() != 0) {
            return Collections.emptyList();
        }

        return this.find0(source, method);
    }

    private List<T> find0(Object source, Method method) {
        List<T> results = new ArrayList<>();
        Class<?> returnType = method.getReturnType();
        try {
            method.setAccessible(true);

            if (this.type.isAssignableFrom(returnType)) {
                results.add((T) method.invoke(source));
            }

            if (Iterable.class.isAssignableFrom(returnType)) {
                results.addAll(this.find((Iterable<T>) method.invoke(source)));
            } else if (Ref.class.isAssignableFrom(returnType) || IConfig.class.isAssignableFrom(returnType)) {
                Object result = method.invoke(source);
                if (result != null) {
                    results.addAll(this.find(result));
                }
            }
        } catch (InvocationTargetException ignored) {
        } catch (IllegalAccessException shouldNeverHappen) {
            throw new Error(shouldNeverHappen);
        }

        return results;
    }

    private List<T> findForType(T type) {
        return type != null ? Collections.singletonList(type)
                            : Collections.emptyList();
    }

    private boolean isBlacklisted(Class<?> clazz) {
        return ClassUtils.isPrimitiveOrWrapper(clazz) ||
                clazz.equals(Object.class) ||
                clazz.equals(String.class) ||
                clazz.equals(Optional.class) ||
                Enum.class.isAssignableFrom(clazz);
    }
}
