package com.stv.supervod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.stv.supervod.service.RegisterServiceImpl;
import com.stv.supervod.utils.AlertUtils;
import com.stv.supervod.utils.ValidateUtil;

/**
 * @author Administrator
 * @description 注册
 * @authority 所有用户
 * @function 1、用户输入用户名、密码、邮箱，点击确定提交服务器
 *           2、用户点击激活单选框，则出现需要输入的电话号、身份证号、CA卡号等信息，点击激活提交服务器
 */
public class Register extends BaseActivity {

	EditText username;
	EditText password;
	EditText confirm_password;
	EditText phone;
	EditText id_number;
	EditText smartcard;
	EditText service_password;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		username = (EditText) this.findViewById(R.id.username);
		password = (EditText) this.findViewById(R.id.password);
		confirm_password = (EditText) this.findViewById(R.id.confirm_password);
		phone = (EditText) this.findViewById(R.id.phone);
		id_number = (EditText) this.findViewById(R.id.id_number);
		smartcard = (EditText) this.findViewById(R.id.smartcard);
		service_password = (EditText) this.findViewById(R.id.service_password);
	}

	/**
	 * Description: 用户信息注册
	 * @Version1.0 2011-12-12 下午04:30:08 mustang created
	 * @param view
	 */
	public void regist(View view) {
		String vusername = username.getText().toString();
		String vpassword = password.getText().toString();
		String vconfirm_password = confirm_password.getText().toString();
		String vphone = phone.getText().toString();
		String vid_number = id_number.getText().toString();
		String vsmartcard = smartcard.getText().toString();
		String vservice_password = service_password.getText().toString();
		if (ValidateUtil.isBlank(vusername) || ValidateUtil.isBlank(vpassword) || ValidateUtil.isBlank(vconfirm_password)
				|| ValidateUtil.isBlank(vid_number) || ValidateUtil.isBlank(vservice_password)) {
			AlertUtils.displayToast(this, "信息填写不完整");
			return;
		}
        if(!ValidateUtil.isEmail(vusername)||!ValidateUtil.isNumber(vphone)||!ValidateUtil.isNumber(vid_number)||!ValidateUtil.isNumber(vsmartcard)){
        	AlertUtils.displayToast(this, "信息格式不正确");
        	return;
        }
        if(!vpassword.equals(vconfirm_password)){
        	AlertUtils.displayToast(this, "两次输入密码不一致");
        	return;
        }
		
		boolean flag = false;
		try {
			RegisterServiceImpl regService = new RegisterServiceImpl();
			flag = regService.saveRegisterInfo(vusername, vpassword, vphone, vid_number, vsmartcard, vservice_password);
		} catch (Exception e) {
			e.printStackTrace();
			AlertUtils.displayToast(this, "连接网络出现异常");
			return;
		} 
		if (flag) {
			Intent intent = new Intent();
			intent.setClass(this, LoginSuccess.class);
			Bundle bd = new Bundle();
			bd.putString("username", vusername);
			intent.putExtras(bd);
			Framework.switchActivity("register2loginsucess", intent);
		} else {
			AlertUtils.displayToast(this, "注册信息不正确");
		}
	}
	
	
	/**
	 * Description: 返回
	 * @Version1.0 2011-12-14 下午03:01:26 mustang created
	 * @param view
	 */
	public void showRegPage(View view){
		Framework.switchActivityBack();
	}
	

}
