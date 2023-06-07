package edu.uniandes.business;

import edu.uniandes.persistence.AlohandesP;
import edu.uniandes.util.OrderedMap;
import edu.uniandes.util.Tabulable;
import edu.uniandes.util.TextTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;

public class AlohandesB {
    private final static Logger LOG = LogManager.getLogger(AlohandesB.class);
    private final AlohandesP persistence;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public AlohandesB(AlohandesP persistence) {
        this.persistence = persistence;
    }

    //<editor-fold desc="CRUD">
    public String createOperator(OrderedMap<? super String, String> operator,
                                 OrderedMap<? super String, String> operatorSpec,
                                 OrderedMap<? super String, String> operatorService,
                                 OrderedMap<? super String, String> inheritor,
                                 Set<OrderedMap<String, String>> services) {
        String type = operator.get("Que tipo de operador es?");
        LOG.info("START ⟼ Creando un operador {} [Tipo::{}]", operator.get("Nombre del operador"), type);
        Object[][][] response = persistence.createOperator(operator, operatorSpec, operatorService, inheritor, type);
        LOG.info("END ⟼ Operador {} creado [Tipo::{}]", operator.get("Nombre del operador"), type);
        String[] keys = new String[]{"Operator::\n", "OperatorSpec::\n", "OperatorService::\n"};
        StringBuilder sb = new StringBuilder(128);
        IntStream.range(0, keys.length)
                 .forEach(i -> sb.append(keys[i]).append(TextTable.builder().append(response[i]).toString()));

        if (type.equals("Habitacion de Hotel")) {
            sb.append("RoomsHotel::\n").append(TextTable.builder().append(response[4]).toString());
        } else if (!services.isEmpty()) {//type.equals("Habitacion de Casa")
            TextTable builder = TextTable.builder().append(new String[]{"name", "serviceName", "serviceCost"});
            services.stream()
                    .map(val -> persistence.createService(operator.get("Nombre del operador"), val)[0])
                    .forEach(builder::append);
            sb.append("Service::\n").append(builder);
        }
        return sb.toString();
    }

    public String createClient(OrderedMap<? super String, String> client,
                               OrderedMap<? super String, String> clientPreference) {
        LOG.info("START ⟼ Creando un cliente {}", client.get("Numero Unico de Identificacion Personal"));
        Object[][][] apClient = persistence.createClient(client, clientPreference);
        LOG.info("END ⟼ Cliente {} creado", client.get("Numero Unico de Identificacion Personal"));
        return "Client::\n" + TextTable.builder().append(apClient[0]).toString() +
               "ClientPreference::\n" + TextTable.builder().append(apClient[1]).toString();
    }

