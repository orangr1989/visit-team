package Home;

import android.util.Log;

import java.util.HashMap;
import java.util.List;

import DataModel.Location;
import DataModel.Map;
import DataModel.Measurement;
import Handler.Request;
import Handler.Response;

public class LocationHome implements IEntityHome {

    private static final String TAG = LocationHome.class.getName();

    /**
     * Performs an getLocationList request without callback
     */
    public static void getLocationList() {
        EntityHome.performRequest(Request.RequestType.getLocationList);
    }

    /**
     * Performs an getLocationList request with callback
     *
     * @param callback
     *            {@link EntityHomeCallback}
     */
    public static void getLocationList(EntityHomeCallback callback) {
        EntityHome.performRequest(Request.RequestType.getLocationList, callback);
    }

    /**
     * Performs an getLocation request without callback to estimate the current
     * location
     *
     * @param measurement
     *            {@link Measurement}
     */
    public static void getLocation(Measurement measurement) {
        EntityHome.performRequest(Request.RequestType.getLocation, measurement);
    }

    /**
     * Performs an getLocation request with callback to estimate the current
     * location
     *
     * @param measurement
     *            {@link Measurement}
     * @param callback
     *            {@link EntityHomeCallback}
     */
    public static void getLocation(Measurement measurement,
                                   EntityHomeCallback callback) {
        EntityHome.performRequest(Request.RequestType.getLocation, measurement,
                callback);
    }

    /**
     * Performs an updateLocation request without callback
     *
     * @param loc
     *            {@link Location} to be updated
     * @return <code>true</code> if request can be performed, <code>false</code>
     *         if the {@link Location} has no remote id
     */
    public static boolean updateLocation(Location loc) {
        return updateLocation(loc, null);
    }

    /**
     * Performs an updateLocation request with callback
     *
     * @param loc
     *            {@link Location} to be updated
     * @param callback
     *            {@link EntityHomeCallback}
     * @return <code>true</code> if request can be performed, <code>false</code>
     *         if the {@link Location} has no remote id
     */
    public static boolean updateLocation(Location loc,
                                         EntityHomeCallback callback) {
        if (loc.getId() == null || loc.getId() < 0) {
            Log
                    .i(TAG,
                            "location can't be updated because no remote id is present");
            return false;
        }
        EntityHome.performRequest(Request.RequestType.updateLocation, loc,
                callback);
        return true;
    }

    /**
     * Performs a removeLocation request without callback
     *
     * @param loc
     *            {@link Location} to be removed
     * @return <code>true</code> if request can be performed, <code>false</code>
     *         if the {@link Location} has no remote id
     */
    public static boolean removeLocation(Location loc) {
        return removeLocation(loc, null);
    }

    /**
     * Performs a removeLocation request with callback
     *
     * @param loc
     *            {@link Location} to be removed
     * @param callback
     *            {@link EntityHomeCallback}
     * @return <code>true</code> if request can be performed, <code>false</code>
     *         if the {@link Location} has no remote id
     */
    public static boolean removeLocation(Location loc,
                                         EntityHomeCallback callback) {
        if (loc.getId() == null || loc.getId() < 0) {
            Log
                    .i(TAG,
                            "location can't be removed because no remote id is present");
            return false;
        }
        EntityHome.performRequest(Request.RequestType.removeLocation, loc,
                callback);
        return true;
    }

}
