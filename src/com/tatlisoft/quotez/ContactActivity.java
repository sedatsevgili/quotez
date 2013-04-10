package com.tatlisoft.quotez;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

public class ContactActivity extends Activity{

	private EditText _nameSurnameEditText;
	
	private EditText _emailEditText;
	
	private EditText _contentEditText;
	
	private Button _sendButton;
	
	@Override
	public void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);
		setContentView(R.layout.contact);
		_initialize();
	}
	
	private void _initialize() {
		_nameSurnameEditText = (EditText)findViewById(R.id.editTextNameSurname);
		_emailEditText = (EditText)findViewById(R.id.editTextEmail);
		_contentEditText = (EditText)findViewById(R.id.editTextContent);
		_sendButton = (Button)findViewById(R.id.buttonContactSubmit);
		_sendButton.setOnClickListener(_sendButtonClickListener);
	}
	
	private OnClickListener _sendButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			if(!_checkFormToValidate()) {
				return;
			}
			String content = "";
			content = "İsim: " + _nameSurnameEditText.getText() + "\n";
			content += "Email: " + _emailEditText.getText() + "\n";
			content += "Mesaj: " + _contentEditText.getText() + "\n";
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("message/rfc822");
			intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"tatliyazilim@gmail.com"});
			intent.putExtra(Intent.EXTRA_SUBJECT, "Özlü Sözler Uygulaması Hakkında");
			intent.putExtra(Intent.EXTRA_TEXT, content);
			try {
				startActivity(Intent.createChooser(intent, "Email Gönder.."));
			} catch (Exception ex) {
				_longToast("Email gönderiminde hata: " + ex.getMessage());
			}
		}
		
	};
	
	private boolean _checkFormToValidate() {
		if(_nameSurnameEditText.getText().length() == 0) {
			_longToast("Lütfen isminizi giriniz");
			_nameSurnameEditText.requestFocus();
			return false;
		}
		if(_emailEditText.getText().length() == 0) {
			_longToast("Lütfen email adresinizi giriniz");
			_emailEditText.requestFocus();
			return false;
		}
		if(_contentEditText.getText().length() == 0) {
			_longToast("Lütfen mesajınızı giriniz");
			_contentEditText.requestFocus();
			return false;
		}
		return true;
	}
	
	private void _longToast(CharSequence message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	private void _shortToast(CharSequence message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
}
