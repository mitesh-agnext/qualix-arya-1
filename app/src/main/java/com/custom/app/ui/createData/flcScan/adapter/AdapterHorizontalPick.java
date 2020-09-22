package com.custom.app.ui.createData.flcScan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.custom.app.R;
import com.custom.app.util.Constants;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class AdapterHorizontalPick extends RecyclerView.Adapter<AdapterHorizontalPick.MyView> {

    ArrayList<String> sectionNameList;
    ArrayList<Integer> sectionImageList;
    HorizontalCallback mCallBack;
    Context context;
    private int pos;
    private int selectedPos = 0;
    int selectedPosition = 0;
    private boolean isExpanded;

    public AdapterHorizontalPick(ArrayList<String> sectionNameList, ArrayList<Integer> sectionImageList, HorizontalCallback mCallBack, Context context) {
        this.sectionNameList = sectionNameList;
        this.sectionImageList = sectionImageList;
        this.mCallBack = mCallBack;
        this.context = context;
        selectedPosition = Constants.secId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_horizontal_slider, parent, false);
        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyView holder, final int position) {
        holder.tvSectionId.setText(sectionNameList.get(position));
//        holder.itemView.setSelected(selectedPos == position);
        isExpanded = selectedPosition == position;

        if (isExpanded) {
            holder.ivItem.setColorFilter(ContextCompat.getColor(context, R.color.yellow), android.graphics.PorterDuff.Mode.MULTIPLY);
            Constants.secId = position;
        } else {
            holder.ivItem.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    selectedPosition = position;
                    Constants.secId = selectedPosition;

                } else {
                    selectedPosition = position;
                    Constants.secId = selectedPosition;

                }
                notifyDataSetChanged();
                mCallBack.horizontalItem(selectedPosition);

            }
        });
    }

    @Override
    public int getItemCount() {
        return sectionNameList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyView extends RecyclerView.ViewHolder {
        RoundedImageView ivItem;
        TextView tvSectionId;
        LinearLayout lnRow;

        public MyView(View itemView) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.factoryImage);
            tvSectionId = itemView.findViewById(R.id.factoryName);
            lnRow = itemView.findViewById(R.id.lnRow);
        }
    }

    public interface HorizontalCallback {
        void horizontalItem(int position);
    }

}