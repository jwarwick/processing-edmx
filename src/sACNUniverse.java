package eDMX;

import java.net.*;
import java.io.*;
import java.util.Arrays;

public class sACNUniverse {

  private sACNSource source;
  private short universe_number;
  private byte sequence_number = 0;

  private int buffer_size = 512;
  private byte[] data_buffer;

  public sACNUniverse(sACNSource source, short universe_number) {
    this.source = source;
    this.universe_number = universe_number;
    data_buffer = new byte[buffer_size];
  }

  public void sendData() throws UnknownHostException, IOException {
    source.sendPacket(universe_number, sequence_number++, data_buffer);
  }

  public void setSlot(int index, byte value) {
    data_buffer[index] = value;
  }

  public void setSlots(int index, byte[] values) {
    for (int offset=0; offset<values.length; offset++) {
      data_buffer[index+offset] = values[offset];
    }
  }

  public void fillSlots(byte value) {
    Arrays.fill(data_buffer, value);
  }

}

