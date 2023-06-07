package edu.uniandes.persistence;

import edu.uniandes.___data.DataUtils;
import edu.uniandes.data.*;
import edu.uniandes.util.OrderedMap;
import edu.uniandes.util.Tabulable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jdo.*;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("FeatureEnvy") public class AlohandesP {
    private final static Logger LOG = LogManager.getLogger(AlohandesP.class);
    private static final String SI = "Si";
    private static AlohandesP INSTANCE;
    private final PersistenceManagerFactory pmf;

    private final SQLHandler<ApartmentSpec> sqlApartmentSpec;
    private final SQLHandler<HostOffer> sqlHostOffer;
    private final SQLHandler<Client> sqlClient;
    private final SQLHandler<ClientPreference> sqlClientPreference;
    private final SQLHandler<HostelSpec> sqlHostelSpec;
    private final SQLHandler<Hotel> sqlHotel;
    private final SQLHandler<HotelRoomSpec> sqlHotelRoomSpec;
    private final SQLHandler<HouseRoomSpec> sqlHouseRoomSpec;
    private final SQLHandler<GroupReservation> sqlGroupReservation;
    private final SQLHandler<Operator> sqlOperator;
    private final SQLHandler<OperatorService> sqlOperatorService;
    private final SQLHandler<OperatorSpec> sqlOperatorSpec;
    private final SQLHandler<ResidenceSpec> sqlResidenceSpec;
    private final SQLHandler<RoomsHotel> sqlRoomsHotel;
    private final SQLHandler<ServiceScheme> sqlServiceScheme;
    private final SQLHandler<StudentResSpec> sqlStudentResSpec;

    private final SQLUtil sqlUtil;
    private final SQLReq sqlReq;

    static {
        getInstance();
    }

    {
        sqlApartmentSpec = new SQLHandler<>(ApartmentSpec.class);
        sqlHostOffer = new SQLHandler<>(HostOffer.class);
        sqlClient = new SQLHandler<>(Client.class);
        sqlClientPreference = new SQLHandler<>(ClientPreference.class);
        sqlHostelSpec = new SQLHandler<>(HostelSpec.class);
        sqlHotel = new SQLHandler<>(Hotel.class);
        sqlHotelRoomSpec = new SQLHandler<>(HotelRoomSpec.class);
        sqlHouseRoomSpec = new SQLHandler<>(HouseRoomSpec.class);
        sqlGroupReservation = new SQLHandler<>(GroupReservation.class);
        sqlOperator = new SQLHandler<>(Operator.class);
        sqlOperatorService = new SQLHandler<>(OperatorService.class);
        sqlOperatorSpec = new SQLHandler<>(OperatorSpec.class);
        sqlResidenceSpec = new SQLHandler<>(ResidenceSpec.class);
        sqlRoomsHotel = new SQLHandler<>(RoomsHotel.class);
        sqlServiceScheme = new SQLHandler<>(ServiceScheme.class);
        sqlStudentResSpec = new SQLHandler<>(StudentResSpec.class);

        sqlUtil = new SQLUtil();
        sqlReq = new SQLReq();
    }

    private AlohandesP() {
        this.pmf = JDOHelper.getPersistenceManagerFactory("Alohandes");
    }

    public static AlohandesP getInstance() {
        if (INSTANCE == null) INSTANCE = new AlohandesP();
        return INSTANCE;
    }

    /**
     * Creates an Operator with its corresponding OperatorSpec, OperatorService and
     * inheritor and returns them as a three-dimensional array.
     *
     * @param operator        the map with the basic information of the Operator
     * @param operatorSpec    the map with the specifications of the Operator
     *                        (capacity, size, location, shared)
     * @param operatorService the map with the services provided by the Operator
     *                        (furnished, wifi, kitchenette)
     * @param inheritor       the map with the additional information of the inheritor
     *                        (costs, number of rooms, opening hours, etc.)
     * @param type            the type of the inheritor
     * @return a three-dimensional array with the Operator, OperatorSpec and
     * OperatorService objects as two-dimensional arrays in the first,
     * second and third positions respectively
     */
    @SuppressWarnings("FeatureEnvy")
    public Object[][][] createOperator(OrderedMap<? super String, String> operator,
                                                                        OrderedMap<? super String, String> operatorSpec,
                                                                        OrderedMap<? super String, String> operatorService,
                                                                        OrderedMap<? super String, String> inheritor,
                                                                        String type) {
        PersistenceManager pm = pmf.getPersistenceManager();
        final String[] name = new String[1];
        return tx(pm,
                  () -> {
                      var keys00 = new Object() {//Operator values
                          private final long nuip = Long.parseLong(operator.get("NUIP del operador"));
                          private final String name00 = operator.get("Nombre del operador");
                          private final String type = operator.get("Que tipo de operador es?");
                      };
                      LOG.trace("INSERT ⟼ Operator::{} ⟼ {} added", keys00.name00,
                                sqlOperator.create(pm, keys00.nuip, keys00.name00, keys00.type));
                      name[0] = keys00.name00;
                      return new Operator(keys00.nuip, name[0], keys00.type).toTable();
                  },
                  () -> {
                      var keys01 = new Object() {//Operator Spec values
                          private final int capacity = Integer.parseInt(operatorSpec.get("Capacidad"));
                          private final int size = Integer.parseInt(operatorSpec.get("Tamanio"));
                          private final String location = operatorSpec.get("Localizacion");
                          private final boolean shared = SI.equals(operatorSpec.get("Compartido?"));
                      };
                      LOG.trace("INSERT ⟼ OperatorSpec::{} ⟼ {} added", name[0],
                                sqlOperatorSpec.create(pm, name[0], keys01.capacity, keys01.size, keys01.location,
                                                       keys01.shared));
                      return new OperatorSpec(name[0], keys01.capacity, keys01.size, keys01.location,
                                                                         keys01.shared).toTable();
                  },
                  () -> {
                      var keys02 = new Object() {//Operator Service values
                          private final boolean furnished = SI.equals(operatorService.get("Amueblado?"));
                          private final boolean wifi = SI.equals(operatorService.get("Tiene wifi?"));
                          private final boolean kitchenette = SI.equals(operatorService.get("Tiene cocineta?"));
                      };
                      LOG.trace("INSERT ⟼ OperatorService::{} ⟼ {} added", name[0],
                                sqlOperatorService.create(pm, name[0], keys02.furnished, keys02.wifi, keys02.kitchenette));
                      return new OperatorService(name[0], keys02.furnished, keys02.wifi,
                                                                            keys02.kitchenette).toTable();
                  },
                  () -> switch (type) {
                      case "Residencia estudiantil" -> {
                          var keys10 = new Object() {
                              private final int restaurant = Integer.parseInt(inheritor.get("Coste del restaurante"));
                              private final int studyRoom = Integer.parseInt(inheritor.get("Coste de la sala de estudio"));
                              private final int recreationRoom = Integer.parseInt(inheritor.get("Coste por sala de ocio"));
                              private final int gym = Integer.parseInt(inheritor.get("Coste por gimnasio"));
                          };
                          LOG.trace("INSERT ⟼ StudentResSpec::{} ⟼ {} added", name[0],
                                    sqlStudentResSpec.create(pm, name[0], keys10.restaurant, keys10.studyRoom,
                                                             keys10.recreationRoom, keys10.gym));

                          yield new StudentResSpec(name[0], keys10.restaurant, keys10.studyRoom, keys10.recreationRoom,
                                                                              keys10.gym).toTable();
                      }
                      case "Residencia no-estudiantil" -> {
                          var keys11 = new Object() {
                              private final int bedroomNum = Integer.parseInt(inheritor.get("Numero de habitaciones"));
                              private final int administrationFee = Integer.parseInt(inheritor.get("Cuota administrativa"));
                              private final String insuranceDesc = inheritor.get("Descripcion del seguro");
                          };
                          LOG.trace("INSERT ⟼ ResidenceSpec::{} ⟼ {} added", name[0],
                                    sqlResidenceSpec.create(pm, name[0], keys11.bedroomNum, keys11.administrationFee,
                                                            keys11.insuranceDesc));
                          yield new ResidenceSpec(name[0], keys11.bedroomNum, keys11.administrationFee,
                                                                             keys11.insuranceDesc).toTable();
                      }
                      case "Hostal" -> {
                          var keys12 = new Object() {
                              private final long nit = Long.parseLong(inheritor.get("NIT"));
                              private final Timestamp openingHours = parse(inheritor.get("Horario de apertura"));
                              private final Timestamp closingHours = parse(inheritor.get("Horario de cierre"));

                              private static Timestamp parse(String s) {
                                  try {
                                      return Timestamp.from(new SimpleDateFormat("HH:mm").parse(s).toInstant());
                                  } catch (ParseException e) {
                                      throw new RuntimeException(e);
                                  }
                              }
                          };
                          LOG.trace("INSERT ⟼ HostelSpec::{} ⟼ {} added", name[0],
                                    sqlHostelSpec.create(pm, name[0], keys12.nit, keys12.openingHours,
                                                         keys12.closingHours));
                          yield new HostelSpec(name[0], keys12.nit, keys12.openingHours, keys12.closingHours).toTable();
                      }
                      case "Habitacion de Hotel" -> {
                          var keys13 = new Object() {
                              private final int roomNumber = Integer.parseInt(inheritor.get("Numero de habitacion"));
                              private final String roomType = inheritor.get("Tipo de sala");
                              private final boolean bathtub = SI.equals(inheritor.get("Tiene baniera?"));
                              private final boolean jacuzzi = SI.equals(inheritor.get("Tiene yacuzzi?"));
                              private final boolean livingRoom = SI.equals(inheritor.get("Tiene sala?"));
                          };
                          LOG.trace("INSERT ⟼ HotelRoomSpec::{} ⟼ {} added", name[0],
                                    sqlHotelRoomSpec.create(pm, name[0], keys13.roomNumber, keys13.roomType,
                                                            keys13.bathtub, keys13.jacuzzi, keys13.livingRoom));
                          yield new HotelRoomSpec(name[0], keys13.roomNumber, keys13.roomType, keys13.bathtub,
                                                                             keys13.jacuzzi, keys13.livingRoom).toTable();
                      }
                      case "Habitacion de casa" -> {
                          var keys14 = new Object() {
                              private final boolean food = SI.equals(inheritor.get("Tiene comidas incluidas?"));
                              private final boolean privateBathroom = SI.equals(inheritor.get("Tiene banio privado?"));
                          };
                          LOG.trace("INSERT ⟼ HouseRoomSpec::{} ⟼ {} added", name[0],
                                    sqlHouseRoomSpec.create(pm, name[0], keys14.food, keys14.privateBathroom));
                          yield new HouseRoomSpec(name[0], keys14.food, keys14.privateBathroom).toTable();
                      }
                      case "Apartamento" -> {
                          var keys15 = new Object() {
                              private final boolean includedServices = SI.equals(inheritor.get("Servicios incluidos?"));
                              private final boolean includedTV = SI.equals(inheritor.get("Television incluida?"));
                              private final int administrationFee = Integer.parseInt(inheritor.get("Cuota administrativa"));
                          };
                          LOG.trace("INSERT ⟼ ApartmentSpec::{} ⟼ {} added", name[0],
                                    sqlApartmentSpec.create(pm, name[0], keys15.includedServices, keys15.includedTV,
                                                            keys15.administrationFee));
                          yield new ApartmentSpec(name[0], keys15.includedServices, keys15.includedTV,
                                                                             keys15.administrationFee).toTable();
                      }
                      default -> null;
                  },
                  () -> {
                      if (type.equals("Habitacion de Hotel")) {
                          var keys13 = new Object() {
                              private final int hotelNIT = Integer.parseInt(inheritor.get("NIT del hotel"));
                              private final int roomNumber = Integer.parseInt(inheritor.get("Numero de habitacion"));
                          };
                          LOG.trace("INSERT ⟼ RoomsHotel::{}-{} ⟼ {} added", name[0], keys13.roomNumber,
                                    sqlRoomsHotel.create(pm, keys13.hotelNIT, keys13.roomNumber));
                          return new RoomsHotel(keys13.hotelNIT, name[0]).toTable();
                      }
                      return null;
                  });
    }

    public Object[][] createHostOffer(Object[] booking,
                                      Date start,
                                      Date end) {
        PersistenceManager pm = pmf.getPersistenceManager();
        return tx(pm,
                  () -> {
                      var keys = new Object() {
                          private final long id = sqlUtil.nextHostOfferID(pm);
                          private final long nuip = Long.parseLong((String) booking[0]);
                          private final String name = booking[1].toString();
                          private final long cost = Long.parseLong((String) booking[2]);
                      };
                      LOG.trace("INSERT ⟼ Booking::{}{} ⟼ {} added", booking[0], booking[1],
                                sqlHostOffer.create(pm, keys.nuip, keys.name, keys.cost, start, end));
                      return new HostOffer(keys.id, keys.nuip, keys.name, keys.cost, start, end, true, true).toTable();
                  })[0];
    }

    public Object[][] createReservation(Object[] offer) {
        PersistenceManager pm = pmf.getPersistenceManager();
        return tx(pm,
                  () -> {
                      final long id = Long.parseLong(offer[0].toString());
                      LOG.trace("INSERT ⟼ Offer::{} ⟼ {} added", id, sqlHostOffer.customQuery(pm, "REQ04", HostOffer.class, id));
                      return DataUtils.fromTable(HostOffer.class,
                                                 sqlHostOffer.retrieveByPK(pm, HostOffer.class, id)).toTable();
                  })[0];
    }

    public Object[][] cancelReservation(Object[] offer) {
        PersistenceManager pm = pmf.getPersistenceManager();
        return tx(pm, () -> {
            final long id = Long.parseLong(offer[0].toString());
            LOG.trace("DELETE ⟼ Offer::{} ⟼ {} deleted", id, sqlHostOffer.customQuery(pm, "REQ05", HostOffer.class, id));
            return DataUtils.fromTable(HostOffer.class,
                                       sqlHostOffer.retrieveByPK(pm, HostOffer.class, id)).toTable();
        })[0];
    }

    public Object[][] createGroupReservation(Object[] group) {
        PersistenceManager pm = pmf.getPersistenceManager();
        return tx(pm,
                  () -> {
                      var keys = new Object() {
                          private final long[] id = Arrays.stream(group).map(Object::toString).mapToLong(Long::parseLong).toArray();
                          private final long reservationID = sqlUtil.nextGroupID(pm);
                      };
                      List<Object[]> groupReservations = new LinkedList<>();
                      groupReservations.add(GroupReservation.getHeaders());
                      Arrays.stream(keys.id).forEach(id -> {
                          LOG.trace("INSERT ⟼ GroupReservation::{} ⟼ {} added", keys.id,
                                    sqlGroupReservation.create(pm, id, keys.reservationID));
                          groupReservations.add(new GroupReservation( id, keys.reservationID).toTable()[1]);
                      });
                      return groupReservations.toArray(Object[][]::new);
                  })[0];
    }

    public Object[][] cancelGroupReservation(Object[] group) {
        PersistenceManager pm = pmf.getPersistenceManager();
        return tx(pm,
                  () -> {
                      long reservationID = Long.parseLong((String) group[0]);
                      List<Object[]> groupReservations = new LinkedList<>();
                      groupReservations.add(GroupReservation.getHeaders());
                      for (Object[] reservation :
                              sqlGroupReservation.customQuery(pm, "REQ0N", GroupReservation.class, reservationID)) {
                          LOG.trace("DELETE ⟼ GroupReservation::{} ⟼ {} deleted", reservationID,
                                    sqlHostOffer.customQuery(pm, "REQ06", HostOffer.class, reservation[0]));
                          groupReservations.add(DataUtils.fromTable(HostOffer.class,
                                                                    sqlHostOffer.retrieveByPK(pm, HostOffer.class,
                                                                                              reservation[0]))
                                                         .toTable()[1]);
                      }
                      return groupReservations.toArray(Object[][]::new);
                  })[0];
    }

    public Object[][][] createService(String name,
                                      OrderedMap<String, String> service) {
        PersistenceManager pm = pmf.getPersistenceManager();
        return tx(pm,
                  () -> {
                      var keys = new Object() {
                          private final String serviceName = service.get(0);
                          private final int serviceCost = Integer.parseInt(service.get(1));
                      };
                      LOG.trace("INSERT ⟼ ServiceScheme::{},{} ⟼ {} added", name, keys.serviceName,
                                sqlServiceScheme.create(pm, name, keys.serviceName, keys.serviceCost));
                      return new ServiceScheme(name, keys.serviceName, keys.serviceCost).toTable();
                  });
    }

    public Object[][][] createClient(OrderedMap<? super String, String> client,
                                     OrderedMap<? super String, String> clientPreference) {
        PersistenceManager pm = pmf.getPersistenceManager();
        long[] nuip = new long[1];
        return tx(pm,
                  () -> {
                      var keys00 = new Object() {//Client values
                          private final long nuip = Long.parseLong(client.get("Numero Unico de Identificacion Personal"));
                          private final String name = client.get("Nombre del cliente");
                          private final String clientType = client.get("Tipo de cliente?");
                      };
                      LOG.trace("INSERT ⟼ Client::{} ⟼ {} added", keys00.nuip,
                                sqlClient.create(pm, keys00.nuip, keys00.name, keys00.clientType));
                      nuip[0] = keys00.nuip;
                      return new Client(nuip[0], keys00.name, keys00.clientType).toTable();
                  },
                  () -> {

                      var keys01 = new Object() {//Client Preferences values
                          private final boolean furnished = SI.equals(clientPreference.get("Amueblado?"));
                          private final boolean shared = SI.equals(clientPreference.get("Compartido?"));
                          private final boolean wifi = SI.equals(clientPreference.get("Que tenga wifi?"));
                          private final boolean kitchenette = SI.equals(clientPreference.get("Que tenga cocineta?"));
                      };
                      LOG.trace("INSERT ⟼ ClientPreference::{} ⟼ {} added", nuip[0],
                                sqlClientPreference.create(pm, nuip[0], keys01.furnished, keys01.shared, keys01.wifi,
                                                           keys01.kitchenette));
                      return new ClientPreference(nuip[0], keys01.furnished, keys01.shared, keys01.wifi,
                                                                             keys01.kitchenette).toTable();
                  });
    }

    public Object[][] createHotel(Object[] hotel) {
        PersistenceManager pm = pmf.getPersistenceManager();
        return tx(pm, () -> {
            var keys00 = new Object() {
                private final int nit = Integer.parseInt((String) hotel[0]);
                private final boolean restaurant = SI.equals(hotel[1]);
                private final boolean pool = SI.equals(hotel[2]);
                private final boolean parkingLot = SI.equals(hotel[3]);
                private final boolean wifi = SI.equals(hotel[4]);
                private final boolean cableTV = SI.equals(hotel[5]);
            };
            LOG.trace("INSERT ⟼ Hotel::{} ⟼ {} added", keys00.nit,
                      sqlHotel.create(pm, keys00.nit, keys00.restaurant, keys00.pool, keys00.parkingLot,
                                      keys00.wifi, keys00.cableTV));
            return new Hotel(keys00.nit, keys00.restaurant, keys00.pool, keys00.parkingLot,
                                                        keys00.wifi, keys00.cableTV).toTable();
        });
    }

    /**
     * Generates an error detail message from the given exception.
     *
     * @param e the exception to generate the error detail message from.
     * @return a String containing the error detail message.
     */
    private String generateErrorDetail(Exception e) {
        return e instanceof JDODataStoreException je ? je.getNestedExceptions()[0].getMessage() : "";
    }

    /**
     * Retrieves a single object of type R using the provided primary key values.
     *
     * @param arg the class of the object to be retrieved
     * @param pks the primary key values to be used in the retrieval process
     * @param <R> the type of object to be retrieved
     * @return a single object of type R
     * @throws ReflectiveOperationException if an error occurs during reflection
     */
    @SuppressWarnings("unchecked")
    public <R extends Tabulable> List<Object[]> retrieveOne(Class<R> arg, Object... pks)
            throws ReflectiveOperationException {
        assert pks.length > 1;
        String sqlClass = "sql" + arg.getSimpleName();
        Field field = AlohandesP.class.getDeclaredField(sqlClass);
        field.setAccessible(true);
        SQLHandler<R> value = (SQLHandler<R>) field.get(this);
        return value.retrieveByPK(pmf.getPersistenceManager(), arg, pks);
    }

    public List<Object[]> retrieveGroupReservation(Object pk)
            throws ReflectiveOperationException {
        return sqlGroupReservation.customQuery(pmf.getPersistenceManager(), "REQ0N", null, pk);
    }

    /**
     * Retrieves all objects of type R from the database using an SQL handler.
     *
     * @param arg the class of the objects to retrieve
     * @param <R> the type of object to be retrieved
     * @return a list of tables of R
     * @throws ReflectiveOperationException if there are problems with the reflection
     */
    @SuppressWarnings("unchecked")
    public <R extends Tabulable> List<Object[]> retrieveAll(Class<R> arg)
            throws ReflectiveOperationException {
        String sqlClass = "sql" + arg.getSimpleName();
        Field field = AlohandesP.class.getDeclaredField(sqlClass);
        field.setAccessible(true);
        return ((SQLHandler<R>) field.get(this)).retrieveAll(pmf.getPersistenceManager(), arg);
    }

    @SuppressWarnings("unchecked")
    public <R extends Tabulable> List<Object[]> delete(Class<R> arg, Object... pks)
            throws ReflectiveOperationException {
        String sqlClass = "sql" + arg.getSimpleName();
        Field field = AlohandesP.class.getDeclaredField(sqlClass);
        field.setAccessible(true);
        SQLHandler<R> value = (SQLHandler<R>) field.get(this);
        return value.deleteByPK(pmf.getPersistenceManager(), pks);
    }

    public List<Object[]> reqC01() {return sqlReq.c01moneyByYear(pmf.getPersistenceManager());}

    public List<Object[]> reqC02() {return sqlReq.c02popularOffers(pmf.getPersistenceManager());}

    public List<Object[]> reqC03() {return sqlReq.c03occupationIndex(pmf.getPersistenceManager());}

    public List<Object[]> reqC04(Date[] r, boolean[] services) {
        return sqlReq.c04availableInRangeAndServices(pmf.getPersistenceManager(), r, services);
    }

    public List<Object[]> reqC05() {return sqlReq.c05alohandesUse(pmf.getPersistenceManager());}

    public List<Object[]> reqC06(long nuip) {return sqlReq.c06alohandesUseTotal(pmf.getPersistenceManager(), nuip);}

    //Iter 3
    public List<Object[]> reqC07(String timeUnit,
                                 String operatorType) {
        return sqlReq.c07alohandesOperation(pmf.getPersistenceManager(), timeUnit, operatorType);
    }

    public List<Object[]> reqC08(String operatorName) {
        return sqlReq.c08frequentClients(pmf.getPersistenceManager(), operatorName);
    }

    public List<Object[]> reqC09() {return sqlReq.c09lowDemand(pmf.getPersistenceManager());}

    @FunctionalInterface private interface TxManager { Object[][] call() throws Exception; }

    private Object[][][] tx(PersistenceManager pm, TxManager... lambdas) {
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            //noinspection AnonymousInnerClassMayBeStatic
            Object[][][] objs = new ArrayList<Object[][]>(){{
                for (TxManager l : lambdas) {
                    Object[][] call = l.call();
                    if (call != null) add(call);
                }
            }}.toArray(Object[][][]::new);
            tx.commit();
            return objs;
        } catch (Exception e) {
            LOG.error("{}: {}\n {}", e.getClass().getSimpleName(), e.getMessage(), generateErrorDetail(e));
            throw new RuntimeException(e);
        } finally {
            if (tx.isActive()) tx.rollback();
        }
    }
}
