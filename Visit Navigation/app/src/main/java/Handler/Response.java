package Handler;

public class Response<D> {

    /**
     * Different status the server reports back to the client
     *
     */
    public enum Status {
        ok, failed, warning, jsonError
    }

    private Status status;
    private String message;
    private D data;

    /**
     * Empty constructor is need for deserialization
     */
    protected Response() {
    }

    /**
     *
     * @param status
     *            Response {@link Status}
     * @param message
     *            Message from the server
     */
    public Response(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     *
     * @param status
     *            Response {@link Status}
     * @param message
     *            Message from the server
     * @param data
     *            Data returned by the server
     */
    public Response(Status status, String message, D data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    /**
     *
     * @param status
     *            Response {@link Status}
     * @param data
     *            Data returned by the server
     */
    public Response(Status status, D data) {
        this.status = status;
        this.data = data;
    }

    /**
     *
     * @param status
     *            Response {@link Status}
     */
    public Response(Status status) {
        this.status = status;
    }

    /**
     *
     * @return {@link Status}
     */
    public Status getStatus() {
        return status;
    }

    /**
     *
     * @return Message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @return Data
     */
    public D getData() {
        return data;
    }

}
