package pl.themolka.arcade.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo {
    String id();

    Class<? extends Module>[] dependency() default {};

    Class<? extends Module>[] loadBefore() default {};
}
