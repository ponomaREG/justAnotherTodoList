package com.test.taskcurrent.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test.taskcurrent.R;
import com.test.taskcurrent.taskOfCurrentDay;

import java.util.List;

public class ViewHolderRV extends RecyclerView.Adapter<ViewHolderRV.ViewHolder>{
    private LayoutInflater inflater;
    private List<Task> tasks;
    private Activity activity;
    private boolean isIconVisible = true;



    public ViewHolderRV(Context context, List<Task> tasks){
        this.inflater = LayoutInflater.from(context);
        this.tasks = tasks;
        this.activity = (Activity) context;
    }

    @NonNull
    @Override
    public ViewHolderRV.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = this.inflater.inflate(R.layout.task_base_listview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderRV.ViewHolder holder, int position) {
        if(getDataSet().get(position).isDone()){
            holder.task.setPaintFlags(holder.task.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textViewLL.setBackground(activity.getResources().getDrawable(R.drawable.task_done_style));
        }else {
            holder.task.setPaintFlags(holder.task.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textViewLL.setBackground(activity.getResources().getDrawable(R.drawable.task_selector));
        }
        holder.task.setText(this.tasks.get(position).getTask());
        setCorrectImageToIcon(holder.icon,position);
        setOclToImageIcon(holder,position);
        holder.itemView.setSelected(false);
        if(!this.isIconVisible) {
            holder.iconLL.setVisibility(View.GONE);
        }else {
            holder.iconLL.setVisibility(View.VISIBLE);
            holder.itemView.findViewById(R.id.line_parentLL).setBackground(activity.getResources().getDrawable(R.drawable.task));
        }
    }

    public void setVisibilityOfIcon(boolean isIconVisible){
        this.isIconVisible = isIconVisible;
    }

    public List<Task> getDataSet(){return this.tasks;}

    @Override
    public int getItemCount() {
        return tasks.size();
    }


    private void setCorrectImageToIcon(ImageView icon_view, int position){
        icon_view.setImageDrawable((this.tasks.get(position).isDone()) ? activity.getResources().getDrawable(R.drawable.line_rv_set_undone):activity.getResources().getDrawable(R.drawable.line_rv_set_done));
    }

    private void setOclToImageIcon(ViewHolderRV.ViewHolder holder,int position){
        holder.icon.setOnClickListener(v -> {
            Task t = getDataSet().get(position);
            if(t.isDone()) t.setUnDone();
            else t.setDone();
            ((taskOfCurrentDay) activity).setDoneOrUndoneTaskByID(t.getID(),t.isDone());
            this.notifyDataSetChanged();
        });
    }


    class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener, View.OnLongClickListener{
        TextView task;
        ImageView icon;
        LinearLayout iconLL, textViewLL;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.task);
            iconLL = itemView.findViewById(R.id.line_llIcon);
            icon = itemView.findViewById(R.id.line_IconSetDoneOrUndone);
            textViewLL = itemView.findViewById(R.id.line_textview_ll);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if((taskOfCurrentDay.isActionMode)){
                if(((taskOfCurrentDay) activity).checkIfTaskAlreadyInCheckedTasks(tasks.get(getAdapterPosition()))){
                    ((taskOfCurrentDay) activity).removeTaskFromCheckedTasks(getAdapterPosition());
//                    v.setSelected(false);
                }else {
//                    v.setSelected(true);
                    ((taskOfCurrentDay) activity).addInCheckedTasks(getAdapterPosition());
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(!taskOfCurrentDay.isActionMode) {
                AnotherHelpers.vibrateWhenClickIsLong(activity);
                taskOfCurrentDay.isActionMode = true;
                ((taskOfCurrentDay) activity).startActionModeForTask();
//                ((taskOfCurrentDay) activity).addInCheckedTasks(getAdapterPosition());
//                int pos = getAdapterPosition();
                setVisibilityOfIcon(false);
                notifyDataSetChanged();
//                ((taskOfCurrentDay) activity).addInCheckedTasks(pos);
                return true;
            }
            return false;
        }


    }
}
