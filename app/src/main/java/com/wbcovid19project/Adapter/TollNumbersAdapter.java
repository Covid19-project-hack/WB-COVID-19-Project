package com.wbcovid19project.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wbcovid19project.Models.Test_Labs;
import com.wbcovid19project.Models.Toll_Numbers;
import com.wbcovid19project.R;

import java.util.ArrayList;
import java.util.List;

public class TollNumbersAdapter extends RecyclerView.Adapter<TollNumbersAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Toll_Numbers> mToll_Numbers;

    public TollNumbersAdapter(Context context, List<Toll_Numbers> list) {
        mContext = context;
        mToll_Numbers = list;
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_toll_numbers, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        final Toll_Numbers test_labs = mToll_Numbers.get(position);

        holder.Name.setText(test_labs.getName());
        holder.Phone_Number.setText("Phone Number : "+test_labs.getPhone_number());
    }

    @Override
    public int getItemCount() {
        return mToll_Numbers.size();
    }



    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView Name, Phone_Number;
        public ImageViewHolder(View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.name);
            Phone_Number = itemView.findViewById(R.id.phone_number);
        }
    }

}