package eDMX;

import java.net.*;

import processing.core.*;

public class sACN {

  private static final String default_source_name = "Processing";

  private String source_name;
  private byte priority = 100;
  private boolean preview_data = false;
  private byte sequence_number = 0;
  private short cid = 0x123;

  private DatagramSocket datagramSocket;

  public sACN(PApplet applet) throws SocketException {
    this(applet, default_source_name);
  }

  public sACN(PApplet applet, String source_name) throws SocketException {
    this.source_name = source_name;
    DatagramSocket datagramSocket = new DatagramSocket();

    System.out.println("CREATED sACN object:" + this.source_name);

    applet.registerMethod("dispose", this);
  }

  public void sendPacket(int universe_number, byte start_code, byte[] data) {
    this.sendPacket(universe_number, start_code, data, this.source_name, this.priority, 
        this.preview_data, this.sequence_number, this.cid);
  }

  public void sendPacket(int universe_number, byte start_code, byte[] data, String source_name,
      byte priority, boolean preview_data, byte sequence_number, short cid) {
    
  }

  private InetAddress getUniverseAddress(int universe_number) throws UnknownHostException {
    byte[] ip = new byte[4];
    ip[0] = (byte)239;
    ip[1] = (byte)235;
    ip[2] = (byte)((universe_number & 0xffff) >> 8);
    ip[3] = (byte)(universe_number & 0xff);
    
    InetAddress address = InetAddress.getByAddress(ip);
    return address;
  }

  /** 
   * Close the socket. This method is automatically called by Processing when 
   * the PApplet shuts down.
   *
   * @see sACN#close()
   */
  public void dispose() {
    close();
  }

  /**
   * Close the socket.
   */
  public void close() {
    datagramSocket.close();
  }

}
