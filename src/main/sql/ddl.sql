CREATE SEQUENCE host_offer_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE group_reservation_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE operator
(
    nuip NUMBER(19)    NOT NULL,
    name VARCHAR2(255) NOT NULL,
    type VARCHAR2(255) NOT NULL,
    CONSTRAINT pk_operator PRIMARY KEY (name)
);

ALTER TABLE operator
    ADD CONSTRAINT chk_operator_type CHECK ( type IN ('Residencia estudiantil', 'Residencia no-estudiantil', 'Hostal',
                                                      'Habitacion de Hotel', 'Habitacion de casa',
                                                      'Apartamento') );

CREATE TABLE client
(
    nuip        NUMBER(19)    NOT NULL,
    name        VARCHAR2(255) NOT NULL,
    client_type VARCHAR2(255) NOT NULL,
    CONSTRAINT pk_client PRIMARY KEY (nuip)
);

ALTER TABLE client
    ADD CONSTRAINT check_client_type CHECK ( client_type IN
                                             ('Estudiante', 'Estudiante Graduado', 'Empleado', 'Profesor',
                                              'Familiar de estudiante',
                                              'Profesor invitado', 'Invitado a evento') );

CREATE TABLE client_preference
(
    nuip        NUMBER(19)          NOT NULL,
    furnished   NUMBER(1) DEFAULT 1 NOT NULL,
    shared      NUMBER(1) DEFAULT 1 NOT NULL,
    wifi        NUMBER(1) DEFAULT 1 NOT NULL,
    kitchenette NUMBER(1) DEFAULT 1 NOT NULL,
    CONSTRAINT pk_client_preference PRIMARY KEY (nuip),
    CONSTRAINT fk_client_preference_client FOREIGN KEY (nuip)
        REFERENCES client (nuip)
);

CREATE TABLE apartment_spec
(
    name               VARCHAR2(255)       NOT NULL,
    included_services  NUMBER(1) DEFAULT 1 NOT NULL,
    included_tv        NUMBER(1) DEFAULT 1 NOT NULL,
    administration_fee NUMBER(10)          NOT NULL,
    CONSTRAINT pk_apartment_spec PRIMARY KEY (name),
    CONSTRAINT fk_apartment_spec_operator FOREIGN KEY (name)
        REFERENCES operator (name)
);

CREATE TABLE hostel_spec
(
    name          VARCHAR2(255)                                            NOT NULL,
    nit           NUMBER(19)                                               NOT NULL,
    opening_hours TIMESTAMP DEFAULT TO_TIMESTAMP('05:00:00', 'HH24:MI:SS') NOT NULL,
    closing_hours TIMESTAMP DEFAULT TO_TIMESTAMP('23:00:00', 'HH24:MI:SS') NOT NULL,
    CONSTRAINT pk_hostel_spec PRIMARY KEY (name),
    CONSTRAINT fk_hostel_spec_operator FOREIGN KEY (name)
        REFERENCES operator (name)
);

CREATE TABLE hotel
(
    nit         NUMBER(10)          NOT NULL,
    restaurant  NUMBER(1) DEFAULT 1 NOT NULL,
    pool        NUMBER(1) DEFAULT 1 NOT NULL,
    parking_lot NUMBER(1) DEFAULT 1 NOT NULL,
    wifi        NUMBER(1) DEFAULT 1 NOT NULL,
    cable_tv    NUMBER(1) DEFAULT 1 NOT NULL,
    CONSTRAINT pk_hotel PRIMARY KEY (nit)
);

CREATE TABLE hotel_room_spec
(
    name        VARCHAR2(255)       NOT NULL,
    room_number NUMBER(10)          NOT NULL,
    room_type   VARCHAR2(255)       NOT NULL,
    bathtub     NUMBER(1) DEFAULT 1 NOT NULL,
    jacuzzi     NUMBER(1) DEFAULT 1 NOT NULL,
    living_room NUMBER(1) DEFAULT 1 NOT NULL,
    hotel       NUMBER(10)          NOT NULL,
    CONSTRAINT pk_hotel_room_spec PRIMARY KEY (name),
    CONSTRAINT fk_hotel_room_spec_operator FOREIGN KEY (name)
        REFERENCES operator (name),
    CONSTRAINT fk_hotel_room_spec_hotel FOREIGN KEY (hotel)
        REFERENCES hotel (nit)
);

ALTER TABLE hotel_room_spec
    ADD CONSTRAINT chk_hotel_room_spec_type CHECK ( room_type IN ('Estandar', 'Semi-suite', 'Suite') );

