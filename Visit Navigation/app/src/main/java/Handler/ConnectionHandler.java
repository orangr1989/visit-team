package Handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;

import java.lang.Exception;

public class ConnectionHandler {
    public static int port = 5000;
    public static String host = "10.100.102.8"; // "192.168.52.129" ; // is the host that runs the server side  "10.100.102.8";
    public static String httpProtocol = "http://";

    /**
     * Sends an request to the server
     *
     * @param str
     *            JSON serialized request
     * @return JSON serialized response
     * @throws IOException
     */
    public static String performRequest(String str) throws IOException {

        InetAddress serverAdr;
        serverAdr = Inet4Address.getByName(host);
        Socket socket = null;
        String response = null;
        try {

            socket = new Socket(serverAdr, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket
                    .getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream()));

            out.write(str + "\n");
            out.flush();

            response = in.readLine();

            if (response == null) {
                throw new IOException("no response recieved");
            }
        }
        catch (Exception e) {
            String s = "test";
        }
        finally {
            if (socket != null) {
                socket.close();
            }
        }

        return response;

    }

    public static String getServerURL() {
        return httpProtocol + host;
    }

    public static int getServerPort() {
        return port;
    }
}
