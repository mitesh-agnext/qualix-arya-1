package com.specx.scan.ui.result.base;

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

import java.util.List;

import butterknife.BindView;

@EpoxyModelClass(layout = R2.layout.item_result_table)
public abstract class ResultTableModel extends EpoxyModelWithHolder<ResultTableModel.Holder> {

    @EpoxyAttribute BaseView view;
    @EpoxyAttribute List<AnalysisItem> analyses;

    @Override
    public void bind(@NonNull Holder holder) {
        holder.table.removeAllViews();
        for (AnalysisItem analysis : analyses) {
            TableRow row = (TableRow) LayoutInflater.from(view.context()).inflate(R.layout.item_result_row, null);
            final TextView name = row.findViewById(R.id.tv_name);
            final TextView result = row.findViewById(R.id.tv_result);
            name.setText(analysis.getName().toUpperCase());
            result.setText(String.format("%s %s", analysis.getTotalAmount(), analysis.getAmountUnit()));
            holder.table.addView(row);
        }
    }

    static class Holder extends BaseHolder {

        @BindView(R2.id.tab_layout)
        TableLayout table;

    }
}