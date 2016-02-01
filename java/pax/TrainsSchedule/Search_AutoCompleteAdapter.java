package pax.TrainsSchedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

import pax.TrainsSchedule.API.SuggestRequest;
import pax.TrainsSchedule.Model.Station;
import pax.TrainsSchedule.Model.Station.List;

public class Search_AutoCompleteAdapter extends ArrayAdapter<Station> implements Filterable {

    public        ArrayList<Station> resultList;
    public        Station            mSelectedItem;
    protected     SpiceManager       mSpiceManager;
    private final LayoutInflater     mInflater;
    private       SuggestRequest     mRequest;
    private       Context            mContext;
    public        boolean            needAutocomplete;

    public Search_AutoCompleteAdapter(Context context, SpiceManager spiceManager) {
        super(context, R.layout.search_autocomplete_list_item);
        mSpiceManager = spiceManager;
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mSelectedItem = new Station("", "");
        needAutocomplete = true;
        this.setNotifyOnChange(false);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public Station getItem(int index) {
        return resultList.get(index);
    }

    static class ViewHolder {
        public TextView text;
        public TextView textDescr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View v = convertView;

        final Station item = getItem(position);

        if (v == null) {
            v = mInflater.inflate(R.layout.search_autocomplete_list_item, parent, false);

            holder = new ViewHolder();
            holder.text = (TextView) v.findViewById(R.id.suggetItem0);
            holder.textDescr = (TextView) v.findViewById(R.id.suggetItem1);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.text.setText(item.name);
        holder.textDescr.setText(item.descr);

        return v;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                autocompleteAsync(constraint);
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                Station station = (Station) resultValue;
                if (station == null) return "";

                return station.name;
            }
        };
    }

    private void autocompleteAsync(CharSequence input) {
        if (mRequest != null && !mRequest.isCancelled()) {
            mRequest.cancel();
        }

        if (!needAutocomplete || input == null || input.length() == 0) return;

        mRequest = new SuggestRequest(input.toString());
        mSpiceManager.execute(mRequest, mRequest.createCacheKey(), DurationInMillis.ALWAYS_RETURNED, new SuggestRequestResultListener());
    }

    private class SuggestRequestResultListener implements RequestListener<List> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            if (spiceException instanceof RequestCancelledException) return;
            Toast.makeText(mContext, spiceException.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(List suggests) {
            resultList = new ArrayList<>(suggests.size());
            for (int i = 0; i < suggests.size(); i++) {
                resultList.add(i, suggests.get(i));
            }
            notifyDataSetChanged();
        }
    }
}
