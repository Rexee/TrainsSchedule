package pax.TrainsSchedule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.design.widget.TabLayout.ViewPagerOnTabSelectedListener;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.Calendar;

import pax.TrainsSchedule.API.RaspRequest;
import pax.TrainsSchedule.API.SpiceRaspRequest;
import pax.TrainsSchedule.API.SpiceRaspRequestCached;
import pax.TrainsSchedule.FavoritesListAdapter.OnFavoriteClick;
import pax.TrainsSchedule.Model.Favorite;
import pax.TrainsSchedule.Model.SearchParams;
import pax.TrainsSchedule.Services.RaspServiceNetworkService;
import pax.TrainsSchedule.Util.Multilanguage;
import pax.TrainsSchedule.Util.Util;


public class MainActivity extends AppCompatActivity implements OnFavoriteClick {

    protected SpiceManager mSpiceManager = new SpiceManager(RaspServiceNetworkService.class);

    private static final int    SEARCH_ACTIVITY_RESULT_CODE = 1;
    private static final String SETTINGS_STATION_FROM_NAME  = "SETTINGS_STATION_FROM_NAME";
    private static final String SETTINGS_STATION_FROM_CODE  = "SETTINGS_STATION_FROM_CODE";
    private static final String SETTINGS_STATION_TO_NAME    = "SETTINGS_STATION_TO_NAME";
    private static final String SETTINGS_STATION_TO_CODE    = "SETTINGS_STATION_TO_CODE";
    private static final String SETTINGS_DATE               = "SETTINGS_DATE";

    private MainActivityTabsAdapter mTabsAdapter;
    private ViewPager               mViewPager;
    private SearchParams            mCurrentSearch;
    private Toolbar                 mToolbar;
    private ProgressBar             mProgressBar;
    private TabLayout               mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Multilanguage.Init(this);

        setContentView(R.layout.main_activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mToolbar.setTitle(Multilanguage.app_name);
        setSupportActionBar(mToolbar);

        init_vars();
        init_Tabs();
        init_FAB();
    }

    private void init_vars() {
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        String stationFromName = sPref.getString(SETTINGS_STATION_FROM_NAME, "");
        String stationFromCode = sPref.getString(SETTINGS_STATION_FROM_CODE, "");
        String stationToName = sPref.getString(SETTINGS_STATION_TO_NAME, "");
        String stationToCode = sPref.getString(SETTINGS_STATION_TO_CODE, "");
        long date = sPref.getLong(SETTINGS_DATE, 0);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        long beginOfToday = c.getTimeInMillis();

        date = (date < beginOfToday) ? beginOfToday : date;

        mCurrentSearch = new SearchParams(date, stationFromCode, stationFromName, stationToCode, stationToName);
    }

    private void startSearch(boolean force) {
        if (mCurrentSearch.fromCode.isEmpty() || mCurrentSearch.toCode.isEmpty()) return;

        mToolbar.setSubtitle(mCurrentSearch.fromName + " - " + mCurrentSearch.toName);

        doSearchRequest(force);
    }

