package edu.uniandes.annotations.core;

import java.lang.annotation.Repeatable;

@Repeatable(Queries.class)
public @interface Query {
    String k();
    String v();
}
