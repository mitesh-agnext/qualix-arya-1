package com.specx.scan.ui.result.base;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.base.app.ui.base.BaseView;
import com.base.app.ui.epoxy.BaseHolder;
import com.specx.scan.R;
import com.specx.scan.R2;
import com.specx.scan.data.model.analysis.AnalysisItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@EpoxyModelClass(layout = R2.layout.item_result_table)
public abstract class ResultTableModel extends EpoxyModelWithHolder<ResultTableModel.Holder> {

    @EpoxyAttribute
    BaseView view;
    @EpoxyAttribute
    List<AnalysisItem> analyses;

    ArrayList<Double> rangeList = new ArrayList<>();

    @Override
    public void bind(@NonNull Holder holder) {
        holder.table.removeAllViews();
        rangeList.add(0.0);
        rangeList.add(70.0);
        rangeList.add(8.0);
        rangeList.add(2.0);
        rangeList.add(4.0);
        rangeList.add(2.0);
        rangeList.add(4.0);

        for (AnalysisItem analysis : analyses) {
            TableRow row = (TableRow) LayoutInflater.from(view.context()).inflate(R.layout.item_result_row, null);
            final TextView name = row.findViewById(R.id.tv_name);
            final TextView result = row.findViewById(R.id.tv_result);
//            final TextView range = row.findViewById(R.id.tv_range);

            name.setText(analysis.getName().toUpperCase());
            result.setText(String.format("%s %s", analysis.getTotalAmount(), analysis.getAmountUnit()));
//            range.setText(rangeList.toString() + " %");

            holder.table.addView(row);


//            if (Integer.parseInt(analysis.getTotalAmount()) >= rangeList.get(1)){
//                result.setTextColor(Color.parseColor("#4CAF50"));
//            }
//            else {
//                result.setTextColor(Color.parseColor("#ff0000"));
//            }
//            if (Integer.parseInt(analysis.getTotalAmount()) >= rangeList.get(2)){
//                result.setTextColor(Color.parseColor("#4CAF50"));
//            }
//            else {
//                result.setTextColor(Color.parseColor("#ff0000"));
//            }
//            if (Integer.parseInt(analysis.getTotalAmount()) >= rangeList.get(3)){
//                result.setTextColor(Color.parseColor("#4CAF50"));
//            }
//            else {
//                result.setTextColor(Color.parseColor("#ff0000"));
//            }
//            if (Integer.parseInt(analysis.getTotalAmount()) >= rangeList.get(4)){
//                result.setTextColor(Color.parseColor("#4CAF50"));
//            }
//            else {
//                result.setTextColor(Color.parseColor("#ff0000"));
//            }
//            if (Integer.parseInt(analysis.getTotalAmount()) >= rangeList.get(5)){
//                result.setTextColor(Color.parseColor("#4CAF50"));
//            }
//            else {
//                result.setTextColor(Color.parseColor("#ff0000"));
//            }
//            if (Integer.parseInt(analysis.getTotalAmount()) >= rangeList.get(6)){
//                result.setTextColor(Color.parseColor("#4CAF50"));
//            }
//            else {
//                result.setTextColor(Color.parseColor("#ff0000"));
//            }

        }

    }

    static class Holder extends BaseHolder {

        @BindView(R2.id.tab_layout)
        TableLayout table;

    }
}