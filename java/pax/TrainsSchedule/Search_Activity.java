package pax.TrainsSchedule;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;

import java.util.Calendar;

import pax.TrainsSchedule.Model.Favorite;
import pax.TrainsSchedule.Model.SearchParams;
import pax.TrainsSchedule.Model.Station;
import pax.TrainsSchedule.Search_DatePickerFragment.OnDatePicked;
import pax.TrainsSchedule.Services.SuggestsRaspService;
import pax.TrainsSchedule.Util.DB;
import pax.TrainsSchedule.Util.Multilanguage;
import pax.TrainsSchedule.Util.Util;

public class Search_Activity extends AppCompatActivity implements OnDatePicked {

    public static final String ARGS_FROM_CODE = "ARGS_FROM_CODE";
    public static final String ARGS_FROM_NAME = "ARGS_FROM_NAME";
    public static final String ARGS_TO_CODE   = "ARGS_TO_CODE";
    public static final String ARGS_TO_NAME   = "ARGS_TO_NAME";
    public static final String ARGS_DATE      = "ARGS_DATE";


    protected SpiceManager mSpiceManager = new SpiceManager(SuggestsRaspService.class);
    private Search_AutoCompleteAdapter mSearchAdapterFrom;
    private Search_AutoCompleteAdapter mSearchAdapterTo;
    private AutoCompleteTextView       tvSearchFrom;
    private AutoCompleteTextView       tvSearchTo;
    private Button                     mBtnAddToFavorites;
    private EditText                   tvDate;
    private TextInputLayout            tilSearchFrom;
    private TextInputLayout            tilSearchTo;

