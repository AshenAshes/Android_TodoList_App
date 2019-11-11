package com.byted.camp.todolist.beans;

import java.util.Date;

public class Note {

    public final long id;
    private String deadline;
    private String show;
    private String scheduled;
    private String state;
    private int priority;
    private int week;
    private String caption;
    private String tag;
    private String content;
    private String filename;

    public Note(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getDeadline(){return deadline;}
    public void setDeadline(String deadline){this.deadline=deadline; }
    public String getShow(){return show;}
    public void setShow(String show){this.show=show;}
    public String getScheduled(){return scheduled;}
    public void setScheduled(){this.scheduled=scheduled;}
    public String getState(){return state;}
    public void setState(String state){this.state=state;}
    public int getPriority(){return priority;}
    public void setPriority(int priority){this.priority=priority;}
    public int getWeek(){return week;}
    public void setWeek(int week){this.week=week;}
    public String getCaption(){return caption;}
    public void setCaption(String caption){this.caption=caption;}
    public String getTag(){return tag;}
    public void setTag(String tag){this.tag=tag;}
    public String getFilename(){return filename;}
    public void  setFilename(String filename){this.filename=filename;}

}
