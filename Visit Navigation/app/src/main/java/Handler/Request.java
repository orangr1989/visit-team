package Handler;

import java.lang.reflect.Type;
import java.util.List;

import DataModel.*;
import com.google.gson.reflect.TypeToken;

public class Request<D> {

    /**
     * Supported visit request types
     */
    public enum RequestType {
        deleteFingerprint, deleteAllFingerprint, getFingerprintList, setFingerprint, getLocation, getMapList, getPath,
        getBuildingMaps, getBuildings, setMap, removeMap, getLocationList, updateLocation, removeLocation
    }

    private RequestType action;
    private D data;
    private transient Type requestType;
    private transient Type responseType;

    /**
     * Empty constructor is need for deserialization
     */
    protected Request() {
    }

    /**
     * @param action Action to be performed
     * @param data   Data to be submitted with the request
     */
    public Request(RequestType action, D data) {
        this(action);
        this.data = data;
    }

    /**
     * @param action Action to be performed
     */
    public Request(RequestType action) {
        super();
        this.action = action;
        setTypes(action);
    }

    /**
     * @param action Action to be performed
     */
    public void setAction(RequestType action) {
        this.action = action;
        setTypes(action);
    }

    /**
     * @return Action to be performed
     */
    public RequestType getAction() {
        return action;
    }

    /**
     * @param data Data to be submitted with the request
     */
    public void setData(D data) {
        this.data = data;
    }

    /**
     * @return Data to be submitted with the request
     */
    public D getData() {
        return data;
    }

    /**
     * This method is needed for JSON serialization/deserialization
     *
     * @return {@link Type} of the request
     */
    public Type getRequestType() {
        return requestType;
    }

    /**
     * This method is needed for JSON serialization/deserialization
     *
     * @return {@link Type} of the response
     */
    public Type getResponseType() {
        return responseType;
    }

    private static Type fingerprintRequestType;
    private static Type fingerprintResponseType;
    private static Type fingerprintListResponseType;
    private static Type measurementRequestType;
    private static Type locationResponseType;
    private static Type voidRequestType;
    private static Type voidResponseType;
    private static Type mapListResponseType;
    private static Type mapRequestType;
    private static Type mapResponseType;
    private static Type locationRequestType;
    private static Type locationListResponseType;

    /**
     * Setups the proper types for the action to be performed
     *
     * @param t Action to be performed
     */
    private void setTypes(RequestType t) {
        switch (t) {
            case deleteFingerprint:
                if (fingerprintRequestType == null) {
                    fingerprintRequestType = new TypeToken<Request<Fingerprint>>() {
                    }.getType();
                }
                requestType = fingerprintRequestType;

                if (voidResponseType == null) {
                    voidResponseType = new TypeToken<Response<Void>>() {
                    }.getType();
                }
                responseType = voidResponseType;
                break;

            case deleteAllFingerprint:
                if (voidRequestType == null) {
                    voidRequestType = new TypeToken<Request<Void>>() {
                    }.getType();
                }
                requestType = voidRequestType;

                if (voidResponseType == null) {
                    voidResponseType = new TypeToken<Response<Void>>() {
                    }.getType();
                }
                responseType = voidResponseType;
                break;

            case getFingerprintList:
                if (voidRequestType == null) {
                    voidRequestType = new TypeToken<Request<Void>>() {
                    }.getType();
                }
                requestType = voidRequestType;

                if (fingerprintListResponseType == null) {
                    fingerprintListResponseType = new TypeToken<Response<List<Fingerprint>>>() {
                    }.getType();
                }
                responseType = fingerprintListResponseType;
                break;

            case setFingerprint:
                if (fingerprintRequestType == null) {
                    fingerprintRequestType = new TypeToken<Request<Fingerprint>>() {
                    }.getType();
                }
                requestType = fingerprintRequestType;

                if (fingerprintResponseType == null) {
                    fingerprintResponseType = new TypeToken<Response<Fingerprint>>() {
                    }.getType();
                }
                responseType = fingerprintResponseType;
                break;

            case getLocation:
                if (measurementRequestType == null) {
                    measurementRequestType = new TypeToken<Request<Measurement>>() {
                    }.getType();
                }
                requestType = measurementRequestType;

                if (locationResponseType == null) {
                    locationResponseType = new TypeToken<Response<Location>>() {
                    }.getType();
                }
                responseType = locationResponseType;
                break;
            case getMapList:
                if (voidRequestType == null) {
                    voidRequestType = new TypeToken<Request<Void>>() {
                    }.getType();
                }
                requestType = voidRequestType;

                if (mapListResponseType == null) {
                    mapListResponseType = new TypeToken<Response<List<Map>>>() {
                    }.getType();
                }
                responseType = mapListResponseType;
                break;
            case setMap:
                if (mapRequestType == null) {
                    mapRequestType = new TypeToken<Request<Map>>() {
                    }.getType();
                }
                requestType = mapRequestType;

                if (mapResponseType == null) {
                    mapResponseType = new TypeToken<Response<Map>>() {
                    }.getType();
                }
                responseType = mapResponseType;
                break;
            case removeMap:
                if (mapRequestType == null) {
                    mapRequestType = new TypeToken<Request<Map>>() {
                    }.getType();
                }
                requestType = mapRequestType;

                if (voidResponseType == null) {
                    voidResponseType = new TypeToken<Response<Void>>() {
                    }.getType();
                }
                responseType = voidResponseType;
                break;
            case getLocationList:
                if (voidRequestType == null) {
                    voidRequestType = new TypeToken<Request<Void>>() {
                    }.getType();
                }
                requestType = voidRequestType;

                if (locationListResponseType == null) {
                    locationListResponseType = new TypeToken<Response<List<Location>>>() {
                    }.getType();
                }
                responseType = locationListResponseType;
                break;
            case updateLocation:
                if (locationRequestType == null) {
                    locationRequestType = new TypeToken<Request<Location>>() {
                    }.getType();
                }
                requestType = locationRequestType;

                if (voidResponseType == null) {
                    voidResponseType = new TypeToken<Response<Void>>() {
                    }.getType();
                }
                responseType = voidResponseType;
                break;
            case removeLocation:
                if (locationRequestType == null) {
                    locationRequestType = new TypeToken<Request<Location>>() {
                    }.getType();
                }
                requestType = locationRequestType;

                if (voidResponseType == null) {
                    voidResponseType = new TypeToken<Response<Void>>() {
                    }.getType();
                }
                responseType = voidResponseType;
                break;

            default:
                throw new RuntimeException(
                        "Need to implement Request#setTypes() for all request types");
        }
    }
}