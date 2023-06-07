package edu.uniandes.___data;

import edu.uniandes.annotations.core.Data;
import edu.uniandes.annotations.core.FK;
import edu.uniandes.annotations.core.PK;

@Data("hotel") class Hotel {
    @PK Integer nit;
    Boolean restaurant;
    Boolean pool;
    Boolean parkingLot;
    Boolean wifi;
    Boolean cableTV;
}

@Data("rooms_hotel") class RoomsHotel {
    @PK @FK(Hotel.class) Integer nit;
    @PK @FK(HotelRoomSpec.class) String name;
}