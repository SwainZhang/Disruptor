package com.example.sqlitetest.db;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sqlitetest.R;

import java.util.ArrayList;

/**
 * Created by emery on 2017/5/8.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private ArrayList<User> mUserList;
    private Context mContext;

    public UserAdapter(ArrayList<User> userList, Context context){
        mUserList = userList;

        mContext = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_user,null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user = mUserList.get(position);
        holder.mTv_name.setText(user.name);
        holder.mTv_age.setText(user.age);
        holder.mTv_address.setText(user.address);
        holder.mTv_phone.setText(user.phone);
    }

    @Override
    public int getItemCount() {
        return mUserList==null?0:mUserList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private  TextView mTv_name;
        private TextView mTv_age;
        private  TextView mTv_address;
        private  TextView mTv_phone;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTv_name = (TextView) itemView.findViewById(R.id.tv_name);
            mTv_age = (TextView) itemView.findViewById(R.id.tv_age);
            mTv_address = (TextView) itemView.findViewById(R.id.tv_address);
            mTv_phone = (TextView) itemView.findViewById(R.id.tv_phone);
        }
    }
}
