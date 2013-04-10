package com.tatlisoft.quotez;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import com.tatlisoft.quotez.model.Person;

public class PeopleListerActivity extends Activity {

	private ListView _peopleListView;
	private EditText _peopleSearchEditText;
	private String _searchKey;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.people_layout);
		_initialize();
	}
	
	private void _initialize() {
		_peopleListView = (ListView)findViewById(R.id.listViewPeople);
		_peopleSearchEditText = (EditText)findViewById(R.id.editTextPeopleSearch);
		_searchKey = "";
		_peopleSearchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				_searchKey = s.toString().trim();
				PeopleListerActivity.this.loadPeople();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
		});
		_peopleListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
				String personName = ((TextView)view).getText().toString();
				Person person = new Person(PeopleListerActivity.this);
				person.loadByName(personName);
				Intent intent = new Intent(PeopleListerActivity.this, MainActivity.class);
				intent.putExtra("personId", person.getId());
				startActivity(intent);
			}
			
		});
		loadPeople();
	}
	
	public void loadPeople() {
		String condition = null;
		if(_searchKey.length() != 0) {
			condition = " name LIKE '%" + _searchKey + "%'";
		}
		ArrayList<String> people = Person.loadAll(this, condition);
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.people_row, people);
		_peopleListView.setAdapter(listAdapter);
		
	}
	
}