CREATE TABLE house_room_spec
(
    name             VARCHAR2(255)       NOT NULL,
    food             NUMBER(1) DEFAULT 1 NOT NULL,
    private_bathroom NUMBER(1) DEFAULT 1 NOT NULL,
    CONSTRAINT pk_house_room_spec PRIMARY KEY (name),
    CONSTRAINT fk_house_room_spec_operator FOREIGN KEY (name)
        REFERENCES operator (name)
);

CREATE TABLE operator_service
(
    name        VARCHAR2(255)       NOT NULL,
    furnished   NUMBER(1) DEFAULT 1 NOT NULL,
    wifi        NUMBER(1) DEFAULT 1 NOT NULL,
    kitchenette NUMBER(1) DEFAULT 1 NOT NULL,
    CONSTRAINT pk_operator_service PRIMARY KEY (name),
    CONSTRAINT fk_operator_service_operator FOREIGN KEY (name)
        REFERENCES operator (name)
);

CREATE TABLE operator_spec
(
    name     VARCHAR2(255)       NOT NULL,
    capacity NUMBER(10)          NOT NULL,
    "size"   NUMBER(10)          NOT NULL,
    location VARCHAR2(255)       NOT NULL,
    shared   NUMBER(1) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_operator_spec PRIMARY KEY (name),
    CONSTRAINT fk_operator_spec_operator FOREIGN KEY (name)
        REFERENCES operator (name)
);

CREATE TABLE host_offer
(
    id       NUMBER(19) DEFAULT host_offer_id_seq.nextval NOT NULL,
    nuip     NUMBER(19)                                   NOT NULL,
    name     VARCHAR2(255)                                NOT NULL,
    cost     NUMBER(19)                                   NOT NULL,
    "start"  DATE       DEFAULT SYSDATE                   NOT NULL,
    "end"    DATE       DEFAULT SYSDATE + 365             NOT NULL,
    active   NUMBER(1)  DEFAULT 1                         NOT NULL,
    is_taken NUMBER(1)  DEFAULT 0                         NOT NULL,
    CONSTRAINT host_offer_pk PRIMARY KEY (id),
    CONSTRAINT host_offer_client_fk FOREIGN KEY (nuip)
        REFERENCES client (nuip),
    CONSTRAINT host_offer_operator_fk FOREIGN KEY (name)
        REFERENCES operator (name)
);

CREATE TABLE group_reservation
(
    group_id       NUMBER(19) DEFAULT group_reservation_id_seq.nextval NOT NULL,
    reservation_id NUMBER(10)                                          NOT NULL,
    CONSTRAINT pk_group_reservation PRIMARY KEY (group_id, reservation_id),
    CONSTRAINT fk_group_reservation_reservation FOREIGN KEY (reservation_id)
        REFERENCES host_offer (id)
);

CREATE TABLE residence_spec
(
    name               VARCHAR2(255) NOT NULL,
    bedroom_num        NUMBER(10)    NOT NULL,
    administration_fee NUMBER(19)    NOT NULL,
    insurance_desc     VARCHAR2(255) NOT NULL,
    CONSTRAINT residence_spec_pk PRIMARY KEY (name),
    CONSTRAINT residence_spec_operator_fk FOREIGN KEY (name)
        REFERENCES operator (name)
);

CREATE TABLE rooms_hotel
(
    nit  NUMBER(10)    NOT NULL,
    name VARCHAR2(255) NOT NULL,
    CONSTRAINT pk_rooms_hotel PRIMARY KEY (nit,
                                           name),
    CONSTRAINT fk_rooms_hotel_hotel FOREIGN KEY (nit)
        REFERENCES hotel (nit),
    CONSTRAINT fk_rooms_hotel_hotel_room_spec FOREIGN KEY (name)
        REFERENCES hotel_room_spec (name)
);

CREATE TABLE service_scheme
(
    name         VARCHAR2(255) NOT NULL,
    service_name VARCHAR2(255) NOT NULL,
    service_cost NUMBER(19)    NOT NULL,
    CONSTRAINT pk_service_scheme PRIMARY KEY (name, service_name),
    CONSTRAINT fk_service_scheme_house_room_spec FOREIGN KEY (name)
        REFERENCES house_room_spec (name)
);

CREATE TABLE student_res_spec
(
    name            VARCHAR2(255)       NOT NULL,
    restaurant      NUMBER(1) DEFAULT 1 NOT NULL,
    study_room      NUMBER(1) DEFAULT 1 NOT NULL,
    recreation_room NUMBER(1) DEFAULT 1 NOT NULL,
    gym             NUMBER(1) DEFAULT 1 NOT NULL,
    CONSTRAINT pk_student_res_spec PRIMARY KEY (name),
    CONSTRAINT fk_student_res_spec_operator FOREIGN KEY (name)
        REFERENCES operator (name)
);