    private void init_Tabs() {
        mProgressBar = (ProgressBar) findViewById(R.id.toolbar_progress_bar);

        if (mProgressBar.getIndeterminateDrawable() != null) {
            mProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), Mode.SRC_IN);
        }

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabsAdapter = new MainActivityTabsAdapter(getSupportFragmentManager());
        mTabsAdapter.addFragment(Util.getDateRepresentation(mCurrentSearch.date));
        mTabsAdapter.addFragment(Multilanguage.favorites, R.drawable.ic_action_important, true);
        mViewPager.setAdapter(mTabsAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabsAdapter.updateImages(this, mTabLayout);

        mTabLayout.setOnTabSelectedListener(new ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(Tab tab) {
                super.onTabSelected(tab);
                mTabsAdapter.onTabClick(tab.getPosition());
            }

            @Override
            public void onTabReselected(Tab tab) {
                super.onTabReselected(tab);
                mTabsAdapter.onTabClick(tab.getPosition());
            }
        });

        startSearch(false);
    }

    private void init_FAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearchActivity();
            }
        });
    }

    private void startSearchActivity() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, Search_Activity.class);
        intent.putExtra(Search_Activity.ARGS_FROM_CODE, mCurrentSearch.fromCode);
        intent.putExtra(Search_Activity.ARGS_FROM_NAME, mCurrentSearch.fromName);
        intent.putExtra(Search_Activity.ARGS_TO_CODE, mCurrentSearch.toCode);
        intent.putExtra(Search_Activity.ARGS_TO_NAME, mCurrentSearch.toName);
        intent.putExtra(Search_Activity.ARGS_DATE, mCurrentSearch.date);
        startActivityForResult(intent, SEARCH_ACTIVITY_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCH_ACTIVITY_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle args = data.getExtras();
                if (args == null) return;
                mCurrentSearch.fromCode = args.getString(Search_Activity.ARGS_FROM_CODE, "");
                mCurrentSearch.fromName = args.getString(Search_Activity.ARGS_FROM_NAME, "");
                mCurrentSearch.toCode = args.getString(Search_Activity.ARGS_TO_CODE, "");
                mCurrentSearch.toName = args.getString(Search_Activity.ARGS_TO_NAME, "");
                mCurrentSearch.date = args.getLong(Search_Activity.ARGS_DATE, 0);

                int pageIndex = mViewPager.getCurrentItem();
                String pageTitle = Util.getDateRepresentation(mCurrentSearch.date);
                mTabsAdapter.setTitle(pageIndex, pageTitle);
                Tab tab = mTabLayout.getTabAt(0);
                if (tab != null) {
                    tab.setText(pageTitle);
                    tab.select();
                }

                startSearch(true);
            }
            mTabsAdapter.updateFavorites();
        }
    }

    @Override
    public void onFavoriteClick(Favorite favorite) {
        mCurrentSearch.fromCode = favorite.fromCode;
        mCurrentSearch.fromName = favorite.fromName;
        mCurrentSearch.toCode = favorite.toCode;
        mCurrentSearch.toName = favorite.toName;

        Tab tab = mTabLayout.getTabAt(0);
        if (tab != null) {
            tab.select();
        }

        startSearch(false);
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
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        Editor ed = sPref.edit();

        ed.clear();
        ed.putString(SETTINGS_STATION_FROM_NAME, mCurrentSearch.fromName);
        ed.putString(SETTINGS_STATION_FROM_CODE, mCurrentSearch.fromCode);
        ed.putString(SETTINGS_STATION_TO_NAME, mCurrentSearch.toName);
        ed.putString(SETTINGS_STATION_TO_CODE, mCurrentSearch.toCode);
        ed.putLong(SETTINGS_DATE, mCurrentSearch.date);

        ed.apply();

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            startSearch(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doSearchRequest(boolean force) {
        doSearchRequest(1, force);
    }

    private void doSearchRequest(int page, boolean force) {
        if (page == 1 && mSpiceManager.isStarted()) mSpiceManager.cancelAllRequests();

        if (force) {
            loadFromNetwork(mCurrentSearch, page);
        } else {
            loadFromCache(mCurrentSearch, page);
        }
    }

    private void loadFromNetwork(SearchParams searchParams, int page) {
        mProgressBar.setVisibility(View.VISIBLE);

        SpiceRaspRequest mRequest = new SpiceRaspRequest(searchParams.fromCode, searchParams.toCode, Util.getDateForSearch(searchParams.date), page);
        mSpiceManager.execute(mRequest, mRequest.createCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new SearchRequestResultListener());
    }

    private void loadFromCache(SearchParams searchParams, int page) {
        SpiceRaspRequestCached mRequestCached = new SpiceRaspRequestCached(searchParams.fromCode, searchParams.toCode, searchParams.dayWeek, page);
        mSpiceManager.getFromCache(RaspRequest.class, mRequestCached.createCacheKey(), DurationInMillis.ALWAYS_RETURNED, new SearchRequestResultListenerCached(searchParams, page));
    }

    private void putInCache(RaspRequest request, SearchParams search) {
        SpiceRaspRequestCached mRequestCached = new SpiceRaspRequestCached(mCurrentSearch.fromCode, mCurrentSearch.toCode, mCurrentSearch.dayWeek, request.page);
        mSpiceManager.putInCache(mRequestCached.createCacheKey(), request);
    }

    private class SearchRequestResultListener implements RequestListener<RaspRequest> {
        @Override
        public void onRequestFailure(SpiceException e) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (e instanceof RequestCancelledException) return;
            Snackbar.make(findViewById(R.id.fab), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }

        @Override
        public void onRequestSuccess(RaspRequest raspRequest) {
            mProgressBar.setVisibility(View.INVISIBLE);

            putInCache(raspRequest, mCurrentSearch);

            loadNextPage(raspRequest, true);
        }
    }

    private class SearchRequestResultListenerCached implements RequestListener<RaspRequest> {
        private int          page;
        private SearchParams mParentSearch;

        public SearchRequestResultListenerCached(SearchParams currentSearch, int page) {
            this.page = page;
            this.mParentSearch = currentSearch;
        }

        @Override
        public void onRequestFailure(SpiceException e) {
            if (e instanceof RequestCancelledException) return;
            loadFromNetwork(mParentSearch, page);
        }

        @Override
        public void onRequestSuccess(RaspRequest raspRequest) {
            if (raspRequest == null) {
                loadFromNetwork(mParentSearch, page);
            } else {
                loadNextPage(raspRequest, false);
            }
        }
    }

    private void loadNextPage(RaspRequest raspRequest, boolean force) {
        boolean hasNextPage = raspRequest.stopsList.hasNextPage();

        if (raspRequest.stopsList.isStartPage()) {
            mTabsAdapter.newSchedule(raspRequest.stopsList.getStops(), hasNextPage);
        } else {
            mTabsAdapter.appendSchedule(raspRequest.stopsList.getStops(), hasNextPage);
        }

        if (hasNextPage) {
            doSearchRequest(raspRequest.stopsList.getNextPage(), force);
        }
    }
}
