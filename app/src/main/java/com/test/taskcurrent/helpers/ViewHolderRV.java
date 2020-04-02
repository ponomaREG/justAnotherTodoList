package com.test.taskcurrent.helpers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test.taskcurrent.MainActivity;
import com.test.taskcurrent.R;
import com.test.taskcurrent.taskOfCurrentDay;

import java.util.ArrayList;
import java.util.List;

public class ViewHolderRV extends RecyclerView.Adapter<ViewHolderRV.ViewHolder>{
    private LayoutInflater inflater;
    private List<Task> tasks;
    private Activity activity;



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
        holder.itemView.setSelected(false);
    }

    public List<Task> getDataSet(){return this.tasks;}

    @Override
    public int getItemCount() {
        return tasks.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener, View.OnLongClickListener{
        TextView task;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.task);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setSelected(false);
        }

        @Override
        public void onClick(View v) {
            if(taskOfCurrentDay.isActionMode){
                if(((taskOfCurrentDay) activity).checkIfTaskAlreadyInCheckedTasks(tasks.get(getAdapterPosition()))){
                    ((taskOfCurrentDay) activity).removeTaskFromCheckedTasks(tasks.get(getAdapterPosition()));
                    v.setSelected(false);
                }else {
                    v.setSelected(true);
                    ((taskOfCurrentDay) activity).addInCheckedTasks(getAdapterPosition());
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(!taskOfCurrentDay.isActionMode) {
                AnotherHelpers.vibrateWhenClickIsLong(activity);
                taskOfCurrentDay.isActionMode = true;
                v.setSelected(true);
                ((taskOfCurrentDay) activity).startActionModeForTask();
                ((taskOfCurrentDay) activity).addInCheckedTasks(getAdapterPosition());
                return true;
            }
            return false;
        }
    }
}
