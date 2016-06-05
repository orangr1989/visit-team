package Home;

import android.util.Log;

import DataModel.Map;
import Handler.Request;

public class MapHome implements IEntityHome  {
    private static final String TAG = MapHome.class.getName();

    /**
     * Performs an getMapList request without callback
     */
    public static void getMapList() {
        EntityHome.performRequest(Request.RequestType.getMapList);
    }

    /**
     * Performs an getMapList request with callback
     *
     * @param callback
     *            {@link EntityHomeCallback}
     */
    public static void getMapList(EntityHomeCallback callback) {
      //  EntityHome.performRequest(Request.RequestType.getMapList, callback);
        EntityHome.performRequest(Request.RequestType.getMapList,callback);
    }

    /**
     * Performs an setMap request without callback
     *
     * @param map
     *            {@link Map} to be added
     */
    public static void setMap(Map map) {
        EntityHome.performRequest(Request.RequestType.setMap, map);
    }

    /**
     * Performs an setMap request with callback
     *
     * @param map
     *            {@link Map} to be added
     * @param callback
     *            {@link EntityHomeCallback}
     */
    public static void setMap(Map map, EntityHomeCallback callback) {
        EntityHome.performRequest(Request.RequestType.setMap, map, callback);
    }

    /**
     * Performs an removeMap request without callback
     *
     * @param map
     *            {@link Map} to be removed
     * @return <code>true</code> if request can be performed, <code>false</code>
     *         if the {@link Map} has no remote id
     */
    public static boolean removeMap(Map map) {
        return removeMap(map, null);
    }

    /**
     * Performs an removeMap request with callback
     *
     * @param map
     *            {@link Map} to be removed
     * @param callback
     *            {@link EntityHomeCallback}
     * @return <code>true</code> if request can be performed, <code>false</code>
     *         if the {@link Map} has no remote id
     */
    public static boolean removeMap(Map map, EntityHomeCallback callback) {
        if (map.getId() == null || map.getId() < 0) {
            Log.i(TAG, "map can't be removed because no remote id is present");
            return false;
        }
        EntityHome.performRequest(Request.RequestType.removeMap, map, callback);
        return true;
    }

}
