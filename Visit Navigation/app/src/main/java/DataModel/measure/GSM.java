package DataModel.measure;

import DataModel.IEntity;

public class GSM implements IEntity<Integer> {
	
	private Integer id;
	
	/* attributes */
	protected String cellId = "";
	protected String areaId = "";
	protected String signalStrength = "";
	protected String MCC = "";
	protected String MNC = "";
	protected String networkName = "";

	/* **************** Getter and Setter Methods **************** */

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(String signalStrength) {
		this.signalStrength = signalStrength;
	}

	public String getMCC() {
		return MCC;
	}

	public void setMCC(String mcc) {
		MCC = mcc;
	}

	public String getMNC() {
		return MNC;
	}

	public void setMNC(String mnc) {
		MNC = mnc;
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
}