    private SearchParams mCurrentSearch;
    private int          imgWidth;
    private DB           mainDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);

        init_LoadParams();
        init_FAB();
        init_TV();
    }

    private void init_LoadParams() {
        Bundle args = getIntent().getExtras();
        if (args == null) return;
        String currentFromCode = args.getString(ARGS_FROM_CODE, "");
        String currentFromName = args.getString(ARGS_FROM_NAME, "");
        String currentToCode = args.getString(ARGS_TO_CODE, "");
        String currentToName = args.getString(ARGS_TO_NAME, "");
        long date = args.getLong(ARGS_DATE, 0);
        mCurrentSearch = new SearchParams(date, currentFromCode, currentFromName, currentToCode, currentToName);

        mainDB = new DB(this);
    }

    private void init_TV() {

        mSearchAdapterFrom = new Search_AutoCompleteAdapter(this, mSpiceManager);
        mSearchAdapterTo = new Search_AutoCompleteAdapter(this, mSpiceManager);

        mBtnAddToFavorites = (Button) findViewById(R.id.btnAddToFavorites);
        updateFavVisibility(mCurrentSearch.fromCode, mCurrentSearch.toCode);

        tvSearchFrom = (AutoCompleteTextView) findViewById(R.id.tvSearchFrom);
        tvSearchTo = (AutoCompleteTextView) findViewById(R.id.tvSearchTo);
        tvDate = (EditText) findViewById(R.id.tvDate);

        tvSearchFrom.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSearchAdapterFrom.mSelectedItem = mSearchAdapterFrom.getItem(position);
                updateFavVisibility(mSearchAdapterFrom.mSelectedItem, mSearchAdapterTo.mSelectedItem);
            }
        });
        tvSearchFrom.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tilSearchFrom.setError("");
                    tilSearchFrom.setErrorEnabled(false);
                }
            }
        });

        tvSearchTo.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tilSearchTo.setError("");
                    tilSearchTo.setErrorEnabled(false);
                }
            }
        });

        tvSearchTo.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSearchAdapterTo.mSelectedItem = mSearchAdapterTo.getItem(position);
                updateFavVisibility(mSearchAdapterFrom.mSelectedItem, mSearchAdapterTo.mSelectedItem);
            }
        });

        tilSearchFrom = (TextInputLayout) findViewById(R.id.textInputLayoutFrom);
        tilSearchTo = (TextInputLayout) findViewById(R.id.textInputLayoutTo);

        Button btnSwitch = (Button) findViewById(R.id.btnSwitch);

        Drawable imgClear = getResources().getDrawable(R.mipmap.ic_clear_search_api_holo_light);
        imgWidth = tvSearchFrom.getPaddingRight() + imgClear.getIntrinsicWidth();

        tvSearchFrom.setOnTouchListener(new OnClear());
        tvSearchTo.setOnTouchListener(new OnClear());

        if (Util.notEmpty(mCurrentSearch.fromName)) {
            mSearchAdapterFrom.mSelectedItem = new Station(mCurrentSearch.fromCode, mCurrentSearch.fromName);
            tvSearchFrom.setText(mCurrentSearch.fromName);
        }
        if (Util.notEmpty(mCurrentSearch.toName)) {
            mSearchAdapterTo.mSelectedItem = new Station(mCurrentSearch.toCode, mCurrentSearch.toName);
            tvSearchTo.setText(mCurrentSearch.toName);
        }

        tvSearchFrom.setAdapter(mSearchAdapterFrom);
        tvSearchTo.setAdapter(mSearchAdapterTo);

        tvDate.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDataPicker();
                }
            }
        });
        tvDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataPicker();
            }
        });

        btnSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Station tmpFrom = mSearchAdapterFrom.mSelectedItem;
                mSearchAdapterFrom.mSelectedItem = mSearchAdapterTo.mSelectedItem;
                mSearchAdapterTo.mSelectedItem = tmpFrom;

                mSearchAdapterFrom.needAutocomplete = false;
                mSearchAdapterTo.needAutocomplete = false;

                String textFrom = tvSearchFrom.getText().toString();
                tvSearchFrom.setText(tvSearchTo.getText().toString());
                tvSearchTo.setText(textFrom);

                mSearchAdapterFrom.needAutocomplete = true;
                mSearchAdapterTo.needAutocomplete = true;
            }
        });

        tvSearchTo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch();
                    return true;
                }
                return false;
            }
        });

        tvDate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch();
                    return true;
                }
                return false;
            }
        });


        mBtnAddToFavorites.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavorites();
            }
        });

        setDataText();
    }

    private void updateFavVisibility(String codeFrom, String codeTo) {
        if (mainDB.isInFavorites(codeFrom, codeTo)) mBtnAddToFavorites.setVisibility(View.INVISIBLE);
        else mBtnAddToFavorites.setVisibility(View.VISIBLE);
    }

    private void updateFavVisibility(Station from, Station to) {
        if (from == null || to == null || from.code.isEmpty() || to.code.isEmpty() || mainDB.isInFavorites(from.code, to.code)) mBtnAddToFavorites.setVisibility(View.INVISIBLE);
        else mBtnAddToFavorites.setVisibility(View.VISIBLE);
    }

    private boolean checkErrors(Station curFrom, Station curTo) {
        tilSearchFrom.setError("");
        tilSearchFrom.setErrorEnabled(false);

        if (tvSearchFrom.getText().toString().isEmpty()) {
            tilSearchFrom.setErrorEnabled(true);
            tilSearchFrom.setError(Multilanguage.enterStation);
            return true;
        }

        if (curFrom.code.isEmpty()) {
            tilSearchFrom.setErrorEnabled(true);
            tilSearchFrom.setError(Multilanguage.stationNotFound);
            return true;
        }

        if (tvSearchTo.getText().toString().isEmpty()) {
            tilSearchTo.setErrorEnabled(true);
            tilSearchTo.setError(Multilanguage.enterStation);
            return true;
        }

        if (curTo.code.isEmpty()) {
            tilSearchTo.setErrorEnabled(true);
            tilSearchTo.setError(Multilanguage.stationNotFound);
            return true;
        }

        return false;
    }

    private void addToFavorites() {
        Station curFrom = mSearchAdapterFrom.mSelectedItem;
        Station curTo = mSearchAdapterTo.mSelectedItem;

        if (checkErrors(curFrom, curTo)) return;

        DB mainDB = new DB(this);
        mainDB.addToFavorite(new Favorite(curFrom.code, curFrom.name, curTo.code, curTo.name));
        mainDB.close();

        Toast.makeText(getApplication(), Multilanguage.addedToFav, Toast.LENGTH_SHORT).show();
    }

    private void setDataText() {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(mCurrentSearch.date);

        tvDate.setText(Util.getDateRepresentation(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)));
    }

    private void showDataPicker() {
        DialogFragment newFragment = new Search_DatePickerFragment();
        Bundle args = new Bundle();
        args.putLong("DATE", mCurrentSearch.date);
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "date");
    }

    @Override
    public void onDatePicked(int year, int monthOfYear, int dayOfMonth) {
        final Calendar c = Calendar.getInstance();
        c.set(year, monthOfYear, dayOfMonth);
        mCurrentSearch.date = c.getTimeInMillis();

        tvDate.setText(Util.getDateRepresentation(year, monthOfYear, dayOfMonth));
    }

    class OnClear implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() != MotionEvent.ACTION_UP) return false;

            if (event.getX() > v.getWidth() - imgWidth) {
                if (v == tvSearchFrom) {
                    tvSearchFrom.setText("");
                    mSearchAdapterFrom.mSelectedItem.clear();
                    mBtnAddToFavorites.setVisibility(View.INVISIBLE);
                } else {
                    tvSearchTo.setText("");
                    mSearchAdapterTo.mSelectedItem.clear();
                    mBtnAddToFavorites.setVisibility(View.INVISIBLE);
                }
            }
            return false;
        }
    }

    private void init_FAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.search_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSearch();
            }
        });
    }

    private void doSearch() {
        Station curFrom = mSearchAdapterFrom.mSelectedItem;
        Station curTo = mSearchAdapterTo.mSelectedItem;

        if (checkErrors(curFrom, curTo)) return;

        if (curFrom == null || curTo == null || curFrom.code.isEmpty() || curTo.code.isEmpty()) return;

        Intent intent = new Intent();
        intent.putExtra(ARGS_FROM_CODE, curFrom.code);
        intent.putExtra(ARGS_FROM_NAME, curFrom.name);
        intent.putExtra(ARGS_TO_CODE, curTo.code);
        intent.putExtra(ARGS_TO_NAME, curTo.name);
        intent.putExtra(ARGS_DATE, mCurrentSearch.date);
        setResult(RESULT_OK, intent);

//        forceCloseKeyboard();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSpiceManager.start(this);
    }

    @Override
    protected void onStop() {
        mSpiceManager.shouldStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainDB.close();
    }

}
