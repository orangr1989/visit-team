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

    /**
     * Performs an getFingerprintList request with callback
     */
    public static void getFingerprintList() {
        EntityHome.performRequest(Request.RequestType.getFingerprintList);
    }

    /**
     * Performs an getFingerprintList request with callback
     * @param callback
     *            {@link EntityHomeCallback}
     */
    public static void getFingerprintList(EntityHomeCallback callback) {
        EntityHome.performRequest(Request.RequestType.getFingerprintList, callback);
    }

    /**
     * Performs an deleteAllFingerprint request with callback
     */
    public static void deleteFingerprints() {
        EntityHome.performRequest(Request.RequestType.deleteAllFingerprint);
    }

    /**
     * Performs an deleteAllFingerprint request with callback
     * @param callback
     *            {@link EntityHomeCallback}
     */
    public static void deleteFingerprints(EntityHomeCallback callback) {
        EntityHome.performRequest(Request.RequestType.deleteAllFingerprint, callback);
    }

    /**
     * Performs an deleteFingerprint request with callback
     */
    public static void deleteFingerprint(Fingerprint finger) {
        EntityHome.performRequest(Request.RequestType.deleteAllFingerprint, finger);
    }

    /**
     * Performs an deleteFingerprint request with callback
     * @param callback
     *            {@link EntityHomeCallback}
     */
    public static void deleteFingerprint(Fingerprint finger, EntityHomeCallback callback) {
        EntityHome.performRequest(Request.RequestType.deleteAllFingerprint, finger, callback);
    }
}
