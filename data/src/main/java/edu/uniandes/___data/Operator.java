package edu.uniandes.___data;

import edu.uniandes.annotations.core.Data;
import edu.uniandes.annotations.core.FK;
import edu.uniandes.annotations.core.PK;

@Data("operator") class Operator {
    Long nuip;
    @PK String name;
    String type;
}

@Data("operator_service") class OperatorService {
    @PK @FK(Operator.class) String name;
    Boolean furnished;
    Boolean wifi;
    Boolean kitchenette;
}

@Data("operator_spec") class OperatorSpec {
    @PK @FK(Operator.class) String name;
    Integer capacity;
    Integer size;
    String location;
    Boolean shared;
}
