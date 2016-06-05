package Handler;

public interface PerformRequestTaskCallback {

    /**
     * Runs in UI thread
     *
     * @param request
     *            {@link Request}
     * @param response
     *            {@link Response}
     * @param task
     *            The {@link PerformRequestTask} that is calling the callback
     */
    void onPerformedForeground(Request<?> request, Response<?> response,
                               PerformRequestTask task);

    /**
     * Runs in UI thread
     *
     * @param request
     * @param task
     *            The {@link PerformRequestTask} that is calling the callback
     */
    void onCanceledForeground(Request<?> request, PerformRequestTask task);
}