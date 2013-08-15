package bileschi.audiovision;

// originally from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
// and modified by Steve Pomeroy <steve@staticfree.info> and mlbileschi@gmail.com

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends Activity {

  private enum WaveType {
    SQUARE {
      public List<Double> makeWave(int duration, double freqOfTone) {
        List<Double> sample = new ArrayList<Double>();
        int numSamples = duration * SAMPLE_RATE;
        // fill out the array
        for (int i = 0; i < numSamples; i++) {
          double toAdd;
          if (Math.sin(2 * Math.PI * i / (SAMPLE_RATE / freqOfTone)) > 0) {
            toAdd = 1.0;
          } else {
            toAdd = -1.0;
          }

          sample.add(toAdd);
        }
        return sample;
      }
    },

    SINE {
      public List<Double> makeWave(int duration, double freqOfTone) {
        List<Double> sample = new ArrayList<Double>();
        int numSamples = duration * SAMPLE_RATE;
        // fill out the array
        for (int i = 0; i < numSamples; i++) {
          sample.add(Math.sin(2 * Math.PI * i / (SAMPLE_RATE / freqOfTone)));
        }
        return sample;
      }
    };

    public abstract List<Double> makeWave(int duration, double freqOfTone);
  }

  private static final int SAMPLE_RATE = 8000;

  Handler handler = new Handler();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @Override
  protected void onResume() {
    super.onResume();
    // Use a new tread as this can take a while
    final Thread thread = new Thread(new Runnable() {
      public void run() {
        final byte[] soundToPlay = listToByteArr(scaleToListOfShort(addLists(
            toWeightedStereo(WaveType.SINE.makeWave(3, 220), 1.0),
            toWeightedStereo(WaveType.SQUARE.makeWave(3, 554.36), 0.5),
            toWeightedStereo(WaveType.SINE.makeWave(3, 659.26), 0.0))));
        handler.post(new Runnable() {
          public void run() {
            playSound(soundToPlay);
          }
        });
      }
    });
    thread.start();
  }

  public List<Double> addLists(List<Double>... listsToAdd) {
    if (listsToAdd.length == 0) {
      return new ArrayList<Double>();
    }
    int lengthOfAllLists = listsToAdd[0].size();
    for (List<? extends Double> innerList : listsToAdd) {
      // Check that the lengths are all the same.
      if (innerList.size() != lengthOfAllLists) {
        throw new UnsupportedOperationException(
            "Can't add element-wise lists that are not the same size");
      }
    }
    List<Double> toReturn = new ArrayList<Double>();
    for (int i = 0; i < lengthOfAllLists; i++) {
      Double toPush = 0.0;
      for (int j = 0; j < listsToAdd.length; j++) {
        toPush = toPush.doubleValue() + listsToAdd[j].get(i).doubleValue();
      }
      toReturn.add(toPush);
    }
    return toReturn;
  }

  public List<Short> scaleToListOfShort(List<Double> toScale) {
    double maxVal = Collections.max(toScale);
    double scaleFactor = Short.MAX_VALUE / maxVal;
    List<Short> toReturn = new ArrayList<Short>(toScale.size());
    for (Double d : toScale) {
      toReturn.add((short) (d * scaleFactor));
    }
    return toReturn;
  }

  public List<Double> interpolateLeftRight(List<Double> left, List<Double> right) {
    List<Double> toReturn = new ArrayList<Double>();
    if (left.size() != right.size()) {
      throw new UnsupportedOperationException("Cannot interpolate two lists of different length");
    }
    for (int i = 0; i < left.size(); i++) {
      toReturn.add(left.get(i));
      toReturn.add(right.get(i));
    }
    return toReturn;
  }

  /**
   * Right weight is 1-left weight.
   */
  public List<Double> toWeightedStereo(List<Double> sample, double leftWeight) {
    if (leftWeight < 0 || leftWeight > 1) {
      throw new UnsupportedOperationException("Cannot weight a sample left-right with "
          + "that weight. Weights must be between 0 and 1, inclusive.");
    }
    List<Double> toReturn = new ArrayList<Double>(2 * sample.size());
    for (double d : sample) {
      toReturn.add(d * leftWeight);
      toReturn.add(d * (1 - leftWeight));
    }
    return toReturn;
  }

  public byte[] listToByteArr(List<Short> sample) {
    byte[] toReturn = new byte[sample.size() * 2];
    // convert to 16 bit pcm sound array
    // assumes the sample buffer is normalised.
    int idx = 0;
    for (final Short sVal : sample) {
      // in 16 bit wav PCM, first byte is the low order byte
      toReturn[idx++] = (byte) (sVal & 0x00ff);
      toReturn[idx++] = (byte) ((sVal & 0xff00) >>> 8);
    }
    return toReturn;
  }

  void playSound(byte[] soundToPlay) {
    final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
        AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, soundToPlay.length,
        AudioTrack.MODE_STATIC);
    audioTrack.setStereoVolume(1.0f, 1.0f);
    audioTrack.write(soundToPlay, 0, soundToPlay.length);
    audioTrack.play();
  }

}