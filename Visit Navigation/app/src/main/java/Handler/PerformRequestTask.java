package Handler;

import android.os.AsyncTask;
import android.util.Log;

public class PerformRequestTask extends AsyncTask<Request<?>, Void, Response<?>> {

    private static final String TAG = PerformRequestTask.class.getSimpleName();
    private PerformRequestTaskCallback taskCallback;

    /**
     * Creates a {@link PerformRequestTask} with no
     * {@link PerformRequestTaskCallback}
     */
    public PerformRequestTask() {
    }

    /**
     * Creates a {@link PerformRequestTask} with setting a
     * {@link PerformRequestTaskCallback}
     *
     * @param callback
     *            {@link PerformRequestTaskCallback} for the current task
     */
    public PerformRequestTask(PerformRequestTaskCallback callback) {
        super();
        this.taskCallback = callback;
    }

    /**
     * Creates a {@link PerformRequestTask} by copying the
     * {@link PerformRequestTaskCallback} from task
     *
     * @param task
     *            {@link PerformRequestTask} with
     *            {@link PerformRequestTaskCallback} to be used
     */
    public PerformRequestTask(PerformRequestTask task) {
        super();
        this.taskCallback = task.taskCallback;
    }

    private Request<?> request;

    /**
     * Performs an server request on the background
     *
     * @param params
     *            Request to be performed (only the first is used)
     * @return {@link Response} from the server
     */
    @Override
    protected Response<?> doInBackground(Request<?>... params) {

        request = params[0];

        Response<?> response = RequestHandler.performRequest(request);

        return response;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPostExecute(Response<?> result) {

        if (taskCallback != null) {
            try {
                taskCallback.onPerformedForeground(request, result, this);
            } catch (Exception e) {
                Log.w(TAG, "Callback failed, caught Exception: " + e.getMessage(), e);
            }
        }

        cleanup();
    }

    @Override
    protected void onCancelled() {
        if (taskCallback != null) {
            try {
                taskCallback.onCanceledForeground(request, this);
            } catch (Exception e) {
                Log.w(TAG, "Callback failed, caught Exception: " + e.getMessage(), e);
            }
        }

        cleanup();
    }

    /**
     * Cleans up the references
     */
    private void cleanup() {
        taskCallback = null;
        request = null;
    }
}
