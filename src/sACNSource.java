package eDMX;

import java.net.*;
import java.io.*;
import java.util.UUID;

import processing.core.*;

public class sACNSource {

  private String source_name;
  private UUID source_cid;

  private final byte default_priority = 100;
  private final boolean default_preview_data = false;
  private static final String default_source_name = "Processing";

  private sACNSocket sacn_socket;

  public sACNSource(PApplet applet) {
    this(applet, default_source_name);
  }

  public sACNSource(PApplet applet, String source_name) {
    this(applet, source_name, UUID.randomUUID());
  }

  public sACNSource(PApplet applet, String source_name, UUID source_cid) {
    this.source_name = source_name;
    this.source_cid = source_cid;
    sacn_socket = sACNSocket.getInstance();

    applet.registerMethod("dispose", this);
  }

  public void sendPacket(short universe_number, byte sequence_number, byte[] data) throws UnknownHostException, IOException {
    sendPacket(universe_number, (byte)0, data, default_priority, default_preview_data, sequence_number);
  }

  public void sendPacket(short universe_number, byte start_code, byte[] data, 
      byte priority, boolean preview_data, byte sequence_number) throws UnknownHostException, IOException {
    
    sacn_socket.sendPacket(universe_number, start_code, data, source_name,
        priority, preview_data, sequence_number, source_cid);
  }

  /** 
   * Close the socket. This method is automatically called by Processing when 
   * the PApplet shuts down.
   */
  public void dispose() {
    sacn_socket.close();
  }

}
