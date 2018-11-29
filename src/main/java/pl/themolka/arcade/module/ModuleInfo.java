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

package pl.themolka.arcade.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation describes a module. Modules registered in the
 * <code>modules.xml</code> files <b>should</b> be decorated by
 * this module.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo {
    /**
     * A unique ID of this module in the <b>lower case</b>. ID
     * should only contain followed characters: <code>A-Z</code>,
     * <code>a-z</code>, <code>0-9</code>, <code>_</code> and
     * <code>-</code>. Use <code>-</code> separate words.
     * Note that this ID will mostly be parsed by the XML.
     * @return unique ID of this module
     */
    String id();

    /**
     * Modules which this module depends on.
     * @return depending modules.
     */
    Class<? extends Module<?>>[] dependency() default {};

    /**
     * Modules which should be enabled before this module.
     * @return modules enabled before this module.
     */
    Class<? extends Module<?>>[] loadBefore() default {};
}
