package com.wizarpos.emvsample.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wizarpos.emvsample.R;
import com.wizarpos.jni.PinPadInterface;
import com.wizarpos.util.StringUtil;

public class FuncMenuActivity extends FuncActivity
{
	private TextView textTitle  = null;
	private Button   buttonBack = null;
	private Button   buttonMore = null;

	private Button buttonSale = null;
	private Button buttonLastPBOC = null;
	private Button buttonTrans = null;
	private Button buttonSettle = null;
	private Button buttonEncrypt = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_func_menu);

		textTitle = (TextView)findViewById(R.id.tAppTitle);
		textTitle.setText("MAIN");

		buttonBack = (Button)findViewById(R.id.btn_back);
		buttonBack.setOnClickListener(new ClickListener());

		buttonMore = (Button)findViewById(R.id.btn_more);
		buttonMore.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blank));

		buttonSale = (Button)findViewById(R.id.bFunc_Sale);
		buttonSale.setOnClickListener(new ClickListener());


		buttonLastPBOC = (Button)findViewById(R.id.bFunc_LastPBOC);
		buttonLastPBOC.setOnClickListener(new ClickListener());

		buttonTrans = (Button)findViewById(R.id.bFunc_Trans);
		buttonTrans.setOnClickListener(new ClickListener());

		buttonSettle = (Button)findViewById(R.id.bFunc_Settle);
		buttonSettle.setOnClickListener(new ClickListener());

		buttonEncrypt = (Button)findViewById(R.id.bFunc_encrypt);
		buttonEncrypt.setOnClickListener(new ClickListener());
	}

	@Override
	protected void onStart()
	{
		if(debug)Log.d(APP_TAG, "FuncMenuActivity onStart");
		super.onStart();
		appState.theActivity = this;
		if(appState.emvParamChanged == true)
		{
			setEMVTermInfo();
		}
		startIdleTimer(TIMER_IDLE, DEFAULT_IDLE_TIME_SECONDS);
	}

	@Override
	protected void onResume()
	{
		if(debug)Log.d(APP_TAG, "FuncMenuActivity onResume");
		super.onResume();
	}

	@Override
	protected void onStop()
	{
		if(debug)Log.d(APP_TAG, "FuncMenuActivity onStop");
		super.onStop();
	}

	@Override
	protected void onPause()
	{
		if(debug)Log.d(APP_TAG, "FuncMenuActivity onPause");
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		if(debug)Log.d(APP_TAG, "FuncMenuActivity onDestroy");
		super.onDestroy();
	}

	@Override
	public void onBackPressed(){
		go2Idle();
	}

	@Override
	protected void onCancel()
	{
		onBackPressed();
	}

	@Override
	protected void onBack()
	{
		onBackPressed();
	}

	public class ClickListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			switch(v.getId())
			{
			case R.id.bFunc_Sale:
				appState.needCard = true;
				//sale();
				break;

			case R.id.bFunc_LastPBOC:
				showLastPBOC();
				break;

			case R.id.bFunc_Trans:
				queryCardRecord((byte)0x00);
				break;

			case R.id.bFunc_Settle:
				settle();
				break;

			case R.id.bFunc_encrypt:
				//Encrypt();

				break;

			case R.id.btn_back:
				go2Idle();
				break;
			}
		}
	}
	private static final String EncryptTag = "EncryptTest";

	/*private void Encrypt(){
		String sResult = "";
		if(PinPadInterface.open() >= 0)
		{
			if(PinPadInterface.setKey(1,2,0,1) >=0){
//				byte[] inArray = createByteArray(32);
				byte[] inArray = StringUtil.hexString2bytes("57132223000010364419D19122010000000005230F000000");

				Log.i(EncryptTag,"inArray:" + StringUtil.toHexString(inArray,true));
				byte[] outArray = new byte[1024];
//				int realLength = PinPadInterfaceYLP.encrypt(inArray, inArray.length, outArray);
				int realLength = PinPadInterface.encryptWithMode(inArray, outArray, 1/*CBC* /,new byte[8]/*vector * /,8/*length of initial vector* /);

				byte[] result = subBuffer(outArray, realLength);
                Log.i(EncryptTag,"outArray:" + StringUtil.toHexString(result,true));
                Toast.makeText(this,
                    "in"+ StringUtil.toHexString(inArray,true) + "\n" +
                        "out:"+StringUtil.toHexString(result,true),
                    Toast.LENGTH_SHORT).show();

                PinPadInterface.close();
			}
		}
	}*/


	public byte[] subBuffer(byte[] buf, int length) {
		if (length < 0) {
			return null;
		}
		byte[] result = new byte[length];
		if (buf.length >= length) {
			System.arraycopy(buf, 0, result, 0, length);
		}
		return result;
	}
}
