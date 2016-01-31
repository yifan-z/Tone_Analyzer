package tone_analyzer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.AsyncTask;
import android.widget.TextView;

import tone_analyzer.R;

import java.lang.Object;
import org.jtransforms.fft.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private AudioRecord ard = new AudioRecord(MediaRecorder.AudioSource.MIC, 16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 2048);
    private boolean isrecording = false;
    private ArrayList<Integer> freq_final = new ArrayList<Integer>();
    private float mf = 16384f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button start = (Button) findViewById(R.id.button1);
        final Button end = (Button) findViewById(R.id.button2);
        final TextView text1 = (TextView) findViewById(R.id.textView1);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text1.setText("0");
                if (ard.getState() == AudioRecord.STATE_INITIALIZED) {
                }else{
                    ard = new AudioRecord(MediaRecorder.AudioSource.MIC, 16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 2048);
                }
                isrecording = true;
                ard.startRecording();
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ard.getState() == AudioRecord.STATE_INITIALIZED && isrecording) {
                    Log.d("DebugMainActivity", "stop");
                    ard.stop();
                    ard.release();
                    isrecording = false;
                    Integer sum = 0;
                    Integer j = 0;
                    for (Integer i : freq_final) {
                    //only take values larger than 200
                        if (i > 200) {
                            sum += i;
                            j += 1;
                        }
                    }
                    sum = sum / j;
                    freq_final.clear();
                    text1.setText(sum.toString());
                }
            }
        });

    }


  
        protected void onProgressUpdate(short[]... buffer){
            //System.out.println(Arrays.toString(buffer));
            //Log.d("debugOnProgressUpdate", "audio data: " + buffer.toString());
            super.onProgressUpdate(buffer);
            //for (Short var: buffer[0]){
                //Log.d("debugOnProgressUpdate", "audio data: " + var.toString());
            //}
            double temp = mf * mf * 4096 * 4096 / 2d;
            double[] trans = new double[4096];
            double[] mag = new double[1024];
            for (int i = 0; i < 2048; i++){
                trans[2*i] = (double)buffer[0][i];
                trans[2*i+1] = 0;
            }
            DoubleFFT_1D fft = new DoubleFFT_1D(2048);
            fft.complexForward(trans);
            private class backAStask extends AsyncTask<Void, short[], Void> {
        @Override
        protected Void doInBackground(Void... params) {
            while (isrecording) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                short[] buffer = new short[2048];
                ard.read(buffer, 0, 2048);
                publishProgress(buffer);
            }
            return null;
        }

        @Override
            // power spectrum magnitude
            for (int i = 0; i < mag.length; i++){
                mag[i] = 5.0*Math.log10((t[i] * t[i] + t[i+1] * t[i+1]) / temp);
            }

            double max = -100;
            int max_index = 0;
            for (int i = 0; i < 1024; i++){
                if (maxfreq < mag[i]){
                    maxfreq = mag[i];
                    max_i= i;
                }
            }
            int freq = max_i * 16000 / 4096 ;
            if (16000 / 4096 < freq && freq < 16000/2 - 16000 / 4096) {
                int id = max_index;
                double x1 = trans[id-1];
                double x2 = trans[id];
                double x3 = trans[id+1];
                double c = x2;
                double a = (x3+x1)/2 - x2;
                double b = (x3-x1)/2;
                if (a < 0) {
                    double xPeak = -b/(2*a);
                    if (Math.abs(xPeak) < 1) {
                        freq += xPeak * 16000 / 4096;
                        max = (4*a*c - b*b)/(4*a);
                    }
                }
            }
            // round to integer
            freq = Math.round(freq);

            freq_final.add(freq);
        }
    }

}
