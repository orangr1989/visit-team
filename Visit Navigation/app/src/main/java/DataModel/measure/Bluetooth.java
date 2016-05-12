package DataModel.measure;

import DataModel.IEntity;

public class Bluetooth implements IEntity<Integer> {
	
	private Integer id;
	
	/* attributes */
	protected String friendlyName = "";
	protected String bluetoothAddress = "";
	protected String majorDeviceClass = ""; // see
	protected String minorDeviceClass = "";

	/* **************** Getter and Setter Methods **************** */

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public String getBluetoothAddress() {
		return bluetoothAddress;
	}

	public void setBluetoothAddress(String bluetoothAddress) {
		this.bluetoothAddress = bluetoothAddress;
	}

	public String getMajorDeviceClass() {
		return majorDeviceClass;
	}

	public void setMajorDeviceClass(String majorDeviceClass) {
		this.majorDeviceClass = majorDeviceClass;
	}

	public String getMinorDeviceClass() {
		return minorDeviceClass;
	}

	public void setMinorDeviceClass(String minorDeviceClass) {
		this.minorDeviceClass = minorDeviceClass;
	}
}
