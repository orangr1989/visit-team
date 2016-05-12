package DataModel;

public class Map implements IEntity<Integer> {
	
	private Integer id;
	
	/*
	 * unique identifier, commonly the name of this map e.g. 'Azrieli floor 1'
	 */
	protected String mapName = "";

	/*
	 * the Path of the corresponding map (image) where this location resides
	 */
	protected String mapPath = "";
	
	/*
	 * map floor number in the building
	 */
	protected int floorNumber = 0;

	/* **************** Constructors **************** */

	public Map() {
		mapName = "";
		mapPath = "";
		floorNumber = 0;
	}

	public Map(String mapName, String map, int floor) {
		super();
		this.mapName = mapName;
		this.mapPath = map;
		this.floorNumber = floor;
	}

	/* **************** Getter and Setter Methods **************** */

	public Integer getId() {
        return id;
    }

	public void setId(Integer id) {
        this.id = id;
    }
	
	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public String getMapURL() {
		return mapPath;
	}
	
	public void setMapURL(String map) {
		this.mapPath = map;
	}

	public void setMapFloorNumber(int floor) {
		this.floorNumber = floor;
	}
	
	public int getMapFloorNumber() {
		return floorNumber;
	}

	public String toString() {
		return super.toString() + ": " + mapName + "; mapURL = " + mapPath + ";";
	}
}
