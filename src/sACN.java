package eDMX;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.UUID;

import processing.core.*;

public class sACN {

  private static final String default_source_name = "Processing";
  private static final int sdt_acn_port = 5568;

  private String source_name;
  private byte priority = 100;
  private boolean preview_data = false;
  private byte sequence_number = 0;
  private UUID cid = UUID.randomUUID();

  private DatagramSocket datagramSocket;

  public sACN(PApplet applet) throws SocketException {
    this(applet, default_source_name);
  }

  public sACN(PApplet applet, String source_name) throws SocketException {
    this.source_name = source_name;
    this.datagramSocket = new DatagramSocket();

    System.out.println("CREATED sACN object:" + this.source_name);

    applet.registerMethod("dispose", this);
  }

  public void sendPacket(int universe_number, byte start_code, byte[] data) throws UnknownHostException, IOException {
    this.sendPacket(universe_number, start_code, data, this.source_name, this.priority, 
        this.preview_data, this.sequence_number, this.cid);
  }


  public void sendPacket(int universe_number, byte start_code, byte[] data, String source_name,
      byte priority, boolean preview_data, byte sequence_number, UUID cid) throws UnknownHostException, IOException {
    
    final InetAddress addr = getUniverseAddress(universe_number);
    System.out.println("Sending to ip: " + addr.getHostAddress() + ":" + this.sdt_acn_port);

    final int packet_size = 126 + data.length;
    ByteBuffer buffer = ByteBuffer.allocate(packet_size);
    writeRootLayer(buffer, packet_size, cid);

    DatagramPacket packet = new DatagramPacket(buffer.array(), packet_size, addr, this.sdt_acn_port);
    this.datagramSocket.send(packet);
  }

  private static final short rl_preamble_size = 0x0010;
  private static final short rl_postamble_size = 0x0000;
  private static final byte[] rl_identifier = {0x41, 0x53, 0x43, 0x2d, 0x45, 
                                               0x31, 0x2e, 0x31, 0x37, 0x00, 
                                               0x00, 0x00};
  private static final byte rl_high = 0x07;
  private static final int rl_vector = 0x00000004;

  private void writeRootLayer(ByteBuffer buffer, final int packet_size, final UUID cid) {
    buffer.putShort(rl_preamble_size);
    buffer.putShort(rl_postamble_size);
    buffer.put(rl_identifier);
    // root pdu length starts from octet 16
    final short flags = lengthFlags(packet_size - 16);
    buffer.putShort(flags);
    buffer.putInt(rl_vector);
    buffer.putLong(cid.getMostSignificantBits());
    buffer.putLong(cid.getLeastSignificantBits());
  }

  private short lengthFlags(final int packet_size) {
    // low 12 bits = pdu length, high 4 bits = 0x7
    int flags = 0x7;
    flags = flags << 12;
    flags = flags | (packet_size & 0x0fff);
    return (short)flags;
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
