package edu.uniandes.annotations.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface Data {
    /** Table name for the class entity */
    String value();
}
