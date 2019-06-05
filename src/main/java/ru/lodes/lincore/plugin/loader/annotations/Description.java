package ru.lodes.lincore.plugin.loader.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {

    String name() default "";

    String version() default "1.0.0";

    String[] dependencies() default {};

    String[] softdependencies() default {};
}
