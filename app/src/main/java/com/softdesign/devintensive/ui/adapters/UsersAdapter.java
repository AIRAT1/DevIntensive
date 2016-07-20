package com.softdesign.devintensive.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.manager.DataManager;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.ui.views.AspectRatioImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private static final String TAG = UsersAdapter.class.getSimpleName();
    private Context mContext;
    private List<User> mUsers;
    private UserViewHolder.CustomClickListener mCustomClickListener;

    private int minWidth, minHeight;

    public UsersAdapter(List<User> users, UserViewHolder.CustomClickListener customClickListener) {
        this.mUsers = users;
        this.mCustomClickListener = customClickListener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        minWidth = parent.getMeasuredWidth();
        minHeight = AspectRatioImageView.calculateHeight(minWidth, AspectRatioImageView.DEFAULT_ASPECT_RATIO);

        return new UserViewHolder(convertView, mCustomClickListener);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        final User user = mUsers.get(position);
        final String userPhoto;
        if (user.getPhoto().isEmpty()) {
            userPhoto = "null";
            Log.e(TAG, "onBindViewHolder: user with name " + user.getFullName() + " has empty photo");
        }else {
            userPhoto = user.getPhoto();
        }

        DataManager.getInstance().getPicasso()
                .load(userPhoto)
                .error(holder.mDummy)
                .placeholder(holder.mDummy)
//                .fit()
//                .resize(minWidth, minHeight)
//                .centerCrop()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.userPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, " load from cache");
                    }

                    @Override
                    public void onError() {
                        DataManager.getInstance().getPicasso()
                                .load(userPhoto)
                                .error(holder.mDummy)
                                .placeholder(holder.mDummy)
//                                .resize(minWidth, minHeight)
//                                .centerCrop()
                                .into(holder.userPhoto, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.d(TAG, "Could not fetch image");
                                    }
                                });
                    }
                });
        holder.mFullName.setText(user.getFullName());
        holder.mRating.setText(String.valueOf(user.getRating()));
        holder.mCodeLines.setText(String.valueOf(user.getCodeLines()));
        holder.mProjects.setText(String.valueOf(user.getProjects()));
        if (user.getBio() == null || user.getBio().isEmpty()) {
            holder.mBio.setVisibility(View.GONE);
        }else {
            holder.mBio.setVisibility(View.VISIBLE);
            holder.mBio.setText(user.getBio());
        }

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected AspectRatioImageView userPhoto;
        protected TextView mFullName, mRating, mCodeLines, mProjects, mBio;
        protected Button mShowMore;
        protected Drawable mDummy;

        private CustomClickListener mListener;

        public UserViewHolder(View itemView, CustomClickListener customClickListener) {
            super(itemView);
            this.mListener = customClickListener;

            userPhoto = (AspectRatioImageView) itemView.findViewById(R.id.user_photo_img);
            mFullName = (TextView) itemView.findViewById(R.id.user_full_name_txt);
            mRating = (TextView) itemView.findViewById(R.id.rating_txt);
            mCodeLines = (TextView) itemView.findViewById(R.id.code_lines_txt);
            mProjects = (TextView) itemView.findViewById(R.id.projects_txt);
            mBio = (TextView) itemView.findViewById(R.id.bio_text);
            mShowMore = (Button)itemView.findViewById(R.id.more_info_btn);
            mDummy = userPhoto.getContext().getResources().getDrawable(R.drawable.user_bg);

            mShowMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onUserItemClickListener(getAdapterPosition());
            }
        }

        public interface CustomClickListener {
            void onUserItemClickListener(int position);
        }
    }
}
