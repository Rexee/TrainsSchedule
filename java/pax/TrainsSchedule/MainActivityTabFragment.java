package pax.TrainsSchedule;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.Calendar;

import pax.TrainsSchedule.FavoritesListAdapter.OnRemoveClick;
import pax.TrainsSchedule.Model.Favorite;
import pax.TrainsSchedule.Model.Stop;
import pax.TrainsSchedule.Util.DB;


public class MainActivityTabFragment extends Fragment implements OnRemoveClick {

    public String  mTitle;
    public int     mImage;
    public boolean mIsFav;

    private ScheduleListAdapter  mSchedulesAdapter;
    private FavoritesListAdapter mFavoritesAdapter;
    private Stop.List            mStops;
    private RelativeLayout       mRootView;
    ScheduleSnappingLinearLayoutManager mLinearLayoutManager;

    private DB mainDB;

    private CurrentTime mCurrentTime;

    private final Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == 1) {
                scrollToCurrentTimePosition();
                return true;
            }

            return false;
        }
    });

    private class CurrentTime {
        public int currentTimeSec;
        public int currentPositionInStops;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (RelativeLayout) inflater.inflate(R.layout.main_activity_content, container, false);

        init_vars();
        init_Lists();

        return mRootView;
    }

    @Override
    public void onDestroyView() {
        if (mainDB != null) mainDB.close();
        super.onDestroyView();
    }

    private void init_vars() {
        if (!mIsFav) {
            mStops = new Stop.List(null);
            getCurrentTime();
        } else {
            mainDB = new DB(getActivity());
        }
    }

    private void init_Lists() {
        Activity activity = getActivity();

        RecyclerView mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview);
        mRecyclerView.addItemDecoration(new ScheduleListDividerItemDecoration(getActivity(), R.drawable.divider_horizontal_bright_opaque, false, false));
         mLinearLayoutManager = new ScheduleSnappingLinearLayoutManager(mRecyclerView.getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        if (mIsFav) {
            mFavoritesAdapter = new FavoritesListAdapter(activity, this);
            mFavoritesAdapter.mFavorites = mainDB.getFavoritesList();
            mRecyclerView.setAdapter(mFavoritesAdapter);
        } else {
            mSchedulesAdapter = new ScheduleListAdapter(activity, mStops, mCurrentTime.currentPositionInStops, mCurrentTime.currentTimeSec);
            mRecyclerView.setAdapter(mSchedulesAdapter);
        }
    }

    private void scrollToCurrentTimePosition() {
        getCurrentTime();
        mSchedulesAdapter.setCurrentTime(mCurrentTime.currentPositionInStops, mCurrentTime.currentTimeSec);
        mLinearLayoutManager.scrollToPositionWithOffset(mCurrentTime.currentPositionInStops, 0);
//        mRecyclerView.smoothScrollToPosition(mCurrentTime.currentPositionInStops);
    }

    public void updateFavorites() {
        mFavoritesAdapter.mFavorites = mainDB.getFavoritesList();
        mFavoritesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRemoveClick(int position) {
        Favorite favorite = mFavoritesAdapter.mFavorites.get(position);
        mainDB.deleteFavorite(favorite);
        mFavoritesAdapter.mFavorites.remove(position);
        mFavoritesAdapter.notifyDataSetChanged();
    }

    private void getCurrentTime() {
        mCurrentTime = new CurrentTime();

        if (mStops == null || mStops.size() == 0) return;

        Calendar calendar = Calendar.getInstance();

        int timeInt = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        mCurrentTime.currentTimeSec = timeInt;

        for (int i = 0; i < mStops.size() - 1; i++) {
            if (mStops.get(i).departure >= timeInt) {
                mCurrentTime.currentPositionInStops = i;
                return;
            }
        }

        mCurrentTime.currentPositionInStops = mStops.size() - 1;
    }

    public void newSchedule(Stop.List stopsList, boolean hasNextPage) {
        mStops.newStops(stopsList);
        mSchedulesAdapter.notifyDataSetChanged();
        if (!hasNextPage) scrollToCurrentTimePositionRunnable();
    }

    public void appendSchedule(Stop.List stopsList, boolean hasNextPage) {
        mStops.addStops(stopsList);
        mSchedulesAdapter.notifyDataSetChanged();
        if (!hasNextPage) scrollToCurrentTimePositionRunnable();
    }

    private void scrollToCurrentTimePositionRunnable() {
        mHandler.sendMessage(mHandler.obtainMessage(1, this));
    }

    public void onSelect() {
        if (!mIsFav) scrollToCurrentTimePositionRunnable();
    }
}