    public String createHostOffer(Object[] booking) {
        LOG.info("START ⟼ Creando una reserva {}-{}", booking);
        try {
            Object[][] response = persistence.createHostOffer(booking,
                                                              sdf.parse((String) booking[3]),
                                                              sdf.parse((String) booking[4]));
            LOG.info("END ⟼ Reserva creada");
            return TextTable.builder().append(response).toString();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public String createReservation(Object[] offer) {
        LOG.info("START ⟼ Creando una oferta {}", offer[0]);
        Object[][] response = persistence.createReservation(offer);
        LOG.info("END ⟼ Oferta creada");
        return TextTable.builder().append(response).toString();
    }

    public String createGroupReservation(Object[] offer) {
        LOG.info("START ⟼ Creando una oferta conjunta {}", offer[0]);
        Object[][] response = persistence.createGroupReservation(offer);
        LOG.info("END ⟼ Oferta conjunta creada");
        return TextTable.builder().append(response).toString();
    }

    public String createHotel(Object[] hotel) {
        LOG.info("START ⟼ Creando un hotel {}", hotel[0]);
        Object[][] response = persistence.createHotel(hotel);
        LOG.info("END ⟼ Hotel {} creado", hotel[0]);
        return TextTable.builder().append(response).toString();
    }

    public <R extends Tabulable> String retrieveAll(Class<R> arg)
            throws ReflectiveOperationException {
        LOG.info("START ⟼ Recuperando todos los {}", arg.getSimpleName());
        List<Object[]> vos = persistence.retrieveAll(arg);
        LOG.info("END ⟼ {} de {} recuperados", vos.size(), arg.getSimpleName());

        TextTable builder = TextTable.builder();
        vos.forEach(builder::append);

        return arg.getSimpleName() + "::\n" + builder;
    }

    public <R extends Tabulable> String retrieveOne(Class<R> arg, Object... pks)
            throws ReflectiveOperationException {
        LOG.info("START ⟼ Recuperando {} con id(s) {}", arg.getSimpleName(), Arrays.toString(pks));
        List<Object[]> vo = persistence.retrieveOne(arg, pks);
        LOG.info("END ⟼ {} con id(s) {} recuperado [{}]", arg, Arrays.toString(pks), vo);

        TextTable builder = TextTable.builder();
        vo.forEach(builder::append);

        return arg.getSimpleName() + " PK:" + Arrays.toString(pks) + "::\n" + builder;
    }

    public String retrieveGroupReservation(Object... pks)
            throws ReflectiveOperationException {
        LOG.info("START ⟼ Recuperando GroupReservation con id(s) {}", Arrays.toString(pks));
        List<Object[]> vo = persistence.retrieveGroupReservation(pks[0]);
        LOG.info("END ⟼ GroupReservation con id(s) {} recuperado [{}]", Arrays.toString(pks), vo);
        TextTable builder = TextTable.builder();
        vo.forEach(builder::append);

        return "GroupReservation PK:" + Arrays.toString(pks) + "::\n" + builder;
    }

    public <R extends Tabulable> String delete(Class<R> arg, Object... pks)
            throws ReflectiveOperationException {
        LOG.info("START ⟼ Borrando {} con id(s) {}", arg.getSimpleName(), pks);
        List<Object[]> l = switch (arg.getSimpleName()) {
            case "RoomsHotel", "ServiceScheme" -> persistence.delete(arg, pks[0], pks[1]);
            case "Offer" -> {
                //TODO Cobrar por fecha de cancelacion
                System.out.println("TODO");
                yield persistence.delete(arg, pks[0]);
            }
            default -> persistence.delete(arg, pks[0]);
        };
        LOG.info("END ⟼ {} {} borrado(s)", l.get(0), arg);
        TextTable builder = TextTable.builder();
        l.forEach(builder::append);

        return arg.getSimpleName() + " PK:" + Arrays.toString(pks) + "::\n" + builder;
    }
    //</editor-fold>

    public String changeStatusBooking(Object... input) {
        boolean status = Boolean.parseBoolean(input[0].toString());
        Object[][] result;
        if (status) {
            LOG.info("START ⟼ Aceptando reserva {}", input);
            result = persistence.createReservation(input);
        } else {
            LOG.info("START ⟼ Rechazando reserva {}", input);
            result = persistence.cancelReservation(input);
        }
        TextTable builder = TextTable.builder();
        builder.appends(result);
        LOG.info("END ⟼ Reserva {} {}", input, status ? "aceptada" : "rechazada");
        return "Reserva " + (status ? "aceptada" : "rechazada") + "::\n" + builder;
    }

    public String changeStatusGroupBooking(Object... input) {
        boolean status = Boolean.parseBoolean(input[0].toString());
        Object[][] result;
        if (status) {
            LOG.info("START ⟼ Aceptando reserva grupal {}", input);
            result = persistence.createGroupReservation(input);
        } else {
            LOG.info("START ⟼ Rechazando reserva grupal {}", input);
            result = persistence.cancelGroupReservation(input);
        }
        TextTable builder = TextTable.builder();
        builder.appends(result);
        LOG.info("END ⟼ Reserva grupal {} {}", input, status ? "aceptada" : "rechazada");
        return "Reserva grupal " + (status ? "aceptada" : "rechazada") + "::\n" + builder;
    }


    //<editor-fold desc="Requirements">
    public String moneyByYear() {
        LOG.info("START ⟼ Consulta01");
        List<Object[]> list = persistence.reqC01();
        LOG.info("END ⟼ Consulta01");
        TextTable builder = TextTable.builder();
        list.forEach(builder::append);
        return builder.toString();
    }

    public String popularOffers() {
        LOG.info("START ⟼ Consulta02");
        List<Object[]> list = persistence.reqC02();
        LOG.info("END ⟼ Consulta02");
        TextTable builder = TextTable.builder();
        list.forEach(builder::append);
        return builder.toString();
    }

    public String occupationIndex() {
        LOG.info("START ⟼ Consulta03");
        List<Object[]> list = persistence.reqC03();
        LOG.info("END ⟼ Consulta03");
        TextTable builder = TextTable.builder();
        list.forEach(builder::append);
        return builder.toString();
    }

    public String availableInRangeAndServices(Object[] input) {
        LOG.info("START ⟼ Consulta04");
        try {
            Date[] r = {
                    sdf.parse((String) input[0]),
                    sdf.parse((String) input[1])
            };
            String servicesGet = (String) input[2];
            boolean[] services = {
                    servicesGet.contains("amueblado"),
                    servicesGet.contains("cocineta"),
                    servicesGet.contains("wifi")
            };
            List<Object[]> list = persistence.reqC04(r, services);
            LOG.info("END ⟼ Consulta04");
            TextTable builder = TextTable.builder();
            list.forEach(builder::append);
            return builder.toString();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public String alohandesUse() {
        LOG.info("START ⟼ Consulta05");
        List<Object[]> list = persistence.reqC05();
        LOG.info("END ⟼ Consulta05");
        TextTable builder = TextTable.builder();
        list.forEach(builder::append);
        return builder.toString();
    }

    public String alohandesUse(Object[] input) {
        LOG.info("START ⟼ Consulta06");
        List<Object[]> list = persistence.reqC06(Long.parseLong((String) input[0]));
        LOG.info("END ⟼ Consulta06");
        TextTable builder = TextTable.builder();
        list.forEach(builder::append);
        return builder.toString();
    }
    //Iter3

    public String alohandesOperation(Object[] input) {
        LOG.info("START ⟼ Consulta07");
        List<Object[]> list = persistence.reqC07(
                Map.of("Dia", "dd", "Semana", "iw", "Mes", "mm", "Anio", "yy").get((String) input[0]),
                (String) input[1]);
        LOG.info("END ⟼ Consulta07");
        TextTable builder = TextTable.builder();
        list.forEach(builder::append);
        return builder.toString();
    }

    public String frequentClients(Object[] input) {
        LOG.info("START ⟼ Consulta08");
        List<Object[]> list = persistence.reqC08((String) input[0]);
        LOG.info("END ⟼ Consulta08");
        TextTable builder = TextTable.builder();
        list.forEach(builder::append);
        return builder.toString();
    }

    public String lowDemand() {
        LOG.info("START ⟼ Consulta09");
        List<Object[]> list = persistence.reqC09();
        LOG.info("END ⟼ Consulta09");
        TextTable builder = TextTable.builder();
        list.forEach(builder::append);
        return builder.toString();
    }
    //</editor-fold>
}
