package com.jzbyapp.openuser;


import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jzbyapp.tr069service.R;
import com.jzbyapp.tr069service.Tr069Service;
import com.jzbyapp.utils.Config;
import com.jzbyapp.utils.LogUtils;

public class ZeroConfigActivity extends Activity implements OnClickListener{
	private TextView textViewOk;
	private TextView textViewAgain;
	
	private Button but_Ok;
	private Button but_Cancle;
	
	private EditText text0;
	private EditText text1;
	private EditText text2;
	private EditText text3;
	private EditText text4;
	private EditText text5;
	private EditText text6;
	private EditText text7;
	private EditText text8;
	private EditText text9;
	private EditText text10;
	private EditText text11;
	private EditText text12;
	private EditText[] txts;
	
	private String activeCode = "";
	//private Tr069service mContext = null;
	private int count = 0;
	private int temp = 0;
	private int aftertmp=1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setFinishOnTouchOutside(false);
		setContentView(R.layout.edittext_view);
		LogUtils.d("EditTextActivity Oncreate is in ");
		
		Bundle bundle = this.getIntent().getExtras();
		String bundlepar = (String) bundle.get("openflag");
		
		InitFindViewID();
		setListener();
		
		LogUtils.d("lrui 20151016 ShowPopupWindow bundlepar is ==>" + bundlepar);
		if(bundlepar.equalsIgnoreCase("TRUE")){
			textViewOk.setVisibility(View.VISIBLE);
			textViewAgain.setVisibility(View.GONE);
		}else if(bundlepar.equalsIgnoreCase("FALSE")){
			textViewOk.setVisibility(View.GONE);
			textViewAgain.setVisibility(View.VISIBLE);
		}
		
		but_Ok.setOnClickListener(this);
		but_Cancle.setOnClickListener(this);
	}
	
	private void InitFindViewID(){
		textViewOk = (TextView) findViewById(R.id.textlog1);
		textViewAgain = (TextView) findViewById(R.id.textlog2);
		
		text0 = (EditText) findViewById(R.id.edittext0);
		text0.setFocusable(true);
		text1 = (EditText) findViewById(R.id.edittext1);
		text2 = (EditText) findViewById(R.id.edittext2);
		text3 = (EditText) findViewById(R.id.edittext3);
		text4 = (EditText) findViewById(R.id.edittext4);
		text5 = (EditText) findViewById(R.id.edittext5);
		text6 = (EditText) findViewById(R.id.edittext6);
		text7 = (EditText) findViewById(R.id.edittext7);
		text8 = (EditText) findViewById(R.id.edittext8);
		text9 = (EditText) findViewById(R.id.edittext9);
		text10 = (EditText) findViewById(R.id.edittext10);
		text11 = (EditText) findViewById(R.id.edittext11);
		text12 = (EditText) findViewById(R.id.edittext12);
		txts = new EditText[] {text0,text1,text2,text3,text4,text5,text6,text7,text8,text9,text10,text11,text12};
		
		text12.setNextFocusDownId(R.id.btn_ok);
		
		but_Ok = (Button) findViewById(R.id.btn_ok);
		but_Cancle = (Button) findViewById(R.id.btn_cancle);
		
		but_Ok.setNextFocusUpId(R.id.edittext12);
		but_Cancle.setNextFocusUpId(R.id.edittext0);
		
		but_Ok.setOnClickListener(this);
		but_Cancle.setOnClickListener(this);
	}
	
	private void setListener(){
		addTextListener();
	}
	
	private void addTextListener(){
		if(txts != null){
			for(int i=0; i<txts.length; i++){
				txts[i].setOnKeyListener(key);
				txts[i].addTextChangedListener(watcher);
			}
		}
	}
	
OnKeyListener key = new View.OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if(keyCode == KeyEvent.KEYCODE_DEL){
				if(event.getAction() == 0){
					temp = count;
				}	
				switch(v.getId()){
					case R.id.edittext0:
						txts[0].requestFocus();
						txts[0].setText("");
						count =-1;
						break;
					case R.id.edittext1:
						txts[1].requestFocus();
						txts[1].setText("");
						count=0;
						break;
					case R.id.edittext2:
						txts[2].requestFocus();
						txts[2].setText("");
						count=1;
						break;
					case R.id.edittext3:
						txts[3].requestFocus();
						txts[3].setText("");
						count=2;
						break;
					case R.id.edittext4:
						txts[4].requestFocus();
						txts[4].setText("");
						count=3;
						break;
					case R.id.edittext5:
						txts[5].requestFocus();
						txts[5].setText("");
						count=4;
						break;
					case R.id.edittext6:
						txts[6].requestFocus();
						txts[6].setText("");
						count=5;
						break;
					case R.id.edittext7:
						txts[7].requestFocus();
						txts[7].setText("");
						count=6;
						break;
					case R.id.edittext8:
						txts[8].requestFocus();
						txts[8].setText("");
						count=7;
						break;
					case R.id.edittext9:
						txts[9].requestFocus();
						txts[9].setText("");
						count=8;
						break;
					case R.id.edittext10:
						txts[10].requestFocus();
						txts[10].setText("");
						count=9;
						break;
					case R.id.edittext11:
						txts[11].requestFocus();
						txts[11].setText("");
						count=10;
						break;
					case R.id.edittext12:
						txts[12].requestFocus();
						txts[12].setText("");
						count=11;
						break;
					default:
						break;
				}
			}
			return false;
		}
	};
	
	TextWatcher watcher = new TextWatcher(){
		public void onTextChanged(CharSequence charsequence, int i,int j,int k){
			if(j==0){
				if(count < txts.length-1){
					txts[++count].requestFocus();
					LogUtils.d("the count is "+count);
				}
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int counts,
				int after) {
			// TODO Auto-generated method stub
			
			if((aftertmp == 0) && (after == 1)){
				count = (temp-1);	
			}
			aftertmp = after;
		}
	};
	
	private void getEditTextData(){
		for(int i=0; i<txts.length; i++){
			activeCode += txts[i].getText().toString();
		}
		LogUtils.d("getEditTextData() txts.length="+txts.length+"activeCode.length"+activeCode.length() + " "+activeCode);
		//activeCode = activeCode.substring(0, 11)+"-"+activeCode.substring(12, activeCode.length());
		StringBuilder sb = new StringBuilder(activeCode);
		sb.insert(11, "-");
		activeCode = sb.toString();
	}
	
	private void cancleEditTextData(){
		for(int i=0; i<txts.length; i++){
			txts[i].setText("");
		}
	}
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.btn_ok:			
			byte[] keyBytes = new byte[24];
			String STBID = SystemProperties.get("ro.serialno") + 
				SystemProperties.get("ro.mac").replaceAll(":", "");
			LogUtils.d("lrui 20151022 ShowPopupWindow onclick get STBID is ==>" + STBID);
			try {
				keyBytes = ThreeDES.build3DesKey(STBID);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			getEditTextData();
			byte[] encoded = ThreeDES.encryptMode(keyBytes, activeCode.getBytes());
			String str = ThreeDES.byte2hex(encoded);
			LogUtils.d("lrui 20151022 ShowPopupWindow onclick activeCode is ==>" + activeCode + "encode after" + str);
			Tr069Service.TR069ServiceSendMsg(4,str);
			Config.messageQueue.callProgressDialog(getString(R.string.openuser_activing), 1);
			break;
		case R.id.btn_cancle:
			cancleEditTextData();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//startActivityAgain();
		LogUtils.d("onPause()  is in");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LogUtils.d("onResume() is in");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		LogUtils.d("onStop() is in");
	}
}
