package com.test.taskcurrent.helpers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
        holder.task.setText(this.tasks.get(position).getTask());
        holder.icon.setImageDrawable((this.tasks.get(position).isDone()) ? activity.getResources().getDrawable(R.drawable.line_rv_set_undone):activity.getResources().getDrawable(R.drawable.line_rv_set_done));
        holder.itemView.setSelected(false);
        if(!this.isIconVisible) {
            holder.iconLL.setVisibility(View.INVISIBLE);
        }else holder.iconLL.setVisibility(View.VISIBLE);
    }

    public void setVisibilityOfIcon(boolean isIconVisible){
        this.isIconVisible = isIconVisible;
    }

    public List<Task> getDataSet(){return this.tasks;}

    @Override
    public int getItemCount() {
        return tasks.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener, View.OnLongClickListener{
        TextView task;
        ImageView icon;
        LinearLayout iconLL;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.task);
            iconLL = itemView.findViewById(R.id.line_llIcon);
            icon = itemView.findViewById(R.id.line_IconSetDoneOrUndone);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setSelected(false);
            icon.setOnClickListener(v -> {
                Log.d("IMAGE","!");
            });
        }

        @Override
        public void onClick(View v) {
            if(taskOfCurrentDay.isActionMode){
                if(((taskOfCurrentDay) activity).checkIfTaskAlreadyInCheckedTasks(tasks.get(getAdapterPosition()))){
                    ((taskOfCurrentDay) activity).removeTaskFromCheckedTasks(tasks.get(getAdapterPosition()),getAdapterPosition());
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
                ((taskOfCurrentDay) activity).addInCheckedTasks(getAdapterPosition());
                setVisibilityOfIcon(false);
                notifyDataSetChanged();
//                v.setSelected(true);
                return true;
            }
            return false;
        }


    }
}
