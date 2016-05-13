package Home;

import DataModel.Fingerprint;
import Handler.Request;

public class FingerprintHome implements IEntityHome {
    /**
     * Performs an setFingerprint request without callback
     *
     * @param fingerprint
     *            {@link Fingerprint}
     */
    public static void setFingerprint(Fingerprint fingerprint) {
        EntityHome
                .performRequest(Request.RequestType.setFingerprint, fingerprint);
    }

    /**
     * Performs an setFingerprint request with callback
     *
     * @param fingerprint
     *            {@link Fingerprint}
     * @param callback
     *            {@link EntityHomeCallback}
     */
    public static void setFingerprint(Fingerprint fingerprint,
                                      EntityHomeCallback callback) {
        EntityHome.performRequest(Request.RequestType.setFingerprint,
                fingerprint, callback);
    }
}
