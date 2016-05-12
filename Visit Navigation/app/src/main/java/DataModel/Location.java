package DataModel;

public class Location implements IEntity<Integer> {
	
	private Integer id;
	
	/*
	 * unique identifier, commonly the name of this location
	 */
	protected String symbolicID = "";

	/*
	 * the Map where this location resides. includes path to image and a name
	 */
	protected Map map;

	/*
	 * X and Y coordinates of the location in the image referenced by fileName
	 * in pixel format
	 */
	protected int mapXcord = 0;
	protected int mapYcord = 0;

	/*
	 * StaticResources.LOCATION_UNKNOWN = location totally unknown
	 * StaticResources.LOCATION_KNOWN = location known Numbers in between define
	 * level of accuracy
	 */
	protected int accuracy = 0;
	
	/* **************** Constructors **************** */

	public Location() {
		this("", new Map(), 0, 0, 0);
	}

	public Location(String symbolicId, Map map, int mapXcord, int mapYcord, int accuracy) {
		this.symbolicID = symbolicId;
		this.map = map;
		this.mapXcord = mapXcord;
		this.mapYcord = mapYcord;
		this.accuracy = accuracy;
	}

	/* **************** Getter and Setter Methods **************** */

	public Integer getId() {
        return id;
    }

	public void setId(Integer id) {
        this.id = id;
    }

	public int getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}

	public String getSymbolicID() {
		return symbolicID;
	}

	public void setSymbolicID(String symbolicID) {
		this.symbolicID = symbolicID;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public int getMapXcord() {
		return mapXcord;
	}

	public void setMapXcord(int mapXcord) {
		this.mapXcord = mapXcord;
	}

	public int getMapYcord() {
		return mapYcord;
	}

	public void setMapYcord(int mapYcord) {
		this.mapYcord = mapYcord;
	}
}
