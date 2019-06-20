package com.techticz.powerkit.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.techticz.powerkit.R;

public class SeekBarWithValues extends RelativeLayout {

    private int mMax = 100;
    private int mMin = 0;
    private TextView mMinText;
    private TextView mMaxText;
    private TextView mCurrentText;
    private SeekBar mSeek;
    private SeekBarValueChangeListner mListner;

    public SeekBarWithValues(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(
                R.layout.seek_bar_with_values, this);
        // the minimum value is always 0
        mMinText = (TextView) findViewById(R.id.minValue);
        mMinText.setText("0");
        mMaxText = (TextView) findViewById(R.id.maxValue);
        mCurrentText = (TextView) findViewById(R.id.curentValue);
        mSeek = (SeekBar) findViewById(R.id.seekBar);
        mSeek.setMax(100);
        mMaxText.setText(String.valueOf(mSeek.getMax()));
        mSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mListner != null) {
                    mListner.onSeekbarValueChanged(progress + mMin);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setSeekbarValueChangedListner(SeekBarValueChangeListner listner){
        mListner = listner;
    }
    /**
     * This needs additional work to make the current progress text stay
     * right under the thumb drawable.
     *
     * @param newProgress
     *            the new progress for which to place the text
     */
    public void updateCurrentProgress(int newProgress) {
        mSeek.setProgress(newProgress-mMin);
       /* mCurrentText.setText(String.valueOf(newProgress));
        final int padding = mMinText.getWidth() + mSeek.getPaddingLeft();
        final int totalSeekWidth = mSeek.getWidth();
        final RelativeLayout.LayoutParams lp = (LayoutParams) mCurrentText
                .getLayoutParams();
        final int seekLocation = (mSeek.getProgress() * totalSeekWidth)
                / mMax - mCurrentText.getWidth() / 2;
        lp.leftMargin = seekLocation + padding;
        mCurrentText.setLayoutParams(lp);*/
    }

    public SeekBar getSeekBar() {
        return mSeek;
    }



    public void setMax(int i) {
        mMax = i;
        mMaxText.setText(""+mMax);
        mSeek.setMax(mMax-mMin);
    }
    public void setMin(int i) {
        mMin = i;
        mMinText.setText(""+mMin);
        mSeek.setMax(mMax-mMin);
    }

    public int getMin() {
        return mMin;
    }

    public int getMax() {
        return mMax;
    }

    interface SeekBarValueChangeListner{
        public void onSeekbarValueChanged(int value);
    }

    @Override
    public void setEnabled(boolean enable){
        super.setEnabled(enable);
        mSeek.setEnabled(enable);
    }

}
