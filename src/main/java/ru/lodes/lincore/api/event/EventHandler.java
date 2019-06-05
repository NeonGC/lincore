package ru.lodes.lincore.api.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface EventHandler {

    EventPriority priority() default EventPriority.NORMAL;

    boolean ignoreCancelled() default false;
}
