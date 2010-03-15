package atan.test;

//~--- non-JDK imports --------------------------------------------------------

import atan.model.ByteBuffer;

//~--- JDK imports ------------------------------------------------------------

import java.net.DatagramSocket;

/**
 *
 * @author Atan
 */
public class PingPongServer extends UDPServer {
    private int            counter = 0;
    private ByteBuffer     buf     = new ByteBuffer(100);
    private DatagramSocket socket  = null;

    /**
     *
     * @param msg
     * @return
     */
    @Override
    public String respondTo(String msg) {
        System.out.println("received: " + msg);
        return "hallo client";
    }

    /**
     *
     * @param arg
     */
    public static void main(String[] arg) {
        try {
            PingPongServer server = new PingPongServer();
            server.start(4000);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}