package Home;

import DataModel.Cell;
import DataModel.PathRequest;
import Handler.Request;

/**
 * Created by nisan on 21/05/2016.
 */
public class PathHome implements IEntityHome {

    public static void getPath() {
        EntityHome.performRequest(Request.RequestType.getLocationList);
    }

    /**
     * Performs an getPath request with callback
     *
     * @param callback
     *            {@link EntityHomeCallback}
     */
    public static void getPath(PathRequest path, EntityHomeCallback callback) {
        EntityHome.performRequest(Request.RequestType.getPath, path, callback);
    }
}
