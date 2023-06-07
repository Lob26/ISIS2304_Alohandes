package edu.uniandes.annotations.core;

import java.lang.annotation.*;

@Documented @Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface FK {
    /** What class does it refer to */
    Class<?> value();
}
