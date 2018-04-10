package vn.com.mattana.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import vn.com.mattana.dms.R;
import vn.com.mattana.model.api.checkin.CalendarWorkDay;
import vn.com.mattana.model.api.checkin.ShowCalendarAgency;

public class CalendarWorkAdapter extends RecyclerView.Adapter<CalendarWorkAdapter.MyViewHolder> {

    List<CalendarWorkDay> calendarWorkDays;

    public CalendarWorkAdapter(List<CalendarWorkDay> data) {
        this.calendarWorkDays = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_work_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CalendarWorkDay data = calendarWorkDays.get(position);

        holder.dayOfWeek.setText(data.getDayOfWeek());
        holder.date.setText(data.getDate());

        String planContent = "";
        for (ShowCalendarAgency planItem : data.getPlan()) {
            planContent = planContent + planItem.getName() + "\nTarget: " + planItem.getTarget() + "\n--------------\n";
        }
        holder.planContent.setText(planContent);

        String workContent = "";

        for(ShowCalendarAgency workItem : data.getWork()) {
            workContent = workContent + workItem.getName() + "\n";
        }

        holder.workContent.setText(workContent);
    }

    @Override
    public int getItemCount() {
        return calendarWorkDays.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView dayOfWeek, date, planContent, workContent;

        public MyViewHolder(View view) {
            super(view);
            dayOfWeek = (TextView) view.findViewById(R.id.dayofweek);
            planContent = (TextView) view.findViewById(R.id.plancontent);
            date = (TextView) view.findViewById(R.id.date);
            workContent = (TextView) view.findViewById(R.id.workcontent);
        }
    }


}
