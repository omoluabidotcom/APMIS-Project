package de.symeda.sormas.app.report.adapter;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.databinding.RowWeeklyReportListItemLayoutBinding;
import de.symeda.sormas.app.report.viewmodel.WeeklyReportViewModel;

public class WeeklyReportAdapter extends DataBoundAdapter<RowWeeklyReportListItemLayoutBinding> {

    private static final String TAG = WeeklyReportAdapter.class.getSimpleName();

    private final Context context;
    private List<WeeklyReportViewModel> data;

    public WeeklyReportAdapter(Context context, List<WeeklyReportViewModel> data) {
        super(R.layout.row_weekly_report_list_item_layout);
        this.context = context;

        if (data == null)
            this.data = new ArrayList<>();
        else
            this.data = new ArrayList<>(data);
    }

    @Override
    protected void bindItem(DataBoundViewHolder<RowWeeklyReportListItemLayoutBinding> holder,
                            int position, List<Object> payloads) {

        WeeklyReportViewModel record = data.get(position);
        holder.setData(record);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}