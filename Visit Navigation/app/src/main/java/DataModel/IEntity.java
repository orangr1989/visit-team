package DataModel;

import java.io.Serializable;

public interface IEntity<ID extends Serializable> {

    /**
     * gets the primary key
     *
     * @return primary key
     */
    ID getId();

    /**
     * sets the primary key
     *
     * @param id primary key
     */
    void setId(ID id);
}
