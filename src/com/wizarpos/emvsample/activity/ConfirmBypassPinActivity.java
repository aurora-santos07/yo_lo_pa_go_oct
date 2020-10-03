package com.wizarpos.emvsample.activity;

import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.transaction.TransDefine;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.app.Activity;
import android.content.Intent;

public class ConfirmBypassPinActivity extends FuncActivity
{
	private TextView textTitle  = null;
	private Button   buttonBack = null;
    private Button   buttonMore = null;

	private RadioGroup rgChoice = null;
	int rbChoiceID = -1;
	
	private Button buttonCancel = null;
	private Button buttonOK = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bypass_pin);
        
        // title
        textTitle = (TextView)findViewById(R.id.tAppTitle);
		textTitle.setText(appState.getString(TransDefine.transInfo[appState.getTranType()].id_display_en));
		
	    buttonBack = (Button)findViewById(R.id.btn_back);
        buttonBack.setOnClickListener(new ClickListener());
        
        buttonMore = (Button)findViewById(R.id.btn_more);
        buttonMore.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blank));
        
        buttonOK = (Button)findViewById(R.id.btn_digit_enter);
        buttonOK.setOnClickListener(new ClickListener());
        
        buttonCancel = (Button)findViewById(R.id.btn_digit_cancel);
        buttonCancel.setOnClickListener(new ClickListener());

        rgChoice = (RadioGroup)findViewById(R.id.rgBypassPin_Choice);
        rgChoice.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        	public void onCheckedChanged(RadioGroup arg0, int arg1)
        	{
        		rbChoiceID = arg0.getCheckedRadioButtonId();
        	}
   		});

    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
		appState.theActivity = this;
        startIdleTimer(TIMER_FINISH, DEFAULT_IDLE_TIME_SECONDS);
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
    }
    
    @Override
    protected void onStop()
    {
        super.onStop();
    }
    
    @Override
    public void onBackPressed(){
		setResult(Activity.RESULT_CANCELED, getIntent());
		finish();
    }
    
	public class ClickListener implements View.OnClickListener
    {
		@Override
		public void onClick(View v) 
		{
			Intent intent = getIntent();
			switch(v.getId())
			{
			case R.id.btn_digit_enter:
				if(rbChoiceID != -1)
				{
					if(rbChoiceID == R.id.rbBypassPin_Accept)
					{
						//emv_set_pin_bypass_confirmed(1);

					}
					else if(rbChoiceID == R.id.rbBypassPin_Denial)
					{
						//emv_set_pin_bypass_confirmed(0);
					}
					setResult(Activity.RESULT_OK, intent);
					finish();
				}
				break;
			case R.id.btn_back:
			case R.id.btn_digit_cancel:
				setResult(Activity.RESULT_CANCELED, intent);
				finish();
				break;	
			}
		}
	
    }
}
