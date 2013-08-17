package bileschi.audiovision;

import static bileschi.audiovision.SoundFactory.addLists;
import static bileschi.audiovision.SoundFactory.listToByteArr;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ImageAudioConverter {

  private static final double HALF_STEP = Math.pow(2, 1.0 / 12);
  private static final int NUM_HALF_STEPS_ALLOWED = (int) (12 * Math
      .log(SoundFactory.MAX_FREQUENCY / SoundFactory.MIN_FREQUENCY));
  private static final int DURATION = 2;

  public static byte[] imageToSound(Bitmap bitmap) {
    List<? super List<Float>> samples = new ArrayList<List<Float>>();

    System.out.println(bitmap.getHeight() + " height thing "
        + bitmap.getWidth());
    for (int i = 0; i < bitmap.getWidth(); i++) {
      float percentOfWidth = ((float) i) / bitmap.getWidth();

      for (int j = 0; j < bitmap.getHeight(); j++) {
        if (bitmap.getPixel(i, j) == Color.BLACK) {
          System.out.println("Value at i, j  ! " + i + ", " + j + " "
              + hex(bitmap.getPixel(i, j)));
          float percentOfHeight = ((float) j) / bitmap.getHeight();
          System.out
              .println("This is a thing" + percentToFreq(percentOfHeight));
          List<Float> newSample = SoundFactory.squareSineMakeWave(
              WaveType.SINE, DURATION, percentToFreq(percentOfHeight),
              percentOfWidth);
          samples.add(newSample);

        }
      }
    }
    bitmap.recycle();
    return listToByteArr(addLists(samples
        .toArray((List<Float>[]) new List[samples.size()])));
  }

  public static String hex(int n) {
    // call toUpperCase() if that's required
    return String.format("0x%8s", Integer.toHexString(n)).replace(' ', '0');
  }

  private static float percentToFreq(float percent) {
    int semitonesAboveMin = (int) (percent * NUM_HALF_STEPS_ALLOWED);
    int octavesAboveMin = semitonesAboveMin / 12;
    int semitonesAboveOctave = semitonesAboveMin % 12;

    return (float) (SoundFactory.MIN_FREQUENCY * Math.pow(2, octavesAboveMin) * Math
        .pow(HALF_STEP, semitonesAboveOctave));
  }
}
