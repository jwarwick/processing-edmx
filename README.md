# Processing eDMX Library
A [Processing](http://www.processing.org/) library to send DMX data over ethernet using [Streaming ACN](http://en.wikipedia.org/wiki/Architecture_for_Control_Networks) (ANSI E1.31).

## How to Install
Extract the contents of `bin/eDMX.zip` into the Processing `libraries` folder. See also [How to Install a Contributed Library](http://wiki.processing.org/w/How_to_Install_a_Contributed_Library).

## How to Build
```
% make clean
% make
% make package
```

The zip file will now be at `bin/eDMX.zip`

## How to Use
See the demo applications in the `examples` folder. These are also available in Processing under the `File->Examples...` menu.

``` java
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
```

## License
All code is MIT licensed. See the file LICENSE for details.


