package com.tatlisoft.quotez;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.*;
import com.google.ads.mediation.admob.AdMobAdapterExtras;
import com.tatlisoft.quotez.model.Quote;
import com.tatlisoft.quotez.utils.SimpleGestureFilter;

public class MainActivity extends Activity implements com.tatlisoft.quotez.utils.SimpleGestureListener {
    
	private Button _menuButton;
	private Button _likeButton;
	private Button _refreshButton;
	private Button _searchButton;
	private Button _shareButton;
	
	private TextView _quoteTextView;
	private TextView _personTextView;
	
	private String _searchKey;
	private int _personId;
	private Quote _quote;
	private int _liked;
	
	private SimpleGestureFilter _gestureDetector;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        _initialize();
    }
    
    private void _initialize() {
    	_menuButton = (Button)findViewById(R.id.buttonMenu);
    	_likeButton = (Button)findViewById(R.id.buttonLike);
    	_refreshButton = (Button)findViewById(R.id.buttonRefresh);
    	_searchButton = (Button)findViewById(R.id.buttonSearch);
    	_shareButton = (Button)findViewById(R.id.buttonShare);
    	_quoteTextView = (TextView)findViewById(R.id.textViewQuote);
    	_personTextView = (TextView)findViewById(R.id.textViewPerson);
    	_menuButton.setOnClickListener(_menuButtonClickListener);
    	_likeButton.setOnClickListener(_likeButtonClickListener);
    	_refreshButton.setOnClickListener(_refreshButtonClickListener);
    	_searchButton.setOnClickListener(_searchButtonClickListener);
    	_shareButton.setOnClickListener(_shareButtonClickListener);
    	_searchKey = "";
    	_personId = 0;
    	_liked = -1;
    	_gestureDetector = new SimpleGestureFilter(this, this);
    	Bundle extras = getIntent().getExtras();
    	if(extras != null && extras.containsKey("personId")) {
    		_personId = extras.getInt("personId");
    	}
    	getRandomQuote();
    }
    
    
    private OnClickListener _menuButtonClickListener = new OnClickListener() {
    	
    	@Override
    	public void onClick(View view) {
    		MainActivity.this.openOptionsMenu();
    	}
    };
    
    private OnClickListener _likeButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			if(_quote.getLiked() == Quote.LIKED) {
				_unlikeQuote();
			} else {
				_likeQuote();
			}
		}
    };
    
    private void _likeQuote() {
    	if(_quote.getLiked() == Quote.LIKED) {
    		return;
    	}
    	_quote.setLiked(Quote.LIKED);
    	try {
    		_quote.update();
    		_shortToast(getResources().getString(R.string.liked_button_pressed));
    		_likeButton.setBackgroundResource(R.drawable.dark_rating_bad);
    	} catch (Exception e) {
    		_shortToast("Hata: " + e.getMessage());
    	}
    }
    
    private void _unlikeQuote() {
    	if(_quote.getLiked() == Quote.NOT_LIKED) {
    		return;
    	}
    	_quote.setLiked(Quote.NOT_LIKED);
    	try {
    		_quote.update();
    		_shortToast(getResources().getString(R.string.unliked_button_pressed));
    		_likeButton.setBackgroundResource(R.drawable.dark_rating_good);
    	} catch (Exception e) {
    		_shortToast("Hata: " + e.getMessage());
    	}
    }
    
    private OnClickListener _refreshButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			getRandomQuote();
		}
    	
    };
    
    private OnClickListener _searchButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
			alertDialog.setTitle(getResources().getString(R.string.search));
			final EditText input = new EditText(MainActivity.this);
			input.setText(MainActivity.this.getSearchKey());
			alertDialog.setView(input);
			alertDialog.setButton(getResources().getString(R.string.search), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					MainActivity.this.setSearchKey(input.getText().toString().trim());
					MainActivity.this.getRandomQuote();
				}
			});
			alertDialog.show();
		}
    	
    };
    
    private OnClickListener _shareButtonClickListener = new OnClickListener() {
    	public void onClick(View v) {
    		final Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			//shareIntent.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.share));
			shareIntent.putExtra(Intent.EXTRA_TEXT, _quote.toShareString());
			startActivity(Intent.createChooser(shareIntent, getText(R.string.share)));
    	}
    };
    
    private void _longToast(CharSequence message) {
    	Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    private void _shortToast(CharSequence message) {
    	Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    public void getRandomQuote() {
    	String condition = null;
    	if(_searchKey.length() >= 2) {
    		condition = "quote LIKE '%" + _searchKey.replace("'","") + "%'";
    	}
    	if(_personId != 0) {
    		if(condition == null) {
    			condition = "person_id = " + _personId;
    		} else {
    			condition = condition + " AND person_id = " + _personId;
    		}
    	}
    	if(_liked != -1) {
    		if(condition == null) {
    			condition = "liked = " + _liked;
    		} else {
    			condition = condition + " AND liked = " + _liked;
    		}
    	}
    	try {
    		_quote = Quote.getRandom(MainActivity.this, condition);
    	} catch (Exception e) {
    		_longToast(e.getMessage());
    		Log.d("Error", "Hata1: " + e.getMessage());
    		_removeSearchFilters();
    		try {
    			_quote = Quote.getRandom(MainActivity.this);
    		} catch (Exception e2) {
    			Log.d("Error", "Hata2: " + e2.getMessage());
    			_longToast(e2.getMessage());
    			_quote = new Quote(this);
    		}
    	}
    	Animation quoteAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
    	quoteAnimation.setDuration(1000);
    	_quoteTextView.startAnimation(quoteAnimation);
    	Animation personAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
    	personAnimation.setDuration(1000);
    	_personTextView.startAnimation(personAnimation);
		_quoteTextView.setText(_quote.getQuote());
		_personTextView.setText(_quote.getPerson().getName());
		if(_quote.getLiked() == Quote.LIKED) {
			_likeButton.setBackgroundResource(R.drawable.dark_rating_bad);
		} else {
			_likeButton.setBackgroundResource(R.drawable.dark_rating_good);
		}
    }
    
    public void setSearchKey(String _searchKey) {
    	this._searchKey = _searchKey;
    }
    
    public String getSearchKey() {
    	return this._searchKey;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
	    	case R.id.menuItemPeople:
	    		_showPeopleListerActivity();
	    		return true;
	    	case R.id.menuItemLiked:
	    		_liked = Quote.LIKED;
	    		getRandomQuote();
	    		return true;
	    	case R.id.menuItemClean:
	    		_removeSearchFilters();
	    		getRandomQuote();
	    		return true;
	    	case R.id.menuItemCopy:
	    		_copyContent();
	    		return true;
	    	case R.id.menuItemAbout:
	    		_showAboutContent();
	    		return true;
	    	case R.id.menuItemContact:
	    		_showContactActivity();
	    		return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
    
    private void _showPeopleListerActivity() {
    	Intent peopleIntent = new Intent(this, PeopleListerActivity.class);
		startActivity(peopleIntent);
    }
    
    private void _showContactActivity() {
    	Intent contactIntent = new Intent(this, ContactActivity.class);
    	startActivity(contactIntent);
    }

    private void _showAboutContent() {
    	Intent aboutIntent = new Intent(this, AboutActivity.class);
    	startActivity(aboutIntent);
    }
    
    private void _copyContent() {
    	int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(_quote.toShareString());
		} else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip = android.content.ClipData.newPlainText(getResources().getString(R.string.app_name), _quote.toShareString());
			clipboard.setPrimaryClip(clip);
		}
		_longToast(getResources().getString(R.string.copied));
    }
    
    private void _removeSearchFilters() {
    	_searchKey = "";
		_liked = -1;
		_personId = 0;
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
    	_gestureDetector.onTouchEvent(motionEvent);
    	return super.dispatchTouchEvent(motionEvent);
    }

	@Override
	public void onSwipe(int direction) {
		switch(direction) {
		case SimpleGestureFilter.SWIPE_DOWN:
			_unlikeQuote();
			break;
		case SimpleGestureFilter.SWIPE_UP:
			_likeQuote();
			break;
		}
	}

	@Override
	public void onDoubleTap() {
		getRandomQuote();
	}
    
}