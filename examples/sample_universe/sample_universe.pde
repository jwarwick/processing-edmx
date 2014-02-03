import eDMX.*;

sACNSource source;
sACNUniverse universe1;
byte count = 0;

void setup() {
  source = new sACNSource(this, "Test Source");
  universe1 = new sACNUniverse(source, (short)1);
}

void draw() {  
  universe1.fillSlots(count++);
  try {
    universe1.sendData();
  } catch (Exception e) {
    e.printStackTrace();
    exit();
  }
}
