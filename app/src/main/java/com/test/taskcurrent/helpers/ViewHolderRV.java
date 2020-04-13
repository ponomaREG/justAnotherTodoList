package com.test.taskcurrent.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
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
        if(getDataSet().get(position).isDone()){
            holder.task.setPaintFlags(holder.task.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textViewLL.setBackground(activity.getResources().getDrawable(R.drawable.task_selector));
//            moveDoneTaskInEndOfTheList(position);
//            holder.textViewLL.setBackground(activity.getResources().getDrawable(R.drawable.task_done_style));
        }else {
            holder.task.setPaintFlags(holder.task.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textViewLL.setBackground(activity.getResources().getDrawable(R.drawable.task_selector));
        }
        holder.view.setTag(getDataSet().get(position).getID());
        holder.task.setText(this.tasks.get(position).getTask());
        setCorrectImageToIcon(holder.icon,position);
        setOclToImageIcon(holder.icon,position);
        setCorrectImageToStar(holder.star,position);
        setOclToImageStar(holder.star,position);
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

    private void setOclToImageIcon(ImageView icon,int position){
        icon.setOnClickListener(v -> {
            Task t = getDataSet().get(position);
            if(t.isDone()) t.setUnDone();
            else {
                t.setDone();
//                moveDoneTaskInEndOfTheList(position);
            }
            ((taskOfCurrentDay) activity).setDoneOrUndoneTaskByID(t.getID(),t.isDone());
            this.notifyDataSetChanged();
        });
    }


    private void setCorrectImageToStar(ImageView star_view, int position){
        star_view.setImageDrawable(activity.getResources().getDrawable((getDataSet().get(position).isStar()) ? R.drawable.star_selected:R.drawable.star));
    }

    private void setOclToImageStar(ImageView icon, int position){
        icon.setOnClickListener(v->{
            Task t =getDataSet().get(position);
            if(t.isStar()) {
                t.setUnStar();
                moveTaskWithoutStarAtDataSetByOrder(position);
            }
            else {
                t.setStar();
                moveTaskWithStarAtDataSetByOrder(position);
            }
            ((taskOfCurrentDay) activity).setStarOrUnstarTaskByID(t.getID(),t.isStar());
            this.notifyDataSetChanged();
        });
    }

    private void moveTaskWithStarAtDataSetByOrder(int position){
        int index_of_first_task_without_star = findFirstTaskWithoutStar();
        Log.d("Index",index_of_first_task_without_star+"");
        if((index_of_first_task_without_star != -1)&&(position>=index_of_first_task_without_star)) {
            Task t = getDataSet().get(position);
            getDataSet().remove(position);
            getDataSet().add(index_of_first_task_without_star,t);
        }
    }

    private int findFirstTaskWithoutStar(){
        for(int i =0;i<getDataSet().size();i++){
            if(!getDataSet().get(i).isStar()){
                return i;
            }
        }
        return getDataSet().size()-1;
    }

    private void moveTaskWithoutStarAtDataSetByOrder(int position){
        int index_of_last_task_with_star = findLastTaskWithStar();
        if (index_of_last_task_with_star == -1) index_of_last_task_with_star = position;
        else if(!(index_of_last_task_with_star==getDataSet().size()-1)) index_of_last_task_with_star++;
        Task t = getDataSet().get(position);
        getDataSet().remove(position);
        getDataSet().add(index_of_last_task_with_star,t);
    }

//    private void moveDoneTaskInEndOfTheList(int position){
//        Task t = getDataSet().get(position);
//        getDataSet().remove(position);
//        getDataSet().add(getDataSet().size()-1,t);
//    }

    private int findLastTaskWithStar(){
        int index_of_last_star = -1;
        for(int i =0;i<getDataSet().size();i++){
            if(getDataSet().get(i).isStar()){
                index_of_last_star = i;

            }
        }
        return index_of_last_star;
    }


    class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener, View.OnLongClickListener{
        TextView task;
        ImageView icon, star;
        LinearLayout iconLL, textViewLL;
        View view;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            task = itemView.findViewById(R.id.task);
            iconLL = itemView.findViewById(R.id.line_llIcon);
            icon = itemView.findViewById(R.id.line_IconSetDoneOrUndone);
            textViewLL = itemView.findViewById(R.id.line_textview_ll);
            star = itemView.findViewById(R.id.line_star);
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
