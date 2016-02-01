package pax.TrainsSchedule;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pax.TrainsSchedule.Model.Stop;
import pax.TrainsSchedule.Model.Stop.List;
import pax.TrainsSchedule.Util.Multilanguage;

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ViewHolder> {

    private Stop.List mStops;
    private int mCurrentTimePosition = -1;
    private int mCurrentTimeSec      = 0;
    private int mRegularColor        = -1;
    private int mExpressColor        = -1;
    private int mStopsColor          = -1;

    public ScheduleListAdapter(Context context, List mStops, int currentPositionInStops, int currentTimeSec) {
        this.mStops = mStops;

        this.mExpressColor = ContextCompat.getColor(context, R.color.textExpress);
        this.mRegularColor = ContextCompat.getColor(context, R.color.textMinor);
        this.mStopsColor = ContextCompat.getColor(context, R.color.textStops);

        this.mCurrentTimePosition = currentPositionInStops;
        this.mCurrentTimeSec = currentTimeSec;
    }

    public void setCurrentTime(int currentPositionInStops, int currentTimeSec) {
        mCurrentTimePosition = currentPositionInStops;
        mCurrentTimeSec = currentTimeSec;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView departure;
        public final TextView arrive;
        public final TextView duration;
        public final TextView exclude;
        public final TextView route;
        public final TextView timeToWait;

        public ViewHolder(View v) {
            super(v);

            departure = (TextView) v.findViewById(R.id.tvDeparture);
            arrive = (TextView) v.findViewById(R.id.tvArrive);
            exclude = (TextView) v.findViewById(R.id.tvExclude);
            route = (TextView) v.findViewById(R.id.tvRoute);
            duration = (TextView) v.findViewById(R.id.tvDuration);
            timeToWait = (TextView) v.findViewById(R.id.tvTimeToWait);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shedule_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Stop currentStop = mStops.get(position);

        if (mCurrentTimePosition >= 0 && position < mCurrentTimePosition) {
            holder.itemView.setAlpha(0.2f);
        } else {
            holder.itemView.setAlpha(1f);
        }

        holder.arrive.setText(getTimeStr(currentStop.arrival));
        holder.departure.setText(getTimeStr(currentStop.departure));
        holder.duration.setText("("+currentStop.duration+")");

        boolean visibleExclude = false;
        if (currentStop.exclude.isEmpty()) {
            holder.exclude.setVisibility(View.GONE);
        } else {
            visibleExclude = true;
            holder.exclude.setText(currentStop.exclude);
            holder.exclude.setVisibility(View.VISIBLE);
        }

        holder.route.setText(currentStop.route);
        if (currentStop.express) {
            holder.route.setTextColor(mExpressColor);
            if (visibleExclude) holder.exclude.setTextColor(mExpressColor);
        } else {
            holder.route.setTextColor(mRegularColor);
            if (visibleExclude) {
                holder.exclude.setTextColor(mStopsColor);
            }
        }

        int timeToWaitPos = position - mCurrentTimePosition;
        if (timeToWaitPos >= 0 && timeToWaitPos < 5) {
            holder.timeToWait.setText(getTimeToWaitStr(currentStop.departure));
        }
        else holder.timeToWait.setText("");
    }

    private String getTimeStr(int timeInMins) {
        int mins = timeInMins % 60;
        int hours = (timeInMins - mins) / 60;
        mins = timeInMins - hours * 60;

        String hourStr = "0" + hours;
        String minStr = "0" + mins;

        return hourStr.substring(hourStr.length() - 2) + ":" + minStr.substring(minStr.length() - 2);
    }

    public String getTimeToWaitStr(int departure) {
        int diff = departure - mCurrentTimeSec;
        int min = diff % 60;
        int hr = (diff - min) / 60;
        min = diff - hr * 60;

        if (hr == 0) return Multilanguage.in + " " + min + Multilanguage.min;
        return Multilanguage.in + " " + hr + Multilanguage.h + " " + min + Multilanguage.min;
    }

    @Override
    public int getItemCount() {
        return mStops.size();
    }
}
