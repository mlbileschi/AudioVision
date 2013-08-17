package bileschi.audiovision;

// originally from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
// and modified by Steve Pomeroy <steve@staticfree.info> and mlbileschi@gmail.com

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends Activity {

  Handler handler = new Handler();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @Override
  protected void onResume() {
    super.onResume();

    Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),
        R.drawable.topleft);
    Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),
        R.drawable.middle);
    Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(),
        R.drawable.bottomright);
    final byte[] soundToPlay1 = ImageAudioConverter.imageToSound(bitmap1);
    final byte[] soundToPlay2 = ImageAudioConverter.imageToSound(bitmap2);
    final byte[] soundToPlay3 = ImageAudioConverter.imageToSound(bitmap3);

    // Use a new thread as this can take a while
    final Thread thread = new Thread(new Runnable() {
      public void run() {
        handler.post(new Runnable() {
          public void run() {
            AudioTrack audioTrack = SoundFactory
                .newAudioTrack(soundToPlay2.length);
            audioTrack.play();
            SoundFactory.addToAudioTrack(soundToPlay1, audioTrack, 0);
            SoundFactory.addToAudioTrack(soundToPlay2, audioTrack, 0);
            SoundFactory.addToAudioTrack(soundToPlay3, audioTrack, 0);
          }
        });
      }
    });
    thread.start();
  }
}