package Home;

import Handler.Response;
import Handler.Request;

public interface EntityHomeCallback {
    /**
     * Called when a server {@link Request} succeeded.
     *
     * @param response
     *            {@link Response} received
     */
    void onResponse(Response<?> response);

    /**
     * Called when a server {@link Request} failed.
     *
     * @param response
     *            {@link Response} received
     */
    void onFailure(Response<?> response);
}
