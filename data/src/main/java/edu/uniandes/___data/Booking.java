package edu.uniandes.___data;

import edu.uniandes.annotations.core.Data;
import edu.uniandes.annotations.core.FK;
import edu.uniandes.annotations.core.PK;
import edu.uniandes.annotations.core.Query;

import java.util.Date;

@Data("host_offer")
@Query(k = "REQ04", v = "UPDATE host_offer SET is_taken = 1 WHERE id = ?")//REQ04
@Query(k = "REQ05", v = "UPDATE host_offer SET is_taken = 0 WHERE id = ?")//REQ05
@Query(k = "REQ06", v = "UPDATE host_offer SET active = 0 WHERE id = ?")//REQ09
@Query(k = "REQ07", v = "UPDATE host_offer SET active = 1 WHERE id = ?")//REQ10
class HostOffer {//Previously Booking
    @PK(sequence = "host_offer_id_seq") Long id;
    @FK(Client.class) Long nuip;
    @FK(Operator.class) String name;
    Long cost;
    Date start;
    Date end;
    Boolean active;
    Boolean isTaken;
}

@Data("group_reservation")
@Query(k = "REQ0N", v = "SELECT * FROM group_reservation WHERE id = ?")
class GroupReservation {
    @PK(sequence = "group_reservation_id_seq") Long id;
    @PK @FK(HostOffer.class) Long reservationId;
}