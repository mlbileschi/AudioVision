package bileschi.audiovision;

// originally from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
// and modified by Steve Pomeroy <steve@staticfree.info> and mlbileschi@gmail.com

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

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

    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
        R.drawable.diagline);
    final byte[] soundToPlay = ImageAudioConverter.imageToSound(bitmap);
    // Use a new tread as this can take a while
    final Thread thread = new Thread(new Runnable() {
      public void run() {
        /*
         * List<Double> a = makeWave(WaveType.SINE, 3, 110.0); List<Double> e =
         * makeWave(WaveType.SINE, 3, 659.26); List<Double> cSharp =
         * makeWave(WaveType.SQUARE, 3, 554.36);
         * 
         * final byte[] soundToPlay = listToByteArr(addLists(
         * toWeightedStereo(a, 1.0), toWeightedStereo(cSharp, 0.5),
         * toWeightedStereo(e, 0.0)));
         */
        handler.post(new Runnable() {
          public void run() {
            SoundFactory.playSound(soundToPlay);
          }
        });
      }
    });
    thread.start();
  }

  class BitMapView extends View {
    Bitmap mBitmap = null;

    public BitMapView(Context context, Bitmap bm) {
      super(context);
      mBitmap = bm;
    }

    @Override
    protected void onDraw(Canvas canvas) {
      // called when view is drawn
      Paint paint = new Paint();
      paint.setFilterBitmap(true);
      // The image will be scaled so it will fill the width, and the
      // height will preserve the image’s aspect ration
      double aspectRatio = ((double) mBitmap.getWidth()) / mBitmap.getHeight();
      Rect dest = new Rect(0, 0, this.getWidth(),
          (int) (this.getHeight() / aspectRatio));
      canvas.drawBitmap(mBitmap, null, dest, paint);
    }
  }
}