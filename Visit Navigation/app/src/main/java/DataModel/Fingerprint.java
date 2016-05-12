package DataModel;

/**
 * Describes a fingerprint containing a location and a corresponding measurement
 *
 **/
public class Fingerprint implements IEntity<Integer> {
	
	private Integer id;

	/* the measurement that and the location which are associated hereby */
	protected Location location;
	protected Measurement measurement;

	/* **************** Constructors **************** */

	public Fingerprint(Location location, Measurement measurement) {
		this.location = location;
		this.measurement = measurement;
	}
	
	public Fingerprint() {
		this.location = new Location();
		this.measurement = new Measurement();
    }

	/* ************ Getter / Setter Methods ************ */

	public Integer getId() {
        return id;
    }

	public void setId(Integer id) {
        this.id = id;
    }

	public Measurement getMeasurement() {
		return measurement;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setMeasurement(Measurement measurement) {
		this.measurement = measurement;
	}
}
