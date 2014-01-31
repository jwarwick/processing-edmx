package eDMX;

import java.net.*;
import java.io.*;
import java.util.UUID;

import processing.core.*;

public class sACN {

  private static final String default_source_name = "Processing";

  private String source_name;
  private byte priority = 100;
  private boolean preview_data = false;
  private byte sequence_number = 0;
  private UUID cid = UUID.randomUUID();

  private sACNSocket sacn_socket;

  public sACN(PApplet applet) throws SocketException {
    this(applet, default_source_name);
  }

  public sACN(PApplet applet, String source_name) throws SocketException {
    this.source_name = source_name;
    this.sacn_socket = sACNSocket.getInstance();

    applet.registerMethod("dispose", this);
  }

  public void sendPacket(short universe_number, byte[] data) throws UnknownHostException, IOException {
    this.sendPacket(universe_number, (byte)0, data, this.source_name, this.priority, 
        this.preview_data, this.sequence_number, this.cid);
  }

  public void sendPacket(short universe_number, byte start_code, byte[] data, String source_name,
      byte priority, boolean preview_data, byte sequence_number, UUID cid) throws UnknownHostException, IOException {
    
    this.sacn_socket.sendPacket(universe_number, start_code, data, source_name,
        priority, preview_data, sequence_number, cid);

    // XXX - the sequence number should be per-universe
    this.sequence_number++;
  }

  /** 
   * Close the socket. This method is automatically called by Processing when 
   * the PApplet shuts down.
   */
  public void dispose() {
    this.sacn_socket.close();
  }

}
