package Home;

import android.util.Log;

import java.util.HashMap;

import Handler.PerformRequestTask;
import Handler.PerformRequestTaskCallback;
import Handler.Request;
import Handler.Response;
import android.os.AsyncTask;

public class EntityHome implements PerformRequestTaskCallback {
    protected static EntityHome instance = new EntityHome();

    protected HashMap<PerformRequestTask, Request<?>> pending = new HashMap<PerformRequestTask, Request<?>>();
    protected HashMap<Request<?>, Integer> retryCount = new HashMap<Request<?>, Integer>();
    protected HashMap<Request<?>, EntityHomeCallback> callbacks = new HashMap<Request<?>, EntityHomeCallback>();

    private static final String TAG = EntityHome.class.getName();

    public static final Integer MAX_TRIES = 3;

    /**
     * Performs an server request. This method must be invoked on the UI thread.
     *
     * @param action
     *            Action to be performed
     * @param callback
     *            {@link EntityHomeCallback}
     */
    public static void performRequest(Request.RequestType action,
                                      EntityHomeCallback callback) {
        performRequest(action, null, callback);
    }

    /**
     * Performs an server request . This method must be invoked on the UI
     * thread.
     *
     * @param action
     *            Action to be performed
     */
    public static void performRequest(Request.RequestType action) {
        performRequest(action, null);
    }

    /**
     * Performs an server request. This method must be invoked on the UI thread.
     *
     * @param <D>
     *            Data type the request contains
     * @param action
     *            Action to be performed
     * @param data
     *            Data to be submitted with the request
     */
    public static <D> void performRequest(Request.RequestType action, D data) {
        performRequest(action, data, null);
    }

    /**
     * Performs an server request. This method must be invoked on the UI thread
     * (as it invokes {@link AsyncTask#execute(Object...)}
     *
     * @param <D>
     *            Data type the request contains
     * @param action
     *            Action to be performed
     * @param data
     *            Data to be submitted with the request
     * @param callback
     *            {@link EntityHomeCallback} to be called after the
     *            request
     */
    public static <D> void performRequest(Request.RequestType action, D data,
                                          EntityHomeCallback callback) {

        PerformRequestTask task;
        Request<D> request = new Request<D>(action, data);
        task = new PerformRequestTask(instance);

        startTask(task, request, callback);

    }

    /**
     * Starts an asynchronous server request and adds it to the pending list.
     *
     * @param task
     *            {@link PerformRequestTask} to be started
     * @param request
     *            {@link Request} to be performed
     * @param callback
     *            {@link EntityHomeCallback}
     */
    protected static void startTask(PerformRequestTask task,
                                    Request<?> request, EntityHomeCallback callback) {
        instance.pending.put(task, request);
        if (callback != null) {
            instance.callbacks.put(request, callback);
        }
        task.execute(request);
    }

    /**
     * Removes a task form the pending list
     *
     * @param task
     *            {@link PerformRequestTask} to be removed
     */
    protected static void removeTask(PerformRequestTask task) {
        Request<?> r = instance.pending.remove(task);
        if (r == null) {
            Log.e(TAG, "removeTask tried to remove task which was not present");
            return;
        }

        instance.callbacks.remove(r);

    }

    /**
     * Restarts a failed server request.
     *
     * @param task
     *            {@link PerformRequestTask} to be restarted
     */
    protected static void restartTask(PerformRequestTask task) {
        Request<?> r = instance.pending.remove(task);
        if (r == null) {
            Log
                    .e(TAG,
                            "restartTask tried to remove task which was not present");
            return;
        }

        PerformRequestTask newTask = new PerformRequestTask(task);
        instance.pending.put(newTask, r);
        newTask.execute(r);

    }

    private static MapHome mapHome;
    private static LocationHome locationHome;
    private static FingerprintHome fingerprintHome;

    /**
     * Returns the Entity RemoteEntityHome that is responsible for the request.
     *
     * @param request
     *            {@link Request}
     * @return RemoteEntityHome for specific request
     */
    public static IEntityHome getRemoteEntityHome(Request<?> request) {
        return getRemoteEntityHome(request.getAction());
    }

    /**
     * Returns the Entity RemoteEntityHome that is responsible for an action.
     *
     * @param type
     *            Action
     * @return RemoteEntityHome for specific request type
     */
    public static IEntityHome getRemoteEntityHome(Request.RequestType type) {
        switch (type) {
            case setMap:
            case getMapList:
            case removeMap:
                if (mapHome == null) {
                    mapHome = new MapHome();
                }

                return mapHome;

            case getLocation:
            case getLocationList:
            case removeLocation:
            case updateLocation:

                if (locationHome == null) {
                    locationHome = new LocationHome();
                }

                return locationHome;

            case setFingerprint:

                if (fingerprintHome == null) {
                    fingerprintHome = new FingerprintHome();
                }

                return fingerprintHome;

            default:
                throw new IllegalArgumentException("No RemoteEntityHome for type "
                        + type);
        }
    }

    /**
     *
     * @param response
     *            {@link Response} received from the server
     * @return <code>true</code> if request was successful
     */
    private static boolean isSuccess(Response<?> response) {
        return response.getStatus() == Response.Status.ok;
    }

    /**
     * Calls the {@link EntityHomeCallback} if request was successful,
     * otherwise retries to perform the request.
     *
     * @see PerformRequestTaskCallback#onPerformedForeground(Request, Response,
     *      PerformRequestTask)
     */
    public void onPerformedForeground(Request<?> request, Response<?> response,
                                      PerformRequestTask task) {
        EntityHomeCallback cb = callbacks.get(request);

        if (isSuccess(response)) {
            if (cb != null) {
                cb.onResponse(response);
            }
        } else {

            Integer i = retryCount.get(request);
            if (i != null) {
                i++;
            } else {
                i = 1;
            }

            if (i < MAX_TRIES) {
                retryCount.put(request, i);
                restartTask(task);
                return;

            } else {
                retryCount.remove(request);
                if (cb != null) {
                    cb.onFailure(response);
                }
            }

        }

        removeTask(task);

    }

    /**
     * Retries to perform the canceled request
     *
     * @see PerformRequestTaskCallback#onCanceledForeground(Request,
     *      PerformRequestTask)
     */
    public void onCanceledForeground(Request<?> request, PerformRequestTask task) {
        restartTask(task);
    }

    public static HashMap<PerformRequestTask, Request<?>> getPending() {
        return instance.pending;
    }

    public static HashMap<Request<?>, EntityHomeCallback> getCallbacks() {
        return instance.callbacks;
    }

    public static HashMap<Request<?>, Integer> getRetryCount() {
        return instance.retryCount;
    }

    public static void clear() {
        instance.pending.clear();
        instance.retryCount.clear();
        instance.callbacks.clear();
    }

    public static EntityHome getInstance() {
        return instance;
    }

}
