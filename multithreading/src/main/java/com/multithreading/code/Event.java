package com.multithreading.code;

import java.time.*;

class Event{
    protected String name;
    protected ZonedDateTime start;
    protected LocalDateTime end;
    protected int progress;
    protected String status;

    public Event(String name, LocalDateTime start, LocalDateTime end){
        this.name = name;
        this.start = start;
        this.end = end;
        this.progress = 0;
        this.status = "Ongoing";
    }

    public Event(){

    }

    public void setName(String name){
        this.name = name;
    }

    public void setStart(LocalDateTime start){
        this.start = start;
    }

    public void setEnd(LocalDateTime end){
        this.end = end;
    }

    public void setProgress(int progress){
        this.progress = progress;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

    public int getProgress(){
        return progress;
    }

    public String getName(){
        return name;
    }

    public LocalDateTime getStart(){
        return start;
    }

    public LocalDateTime getEnd(){
        return end;
    }   
}