package bileschi.audiovision;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

public class ImageFactory {

  public static Bitmap createBitmap() {
    Bitmap b = Bitmap.createBitmap(10, 10, Config.RGB_565);
    b.setPixel(0, 0, 0);
    return b;
  }

  public static String hex(int n) {
    // call toUpperCase() if that's required
    return String.format("0x%8s", Integer.toHexString(n)).replace(' ', '0');
  }

  public static void readFile(Resources r) {

    Bitmap bitmap = BitmapFactory.decodeResource(r, R.drawable.onebyoneblack);

    int pixelVal = bitmap.getPixel(0, 0);
    System.out.println("HERE IS A THING" + hex(pixelVal));
  }
}
