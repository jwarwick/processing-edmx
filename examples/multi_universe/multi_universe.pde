import eDMX.*;

sACNSource source;
sACNUniverse universe1;
sACNUniverse universe2;
byte count = 0;

void setup() {
  source = new sACNSource(this, "Test Source");
  universe1 = new sACNUniverse(source, (short)1);
  universe2 = new sACNUniverse(source, (short)2);
}

void draw() {  
  universe1.setSlot(0, count);
  byte[] dataArray = {0, count, 0, 
                      0, 0, count};
  universe2.setSlots(3, dataArray);
  count++;
  try {
    universe1.sendData();
    universe2.sendData();
  } catch (Exception e) {
    e.printStackTrace();
    exit();
  }
}
