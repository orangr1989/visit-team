package DataModel;

import java.util.Vector;

import DataModel.measure.Bluetooth;
import DataModel.measure.GSM;
import DataModel.measure.Wifi;

public class Measurement implements IEntity<Integer> {

	private Integer id;
	
	/* time of measurment */
	protected long timestamp = 0;

	/* set of GSM readings that where taken during the measurement */
	protected Vector<GSM> gsmReadings;

	/* set of WiFi readings that where taken during the measurement */
	protected Vector<Wifi> wifiReadings;

	/* set of Bluetooth readings that where taken during the measurement */
	protected Vector<Bluetooth> bluetoothReadings;

	/* constructor */
	public Measurement() {
		timestamp = System.currentTimeMillis();
		gsmReadings = new Vector<GSM>();
		wifiReadings = new Vector<Wifi>();
		bluetoothReadings = new Vector<Bluetooth>();
		
	}
	
	public Measurement(Vector<GSM> gsmReadings, Vector<Wifi> wifiReadings, Vector<Bluetooth> bluetoothReadings) {
		timestamp = System.currentTimeMillis();
		this.gsmReadings = gsmReadings;
		this.wifiReadings = wifiReadings;
		this.bluetoothReadings = bluetoothReadings;
	}


	/* ************ Getter and Setter Methods ************ */

	public Integer getId() {
        return id;
    }

	public void setId(Integer id) {
        this.id = id;
    }

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public Vector<GSM> getGsmReadings() {
		return gsmReadings;
	}

	public void addGSMReading(GSM gsmReading) {
		this.gsmReadings.addElement(gsmReading);
	}

	public Vector<Wifi> getWiFiReadings() {
		return wifiReadings;
	}

	public void addWiFiReading(Wifi wiFiReading) {
		this.wifiReadings.addElement(wiFiReading);
	}

	public Vector<Bluetooth> getBluetoothReadings() {
		return bluetoothReadings;
	}

	public void addBluetoothReading(Bluetooth bluetoothReading) {
		this.bluetoothReadings.addElement(bluetoothReading);
	}

	public boolean equals(Object obj) {
        return super.equals(obj) || (this.getTimestamp() == ((Measurement) obj).getTimestamp());
    }

   public void setBluetoothReadings(Vector<Bluetooth> br) {
       bluetoothReadings = br;
   }

   public void setWiFiReadings(Vector<Wifi> wr) {
       wifiReadings = wr;
   }

   public void setGSMReadings(Vector<GSM> gr) {
       gsmReadings = gr;
   }
}
