package cl.dsoto.trading.model;

import java.io.Serializable;
import java.util.logging.Logger;


public abstract class PersistentEntity implements Serializable {

    private static final Logger logger = Logger.getLogger(PersistentEntity.class.getName());

    /** Constante para indicar un valor por defecto que indique NO persistencia */
    public static final long NON_PERSISTED_ID = DAO.NON_PERSISTED_ID;

    /** El identificador único de la entidad, inicialmente fijado en <code>NON_PERSISTED_ID</code>. */
    private long id = NON_PERSISTED_ID;

    public PersistentEntity() {
        this(NON_PERSISTED_ID);
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public boolean isPersistent() {
        return getId() != NON_PERSISTED_ID;
    }

    public PersistentEntity(long id) {
        this.id = id;
    }
}
