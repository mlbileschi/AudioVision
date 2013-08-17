package bileschi.audiovision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

//TODO(mlbileschi): make sampling duration something small then replicate it.

public class SoundFactory {

  public static final float MIN_FREQUENCY = 65;
  public static final float MAX_FREQUENCY = 880;
  private static final int SAMPLE_RATE = 8000;

  public static void playSound(byte[] soundToPlay) {
    final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
        SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO,
        AudioFormat.ENCODING_PCM_16BIT, soundToPlay.length,
        AudioTrack.MODE_STATIC);
    audioTrack.setStereoVolume(1.0f, 1.0f);
    audioTrack.write(soundToPlay, 0, soundToPlay.length);
    audioTrack.play();
  }

  public static List<Float> makeWave(WaveType waveType, int duration,
      float frequency, float leftPercentVolume, float overallVolume) {
    return toWeightedStereo(
        waveType.makeWave(SAMPLE_RATE, duration, frequency), leftPercentVolume,
        overallVolume);
  }

  public static List<Float> squareSineMakeWave(WaveType waveType, int duration,
      float frequency, float leftPercentVolume) {
    List<Float> sine = makeWave(WaveType.SINE, duration, frequency,
        leftPercentVolume, 1 - leftWeightToSquareness(leftPercentVolume));
    List<Float> square = makeWave(WaveType.SQUARE, duration, frequency,
        leftPercentVolume, leftWeightToSquareness(leftPercentVolume));
    return addLists(sine, square);
  }

  private static float leftWeightToSquareness(float leftWeight) {
    return (float) (.5 * Math.sin(Math.PI * (leftWeight - .5)) + .5);
  }

  public static List<Float> addLists(List<Float>... listsToAdd) {
    if (listsToAdd.length == 0) {
      return new ArrayList<Float>();
    }
    int lengthOfAllLists = listsToAdd[0].size();
    for (List<? extends Float> innerList : listsToAdd) {
      // Check that the lengths are all the same.
      if (innerList.size() != lengthOfAllLists) {
        throw new UnsupportedOperationException(
            "Can't add element-wise lists that are not the same size");
      }
    }
    List<Float> toReturn = new ArrayList<Float>();
    for (int i = 0; i < lengthOfAllLists; i++) {
      float toPush = 0;
      for (int j = 0; j < listsToAdd.length; j++) {
        toPush += listsToAdd[j].get(i);
      }
      toReturn.add(toPush);
    }
    return toReturn;
  }

  private static List<Short> scaleToListOfShort(List<Float> toScale) {
    float maxVal = Collections.max(toScale);
    float scaleFactor = Short.MAX_VALUE / maxVal;
    List<Short> toReturn = new ArrayList<Short>(toScale.size());
    for (Float d : toScale) {
      toReturn.add((short) (d * scaleFactor));
    }
    return toReturn;
  }

  /**
   * Right weight is 1-left weight.
   */
  public static List<Float> toWeightedStereo(List<Float> sample,
      float leftPercentVolume, float overallVolume) {
    if (leftPercentVolume < 0 || leftPercentVolume > 1 || overallVolume < 0
        || overallVolume > 1) {
      throw new UnsupportedOperationException(
          "Cannot weight a sample left-right with "
              + "that weight. Weights must be between 0 and 1, inclusive.");
    }
    List<Float> toReturn = new ArrayList<Float>(2 * sample.size());
    for (float d : sample) {
      toReturn.add(d * leftPercentVolume * overallVolume);
      toReturn.add(d * (1 - leftPercentVolume) * overallVolume);
    }
    return toReturn;
  }

  public static byte[] listToByteArr(List<Float> sample) {
    List<Short> sampleShorts = scaleToListOfShort(sample);

    byte[] toReturn = new byte[sampleShorts.size() * 2];
    // convert to 16 bit pcm sound array
    // assumes the sample buffer is normalised.
    int idx = 0;
    for (final Short sVal : sampleShorts) {
      // in 16 bit wav PCM, first byte is the low order byte
      toReturn[idx++] = (byte) (sVal & 0x00ff);
      toReturn[idx++] = (byte) ((sVal & 0xff00) >>> 8);
    }
    return toReturn;
  }
}
