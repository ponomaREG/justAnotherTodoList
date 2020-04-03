package com.test.taskcurrent.helpers;

public class Task {
    private int id;
    private String task;
    private boolean is_done;

    public Task(int id,String task,boolean is_done){
        this.id = id;
        this.task = task;
        this.is_done = is_done;
    }
    public String getTask(){return this.task;}
    public int getID(){return this.id;}
    public boolean isDone(){return this.is_done;}

    public void setDone(){this.is_done = true;}
    public void setUnDone(){this.is_done = false;}
}
