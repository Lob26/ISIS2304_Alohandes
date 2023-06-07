package edu.uniandes.persistence;

import edu.uniandes.data.HostOffer;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"unchecked", "rawtypes"}) class SQLReq {
    private static final Properties P = new Properties();

    static {
        try {
            P.loadFromXML(SQLReq.class.getResourceAsStream("/META-INF/require.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Query query(PersistenceManager pm, String name, String... filer) {
        return pm.newQuery(Query.SQL, P.getProperty(name) + (filer.length == 0 ? "" : " AND " + filer[0]));
    }

    static List<Object[]> purify(List<Object[]> rows, String... header) {
        return new LinkedList<>(){{
            add(header);
            addAll(rows);
        }};
    }

    static List<Object[]> purify(Object[] row, String... header) {
        return new LinkedList<>(){{
            add(header);
            add(row);
        }};
    }

    //Iter 2
    List<Object[]> c01moneyByYear(PersistenceManager pm) {
        return purify((List<Object[]>) query(pm, "moneyByYear").executeList(), "Operator name", "Total money");
    }

    List<Object[]> c02popularOffers(PersistenceManager pm) {
        return purify(query(pm, "popularOffers").executeList(), "Operator name", "Number of host offer");
    }

    List<Object[]> c03occupationIndex(PersistenceManager pm) {
        return purify(query(pm, "occupationIndex").executeList(), "Operator name", "Occupation Index");
    }

    List<Object[]> c04availableInRangeAndServices(PersistenceManager pm, Date[] dateR, boolean[] services) {
        String furnished = services[0] ? "furnished = 1" : null;
        String wifi = services[1] ? "wifi = 1" : null;
        String kitchenette = services[2] ? "kitchenette = 1" : null;

        String filter = Stream.of(furnished, wifi, kitchenette)
                              .filter(Objects::nonNull)
                              .collect(Collectors.joining(" AND "));
        Query q = query(pm, "availableInRangeAndServices", filter);
        q.setParameters(dateR[0], dateR[1]);
        return purify(q.executeList(), "Operator name", "Furnished", "Wifi", "Kitchenette");
    }

    List<Object[]> c05alohandesUse(PersistenceManager pm) {
        return purify(query(pm, "alohandesUse").executeList(), "NUIP", "Role", "Id", "Start", "End", "Days spent");
    }

    List<Object[]> c06alohandesUseTotal(PersistenceManager pm, long nuip) {
        Query q = query(pm, "alohandesUseTotal");
        q.setParameters(nuip, nuip);
        return purify(q.executeList(),"Role", "Id", "Start", "End", "Days spent");
    }

    //Iter 3
    List<Object[]> c07alohandesOperation(PersistenceManager pm, String unit, String type) {
        Query q = query(pm, "alohandesOperation");
        q.setParameters(unit, type, unit);
        return purify(q.executeList(), "Operator name", "Min revenue", "Max revenue", "Min occupancy", "Max occupancy", "Start date");
    }

    List<Object[]> c08frequentClients(PersistenceManager pm, String operatorName) {
        Query q = query(pm, "frequentClients");
        q.setParameters(operatorName);
        return purify(q.executeList(), "NUIP", "Name", "Number of reservations");
    }

    List<Object[]> c09lowDemand(PersistenceManager pm) {
        return purify(query(pm, "lowDemand").executeList(), HostOffer.getHeaders());
    }
}
