package edu.uniandes.persistence;

import edu.uniandes.annotations.handler.TableHandler;
import edu.uniandes.util.Tabulable;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

@SuppressWarnings("unchecked")
class SQLHandler<Entity extends Tabulable> {
    private final Map<String, String> queries;

    SQLHandler(Class<Entity> entity) {this.queries = TableHandler.getQueries(entity);}

    private static void cleanBooleans(Object[] values) {
        IntStream.range(0, values.length).forEach(i -> {if (values[i] instanceof Boolean b) values[i] = b ? 1 : 0;});
    }

    /** Create an entity in the DB */
    long create(PersistenceManager pm, Object... values) {
        assert values != null && values.length >= 1;
        cleanBooleans(values);
        return (long) pm.newQuery(Query.SQL, queries.get("INSERT")).setParameters(values).execute();
    }

    /** Retrieve a single entity(Using its PKs) from the DB */
    List<Object[]> retrieveByPK(PersistenceManager pm, Class<Entity> entity, Object... keys)
            throws ReflectiveOperationException {
        assert keys != null && keys.length >= 1;
        cleanBooleans(keys);
        return List.of((Object[]) pm.newQuery(Query.SQL, queries.get("SELECT_ONE")).setParameters(keys).executeUnique(),
                       (String[]) entity.getMethod("getHeaders").invoke(null));
    }

    /** Retrieve all entities from the DB */
    List<Object[]> retrieveAll(PersistenceManager pm, Class<Entity> entity)
            throws ReflectiveOperationException {
        return SQLReq.purify((List<Object[]>) pm.newQuery(Query.SQL, queries.get("SELECT_ALL")).executeList(),
                             (String[]) entity.getMethod("getHeaders").invoke(null));
    }

    /** Update a single entity(Using its PKs) from the DB */
    List<Object[]> updateByPK(PersistenceManager pm, Class<Entity> entity, Object... values) {
        assert values != null && values.length >= 1;
        cleanBooleans(values);
        return List.of(new Object[]{"Filas actualizadas"},
                       new Object[]{pm.newQuery(Query.SQL, queries.get("UPDATE")).setParameters(values).execute()});
    }

    /** Delete a single entity(Using its PKs) from the DB */
    List<Object[]> deleteByPK(PersistenceManager pm,
                    Object... keys) {
        assert keys != null && keys.length >= 1;
        Query<Long> q = pm.newQuery(Query.SQL, queries.get("DELETE"));
        return List.of(new Object[]{"Filas actualizadas"}, new Object[]{q.setParameters(keys).execute()});
    }

    List<Object[]> customQuery(PersistenceManager pm, String key, Class<Entity> entity, Object... args)
            throws ReflectiveOperationException {
        var custom = pm.newQuery(Query.SQL, Optional.ofNullable(queries.get(key)).orElseThrow(IllegalArgumentException::new));

        if (entity == null) return List.of(new Object[]{"Filas modificadas"}, (Object[]) custom.setParameters(args).executeUnique());
        else return SQLReq.purify((List<Object[]>) custom.setParameters(args).executeList(),
                                  (String[]) entity.getMethod("getHeaders").invoke(null));
    }
}
