package bileschi.audiovision;

public enum WaveType {
  SQUARE {
    public float[] makeWave(int sampleRate, int duration, float freqOfTone) {
      int numSamples = duration * sampleRate;
      float[] sample = new float[numSamples];
      System.out.println("makewave wavetype length : " + numSamples);

      float k = (sampleRate / freqOfTone);
      // fill out the array
      for (int i = 0; i < numSamples; i++) {
        float toAdd;
        float period = (float) .5 * k;
        if (i % period < period / 2) {
          toAdd = 1;
        } else {
          toAdd = -1;
        }
        sample[i] = toAdd;
      }
      return sample;
    }
  },

  SINE {
    public float[] makeWave(int sampleRate, int duration, float freqOfTone) {
      int numSamples = duration * sampleRate;
      float[] sample = new float[numSamples];
      float sineFreq = (float) (2 * Math.PI / (sampleRate / freqOfTone));
      // fill out the array
      for (int i = 0; i < numSamples; i++) {
        sample[i] = (float) Math.sin(i * sineFreq);
      }
      return sample;
    }
  };

  public abstract float[] makeWave(int sampleRate, int duration,
      float freqOfTone);
}