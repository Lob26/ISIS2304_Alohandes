package edu.uniandes.persistence;

import edu.uniandes.___data.SeqUtils;
import javax.jdo.PersistenceManager;

import static javax.jdo.Query.SQL;

class SQLUtil {
    long nextHostOfferID(PersistenceManager pm) {
        return (long) pm.newQuery(SQL, "SELECT " + SeqUtils.sequence("HostOffer") + ".nextval FROM dual").executeUnique();
    }

    long nextGroupID(PersistenceManager pm) {
        return (long) pm.newQuery(SQL, "SELECT " + SeqUtils.sequence("GroupReservation") + ".nextval FROM dual").executeUnique();
    }
}
