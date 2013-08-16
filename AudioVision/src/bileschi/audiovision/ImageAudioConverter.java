package bileschi.audiovision;

import static bileschi.audiovision.SoundFactory.addLists;
import static bileschi.audiovision.SoundFactory.listToByteArr;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class ImageAudioConverter {

  private static final double HALF_STEP = Math.pow(2, 1.0 / 12);
  private static final int NUM_HALF_STEPS_ALLOWED = (int) (12 * Math
      .log(SoundFactory.MAX_FREQUENCY / SoundFactory.MIN_FREQUENCY));

  public static byte[] imageToSound(Bitmap bitmap) {
    List<? super List<Double>> samples = new ArrayList<List<Double>>();
    for (int i = 0; i < bitmap.getWidth(); i++) {
      double percentOfWidth = ((double) i) / bitmap.getWidth();
      for (int j = 0; j < bitmap.getHeight(); j++) {
        if (bitmap.getPixel(i, j) == 0xff000000) {
          double percentOfHeight = ((double) j) / bitmap.getHeight();
          System.out
              .println("This is a thing" + percentToFreq(percentOfHeight));
          List<Double> newSample = SoundFactory.makeWave(WaveType.SINE, 3,
              percentToFreq(percentOfHeight), percentOfWidth);
          samples.add(newSample);
        }
      }
    }
    return listToByteArr(addLists(samples
        .toArray((List<Double>[]) new List[samples.size()])));
  }

  private static double percentToFreq(double percent) {
    return SoundFactory.MIN_FREQUENCY
        * Math.pow(HALF_STEP, ((int) (percent * NUM_HALF_STEPS_ALLOWED)));
  }
}
