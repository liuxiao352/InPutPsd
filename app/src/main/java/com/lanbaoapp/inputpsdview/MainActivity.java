package com.lanbaoapp.inputpsdview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.lanbaoapp.inputpsdview.widget.InputPsdView;

public class MainActivity extends AppCompatActivity {
    private InputPsdView mInputPsdView;
    private Button mBtn;
    private SeekBar mSeekBar,mSeekBar2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInputPsdView = (InputPsdView) findViewById(R.id.input);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar2 = (SeekBar) findViewById(R.id.seekBar2);
        mBtn = (Button) findViewById(R.id.btn);
        mInputPsdView.setOnInputFinishListener(new InputPsdView.OnInputFinishListener() {
            @Override
            public void inputFinish(String str) {
                Toast.makeText(MainActivity.this, "输入完成 : " + str, Toast.LENGTH_SHORT).show();
            }
        });
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInputPsdView.getIsExpress()) {
                    mInputPsdView.setExpress(1);
                    mBtn.setText("明文");
                }else{
                    mInputPsdView.setExpress(0);
                    mBtn.setText("点点");
                }
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mInputPsdView.setItemWidth(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mInputPsdView.setFillet(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
