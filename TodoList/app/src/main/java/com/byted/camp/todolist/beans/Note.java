package com.byted.camp.todolist.beans;

import java.util.Date;

public class Note {
    public final long id;       //事件在数据库中存储条目对应的唯一id
    private String deadline;    //事件的截止日期
    private String show;        //事件的提醒日期
    private String scheduled;   //事件的记录日期
    private String state;       //事件的状态,分为Todo,Done和None
    private int priority;       //事件的优先级,由高到低为A,B,C,D,None
    private String caption;     //事件的标题
    private String tag;         //事件的tag
    private String content;     //事件的内容
    private String filename;    //事件所存储的文件名
    private String repeat;      //事件的重复类型,分为"不重复","每天","每周","每月"
    private String fatherItem;  //事件的父类的标题
    private String closed;      //事件被完成的时间
    private int week;           //事件的星期偏移量(not used?)

    public Note(long id) {
        this.id = id;
    }
    public long getID() { return id; }
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
    public void setScheduled(String scheduled){this.scheduled=scheduled;}
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
    public String getFatherItem(){return fatherItem;}
    public void setFatherItem(String fatherItem) { this.fatherItem = fatherItem; }
    public String getRepeat(){return repeat;}
    public void setRepeat(String repeat) { this.repeat = repeat; }
    public String getClosed(){return closed;}
    public void setClosed(String closed){this.closed=closed;}
}
