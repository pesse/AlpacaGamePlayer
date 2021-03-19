import org.junit.jupiter.api.Test;

import java.awt.*;

public class BitShifting {

    private String byteString(byte b) {
        return Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
    }

    @Test
    void shift() {
        int rgbaInt = -14801082;

        var c2 = new Color(30, 39, 70);
        var color = new Color(255, 0, 0, 255);

        int rgba = color.getRGB();

        byte[] argb2 = { 127, 127, -128, -128 };

        System.out.println(rgba);
        System.out.println(0xffff0000);

        System.out.println(argb2[0] + ": " + byteString(argb2[0]));
        System.out.println((argb2[0]&0xff) + ": " + byteString(Integer.valueOf(argb2[0]&0xff).byteValue()));
        System.out.println((argb2[0]<<8));
        System.out.println((argb2[0]<<8));
        System.out.println(((argb2[0]&0xff)<<24) + ": " + byteString(new Integer((argb2[0]&0xff)<<24).byteValue()));

        int argbInt =
                ((argb2[0]&0xff) << 24) |
                ((argb2[1]&0xff) << 16) |
                ((argb2[2]&0xff) <<  8) |
                ((argb2[3]&0xff)      );

        System.out.println(argbInt);
    }
}
