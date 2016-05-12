package DataModel.measure;

import DataModel.IEntity;

public class Wifi implements IEntity<Integer> {
	
	private Integer id;
	
	/*
	 *  The unique id of the AP. This is the same thing as the AP's MAC address 
	 */
	protected String bssid = "";

	/*
	 *  The human readable network address
	 */
	protected String ssid = "";

	/*
	 *  The observed signal strength 
	 */
	protected int rssi = 0;

	/*
	 *  Denotes whether encryption in enabled for the AP or not 
	 */
	protected boolean wepEnabled = false;

	/*
	 *  Denotes whether the AP in in infrastructure or peer-to-peer mode 
	 */
	protected boolean isInfrastructure = true;

	/* **************** Getter and Setter Methods **************** */

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public boolean isWepEnabled() {
		return wepEnabled;
	}

	public void setWepEnabled(boolean wepEnabled) {
		this.wepEnabled = wepEnabled;
	}

	public boolean isInfrastructure() {
		return isInfrastructure;
	}

	public void setInfrastructure(boolean isInfrastructure) {
		this.isInfrastructure = isInfrastructure;
	}
}
