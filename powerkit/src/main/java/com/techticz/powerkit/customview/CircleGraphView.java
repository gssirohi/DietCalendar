package com.techticz.powerkit.customview;

import android.content.Context;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.techticz.powerkit.R;


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 27/9/17.
 */

public class CircleGraphView extends FrameLayout implements View.OnClickListener{

    private DecoView mDecoView;
    private TextView mTvValue;
    private TextView mTvLabel;
    private float mDataSeriesMax = 50f;
    private int mBackIndex;
    private int mDataSeriesIndex;
    private DecoEvent mDataEvent;
    private TextView mTvMax;
    private String mLabel;
    private int mColor;
    private int mCompletedSeriesIndex;

    public CircleGraphView(@NonNull Context context) {
        super(context);
        initView();
    }

    public CircleGraphView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CircleGraphView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void initView() {
        ViewGroup view = (ViewGroup) View.inflate(getContext(), R.layout.circle_graph_view,null);
        addView(view);
        mDecoView = (DecoView)findViewById(R.id.dynamicArcView);
        mTvValue = (TextView)findViewById(R.id.tv_graph_circle_value);
        mTvMax = (TextView)findViewById(R.id.tv_graph_circle_max);
        mTvLabel = (TextView)findViewById(R.id.tv_circle_graph_bottom);
        createBackSeries();
        createCompletedSeries();
        mDecoView.addEvent(new DecoEvent.Builder(100)
                .setIndex(mBackIndex)
                .setDuration(2000)
                .setDelay(100)
                .build());
        setOnClickListener(this);
        mColor = getResources().getColor(R.color.primaryColor);
    }

    public void start(String label,float max,float value,int color){
        if(color == 0){
            mColor = getResources().getColor(R.color.primaryColor);
        }else{
            mColor = color;
        }
        mTvLabel.setText(label);
        mLabel = label;
        if(max == 0){
            max = 100;
        }
        mDataSeriesMax = max;
        mTvMax.setText(""+(int)max);
        createDataSeries();
        createEvent(value);
        replay();
    }

    private void replay() {
        mDecoView.addEvent(mDataEvent);
    }

    public void play(float value) {
        createEvent(value);
        mDecoView.addEvent(mDataEvent);
        if(value == mDataSeriesMax){
            mDecoView.addEvent(getCompletedEvent());
        }
    }

    private DecoEvent getCompletedEvent() {
        DecoEvent event = new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_EXPLODE)
                .setIndex(mCompletedSeriesIndex)
                .setDisplayText("Nailed It!")
                .setDuration(2000)
                .setEffectRotations(1)
                .setDelay(3500)
                .build();
        return event;
    }

    private void createEvent(float value) {
        //DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT
        mDataEvent = new DecoEvent.Builder(value)
                .setIndex(mDataSeriesIndex)
                .setDuration(3000)
                .setEffectRotations(1)
                .setDelay(500)
                .build();
    }

    private void createBackSeries() {
        SeriesItem seriesItem = new SeriesItem.Builder(getResources().getColor(R.color.secondaryColor))
                .setRange(0, mDataSeriesMax, 0)
                .setInitialVisibility(true)
                .build();

        mBackIndex = mDecoView.addSeries(seriesItem);
    }

    private void createDataSeries() {
        final SeriesItem seriesItem = new SeriesItem.Builder(mColor)
                .setRange(0, mDataSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                mTvLabel.setText(mLabel+" ("+String.format("%.0f%%", percentFilled * 100f)+")");
                if(currentPosition < 0){
                    currentPosition = 0;
                }
                mTvValue.setText(""+(int)currentPosition);
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mDataSeriesIndex = mDecoView.addSeries(seriesItem);
    }

    private void createCompletedSeries() {
        final SeriesItem seriesItem = new SeriesItem.Builder(getResources().getColor(R.color.primaryLightColor))
                .setRange(0, mDataSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                /*float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                mTvLabel.setText(mLabel+" ("+String.format("%.0f%%", percentFilled * 100f)+")");
                if(currentPosition < 0){
                    currentPosition = 0;
                }
                mTvValue.setText(""+(int)currentPosition);*/
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mCompletedSeriesIndex = mDecoView.addSeries(seriesItem);
    }
    @Override
    public void onClick(View view) {
        resetAndPlay();
    }

    public void resetAndPlay() {
        mDecoView.executeReset();
        mDecoView.addEvent(new DecoEvent.Builder(100)
                .setIndex(mBackIndex)
                .setDuration(2000)
                .setDelay(100)
                .build());
        replay();
    }

    public void setImageRes(int green_veggies) {
        ((ImageView)findViewById(R.id.image)).setImageResource(green_veggies);
        ((ImageView)findViewById(R.id.image)).setVisibility(View.VISIBLE);
        findViewById(R.id.ll).setVisibility(View.GONE);
    }
}
