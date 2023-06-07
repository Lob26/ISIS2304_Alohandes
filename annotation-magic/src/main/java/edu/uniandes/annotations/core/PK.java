package edu.uniandes.annotations.core;

import java.lang.annotation.*;

@Documented @Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PK {
    /** Sequence name for generate the next ID */
    String sequence() default "";
}