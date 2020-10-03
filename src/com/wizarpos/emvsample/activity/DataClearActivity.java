package com.wizarpos.emvsample.activity;

import com.wizarpos.emvsample.R;

import android.os.Bundle;
import android.view.Window;


public class DataClearActivity extends FuncActivity 
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_data_clear);
    }

	@Override
	protected void onStart()
	{
		super.onStart();
		appState.theActivity = this;
        startIdleTimer(TIMER_IDLE, DEFAULT_IDLE_TIME_SECONDS);

	}
	
    @Override 
    protected void onPause() { 
        super.onPause(); 
    }
    
    @Override
    protected void onStop()
    {
        super.onStop();
    }
    
    @Override
    public void onBackPressed(){

    }
}
