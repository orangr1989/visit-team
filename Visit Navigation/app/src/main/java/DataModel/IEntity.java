package DataModel;

import java.io.Serializable;

public interface IEntity<ID extends Serializable> {

    /**
     * gets the primary key
     *
     * @return primary key
     */
    public ID getId();

    /**
     * sets the primary key
     *
     * @param id primary key
     */
    public void setId(ID id);
}
