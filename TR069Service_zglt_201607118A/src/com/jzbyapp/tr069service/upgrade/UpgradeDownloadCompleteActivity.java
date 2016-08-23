package com.jzbyapp.tr069service.upgrade;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.jzbyapp.tr069service.R;
import com.jzbyapp.utils.Config;
import com.jzbyapp.utils.LogUtils;


/**
 *升级包下载完成提示界面，提高与用户的交互，根据对接不同运营商和前端的经验，虽然不同的项目对升级的实现方式不一样，
 *但总体来说都会有让用户选择升级方式(升级完成立马升级还是下次开机时再升级)的要求，现标准本尽可能的将各个项目的共性
 *统一起来所以出现此类如果以后不同的项目对这个界面显示有不同的要求，只需修改本类即可
 * @author
 */
public class UpgradeDownloadCompleteActivity extends Activity {
	private Button btnRebootForInstallCancel;
	private Button btnRebootForInstallConfirm;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.update_slient_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.custom_title);

		// setTitle(R.string.title);

		// updateFilePath = getIntent().getStringExtra(Config.UPDATE_FILE_PATH);

		btnRebootForInstallCancel = (Button) findViewById(R.id.btnRebootForInstallCancel);
		btnRebootForInstallConfirm = (Button) findViewById(R.id.btnrebootForInstallConfirm);

		btnRebootForInstallCancel.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String filepath = "/cache/update.zip";
				LogUtils.d("reboot upgrade >>>>> " + filepath);
				try {
					Config.HisiSettingService.installUpgrade("Upgrade",
							filepath, false);
					finish();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		btnRebootForInstallConfirm.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String filepath = "/cache/update.zip";
				LogUtils.d("Noreboot upgrade >>>>> " + filepath);
				try {
					Config.HisiSettingService.installUpgrade("Upgrade",
							filepath, true);
					finish();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

}
