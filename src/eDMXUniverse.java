package eDMX;

import java.net.*;
import java.io.*;
import processing.core.*;

public class eDMXUniverse {

  private sACN sacn;
  private short universe_number;
  private int buffer_size = 512;
  private byte[] data_buffer;

  public eDMXUniverse(PApplet applet, short universe_number) throws SocketException {
    this(applet, String.format("Processing Universe %d", universe_number), universe_number);
  }

  public eDMXUniverse(PApplet applet, String source_name, short universe_number) throws SocketException {
    this.sacn = new sACN(applet, source_name);
    this.universe_number = universe_number;
    this.data_buffer = new byte[this.buffer_size];
  }

  public void sendData() throws UnknownHostException, IOException {
    this.sacn.sendPacket(this.universe_number, this.data_buffer);
  }

  public void setSlot(int index, byte value) {
    this.data_buffer[index] = value;
  }

  public void setSlots(int index, byte[] values) {
    for (int offset=0; offset<values.length; offset++) {
      this.data_buffer[index+offset] = values[offset];
    }
  }

}

