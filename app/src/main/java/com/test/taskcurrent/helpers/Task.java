package com.test.taskcurrent.helpers;

public class Task {
    private int id;
    private String task;
    private boolean is_done;
    private boolean is_star;

    public Task(int id,String task,boolean is_done, boolean is_star){
        this.id = id;
        this.task = task;
        this.is_done = is_done;
        this.is_star = is_star;
    }
    public String getTask(){return this.task;}
    public int getID(){return this.id;}
    public boolean isDone(){return this.is_done;}
    public boolean isStar(){return this.is_star;}

    public void setDone(){this.is_done = true;}
    public void setUnDone(){this.is_done = false;}

    public void setStar(){this.is_star = true;}
    public void setUnStar(){this.is_star = false;}
}
