package Handler;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;

public class RequestHandler {
    private static Gson gson = GsonFactory.getGsonInstance();

    /**
     * Performs a server request
     *
     * @param request
     *            {@link Request} to be performed
     * @return {@link Response} from the server
     */
    public static Response<?> performRequest(Request<?> request) {
        String json = gson.toJson(request, request.getRequestType());

        String str;
        try {
            str = ConnectionHandler.performRequest(json);
        } catch (IOException e1) {

            return new Response<Void>(Response.Status.failed, e1.getMessage());
        }

        Response<?> response;
        try {
            response = gson.fromJson(str, request.getResponseType());
        } catch (JsonParseException e) {
            return new Response<Void>(Response.Status.jsonError, e.getMessage());
        }

        return response;
    }
}
