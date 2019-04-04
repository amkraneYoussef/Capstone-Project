package chat.goulmima.com.letschat.DataAdapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import chat.goulmima.com.letschat.MessagingActivity;
import chat.goulmima.com.letschat.POJOS.AppUser;
import chat.goulmima.com.letschat.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class OnlineUsersAdapter extends RecyclerView.Adapter<OnlineUsersAdapter.OnlineUserViewHolder> {
    private static final String TAG = "OnlineUsersAdapter";
    public final ArrayList<AppUser> appUsers;
    private final Context context;
    @SuppressWarnings("unused")
    private OnUserClickListener onUserClickListener;

    public OnlineUsersAdapter(Context pContext){
        context = pContext;
        appUsers=new ArrayList<>();

        /*try {
            onUserClickListener = (OnUserClickListener) context;
        }catch(ClassCastException e)
        {
            throw new ClassCastException(context.toString() + "didn't implement OnStepClickListener.");
        }*/
    }


    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull OnlineUserViewHolder viewHolder, int i) {
        try{
            Uri uri = Uri.parse(appUsers.get(i).getPhotoUrl());
            if (uri != null)
            {
                Glide.with(context).load(uri).into(viewHolder.photo);
            }else
            {
                viewHolder.photo.setImageResource(R.drawable.profile);
            }

        }catch(Exception e)
        {
            Log.e(TAG,e.getMessage());
        }
        viewHolder.tv_username.setText(appUsers.get(i).getAppUserName());
        viewHolder.linearLayout.setOnClickListener(view -> {
            if(((Activity)context).findViewById(R.id.ll_tabletMode) == null)
            {
                Intent intent = new Intent(context,MessagingActivity.class);
                intent.putExtra(context.getString(R.string.USER_EXTRA),appUsers.get(i));
                context.startActivity(intent);
            }else
            {
                onUserClickListener.onUserClick(appUsers.get(i));
            }
        });
    }

    @NonNull
    @Override
    public OnlineUserViewHolder onCreateViewHolder(@NonNull ViewGroup itemView, int i) {
        return new OnlineUserViewHolder(LayoutInflater.from(itemView.getContext()).inflate(R.layout.rv_item_online_users,itemView,false));
    }

    @Override
    public int getItemCount() {
        return appUsers.size();
    }

    public class OnlineUserViewHolder extends RecyclerView.ViewHolder{
        final LinearLayout linearLayout;
        final CircleImageView photo;
        final TextView tv_username;
        OnlineUserViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.iv_profile_online);
            tv_username = itemView.findViewById(R.id.tv_username);
            linearLayout = itemView.findViewById(R.id.ll_container);
        }
    }
    public interface OnUserClickListener
    {
        void onUserClick(AppUser appUser);
    }
}
