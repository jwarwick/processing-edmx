package eDMX;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.nio.charset.Charset;

import java.util.Enumeration;
import java.util.List;

public class sACNSocket {

  private final static sACNSocket INSTANCE = new sACNSocket();
  private final static int sdt_acn_port = 5568;
  private static String interfaceName = null;
  private DatagramSocket datagramSocket;

  /**
   * Retrieve the singleton socket.
   */
  public static sACNSocket getInstance() {
    return INSTANCE;
  }

  /**
   * List the network interfaces available.
   */
  public static void listInterfaces() {
      try{
        System.out.println("Network Interfaces:" + "\n");
        for (Enumeration<NetworkInterface> en =
            NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {

          NetworkInterface intf = en.nextElement();
          System.out.println("    " + intf.getName() + " " +
              intf.getDisplayName() + "\n");

          for (Enumeration<InetAddress> enumIpAddr =
              intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {

            String ipAddr = enumIpAddr.nextElement().toString();

            System.out.println("        " + ipAddr + "\n");
          }
        }
      }
      catch(Exception e) {
      }
  }

  /**
   * Bind the datagram socket to a specific network interface.
   * Uses the interface name for lookup (eg: eth1).
   */
  public static void bindByName(String intfname) {
    interfaceName = intfname;
    System.out.println("Setting interfaceName: " + interfaceName);
  }

  /**
   * Create and open the sACN socket.
   * Private. Use the getInstance() method to retrieve the singleton socket.
   */
  private sACNSocket() {
    this.interfaceName = "en1";
    System.out.println("Constructor interfaceName: " + this.interfaceName);
    NetworkInterface netInterface = null;
    try {
      if (null != this.interfaceName)
      {
        System.out.println("Binding to network interface: " + this.interfaceName);
        netInterface = NetworkInterface.getByName(this.interfaceName);
      }
    }
    catch(Exception e) {
      System.out.println("Failed to find network interface: " + this.interfaceName);
    }

    InetAddress netAddress = null;
    System.out.println("network interface: " + netInterface);
    if (null != netInterface)
    {
      netAddress = getFirstIPv4Address(netInterface);
      System.out.println("Binding to address: " + netAddress);
    }
    
    try {
      InetSocketAddress addr = new InetSocketAddress(netAddress, 0);
      this.datagramSocket = new DatagramSocket(addr);
    } 
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private InetAddress getFirstIPv4Address(NetworkInterface iface) {
    List<InterfaceAddress> addresses = iface.getInterfaceAddresses();
    return addresses.get(1).getAddress();
    /* InetAddress addr = InetAddress.getLocalHost(); */
    /* for (Enumeration<InetAddress> enumIpAddr = */
    /*     iface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) { */

    /*   addr = enumIpAddr.nextElement(); */
    /* } */
    /* return addr; */
 }

  /**
   * Close the socket.
   */
  public void close() {
    datagramSocket.close();
  }

  /**
   * Send a sACN packet.
   */
  public void sendPacket(short universe_number, byte start_code, byte[] data, String source_name,
      byte priority, boolean preview_data, byte sequence_number, UUID cid) throws UnknownHostException, IOException {
    
    final InetAddress addr = getUniverseAddress(universe_number);

    final int packet_size = 126 + data.length;
    ByteBuffer buffer = ByteBuffer.allocate(packet_size);
    writeRootLayer(buffer, packet_size, cid);
    writeFramingLayer(buffer, packet_size, source_name, priority, sequence_number, universe_number);
    writeDMPLayer(buffer, packet_size, start_code, data);

    DatagramPacket packet = new DatagramPacket(buffer.array(), packet_size, addr, this.sdt_acn_port);
    this.datagramSocket.send(packet);
  }

  private static final short rl_preamble_size = 0x0010;
  private static final short rl_postamble_size = 0x0000;
  private static final byte[] rl_identifier = {0x41, 0x53, 0x43, 0x2d, 0x45, 
                                               0x31, 0x2e, 0x31, 0x37, 0x00, 
                                               0x00, 0x00};
  private static final byte rl_high = 0x7;
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

  private static final byte fl_high = 0x7;
  private static final int fl_vector = 0x00000002;

  private void writeFramingLayer(ByteBuffer buffer, int packet_size, String source_name, byte priority, 
                                 byte sequence_number, short universe) {
    // framing pdu length starts from octet 38
    final short flags = lengthFlags(packet_size - 38);
    buffer.putShort(flags);
    buffer.putInt(fl_vector);
    byte[] source_bytes;
    try {
      source_bytes = source_name.getBytes("UTF-8");
    }
    catch(UnsupportedEncodingException e) {
      e.printStackTrace();
      return;
    }
    buffer.put(source_bytes);
    // zero-out remainder of source_name field
    byte[] zeros = new byte[64 - source_bytes.length];
    buffer.put(zeros);
    buffer.put(priority);
    buffer.putShort((short)0x0000);
    buffer.put(sequence_number);
    buffer.put((byte)0x00); // XXX - options
    buffer.putShort(universe);
  }

  private static final byte dl_high = 0x7;
  private static final byte dl_vector = 0x02;
  private static final byte dl_type = (byte)0xa1;
  private static final short dl_first = 0x0000;
  private static final short dl_increment = 0x0001;

  private void writeDMPLayer(ByteBuffer buffer, int packet_size, byte start_code, byte[] data) {
    // dmp pdu length starts from octet 115
    final short flags = lengthFlags(packet_size - 115);
    buffer.putShort(flags);
    buffer.put(dl_vector);
    buffer.put(dl_type);
    buffer.putShort(dl_first);
    buffer.putShort(dl_increment);
    // XXX - sanity check data length
    short property_value_count = (short)(1 + data.length);
    buffer.putShort(property_value_count);
    buffer.put(start_code);
    buffer.put(data);
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
    ip[1] = (byte)255;
    ip[2] = (byte)((universe_number & 0xffff) >> 8);
    ip[3] = (byte)(universe_number & 0xff);
    
    InetAddress address = InetAddress.getByAddress(ip);
    return address;
  }

}
