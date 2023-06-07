package edu.uniandes.___data;

import edu.uniandes.annotations.core.Data;
import edu.uniandes.annotations.core.FK;
import edu.uniandes.annotations.core.PK;

@Data("client") class Client {
    @PK Long nuip;
    String name;
    String clientType;
}

@Data("client_preference") class ClientPreference {
    @PK @FK(Client.class) Long nuip;
    Boolean furnished;
    Boolean shared;
    Boolean wifi;
    Boolean kitchenette;
}
