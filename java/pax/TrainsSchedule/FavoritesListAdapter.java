package pax.TrainsSchedule;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pax.TrainsSchedule.Model.Favorite;

public class FavoritesListAdapter extends RecyclerView.Adapter<FavoritesListAdapter.ViewHolder> {

    public interface OnRemoveClick {
        void onRemoveClick(int position);
    }

    public interface OnFavoriteClick {
        void onFavoriteClick(Favorite favorite);
    }

    final OnRemoveClick mOnRemoveClickCallback;
    final OnFavoriteClick mOnFavoriteClickCallback;

    public FavoritesListAdapter(Activity activity, MainActivityTabFragment context) {
        mOnRemoveClickCallback = context;
        mOnFavoriteClickCallback = (OnFavoriteClick) activity;
    }

    public List<Favorite> mFavorites;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView favText;
        public final ImageView favImg;

        public ViewHolder(View v) {
            super(v);

            favText = (TextView) v.findViewById(R.id.tvFavFrom);
            favImg = (ImageView) v.findViewById(R.id.imgFavClear);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_list_item, parent, false);
        final ViewHolder mViewHolder = new ViewHolder(view);

        mViewHolder.favImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnRemoveClickCallback.onRemoveClick(mViewHolder.getAdapterPosition());
            }
        });

        mViewHolder.favText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnFavoriteClickCallback.onFavoriteClick(mFavorites.get(mViewHolder.getAdapterPosition()));
            }
        });

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Favorite currentStop = mFavorites.get(position);
        holder.favText.setText(currentStop.fromName + " - " + currentStop.toName);
    }

    @Override
    public int getItemCount() {
        return mFavorites.size();
    }
}
