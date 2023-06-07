package edu.uniandes.___data;

import edu.uniandes.annotations.core.Data;
import edu.uniandes.annotations.core.FK;
import edu.uniandes.annotations.core.PK;

import java.sql.Timestamp;

@Data("apartment_spec") class ApartmentSpec {
    @PK @FK(Operator.class) String name;
    Boolean includedServices;
    Boolean includedTV;
    Integer administrationFee;
}

@Data("hostel_spec") class HostelSpec {
    @PK @FK(Operator.class) String name;
    Long nit;
    Timestamp openingHours;
    Timestamp closingHours;
}

@Data("residence_spec") class ResidenceSpec {
    @PK @FK(Operator.class) String name;
    Integer bedroomNum;
    Integer administrationFee;
    String insuranceDesc;
}

@Data("student_res_spec") class StudentResSpec {
    @PK @FK(Operator.class) String name;
    Integer restaurant;
    Integer studyRoom;
    Integer recreationRoom;
    Integer gym;
}

@Data("hotel_room_spec") class HotelRoomSpec {
    @PK @FK(Operator.class) String name;
    Integer roomNumber;
    String roomType;
    Boolean bathtub;
    Boolean jacuzzi;
    Boolean livingRoom;
}

@Data("house_room_spec") class HouseRoomSpec {
    @PK @FK(Operator.class) String name;
    Boolean food;
    Boolean privateBathroom;
}

@Data("service_scheme") class ServiceScheme {
    @PK @FK(HouseRoomSpec.class) String name;
    String serviceName;
    Integer serviceCost;
}
