package com.multithreading;

import java.time.LocalDateTime;

class Event {
    protected String name;
    protected LocalDateTime start;
    protected LocalDateTime end;
    protected volatile int progress;
    protected String status;

    public Event(String name, LocalDateTime start, LocalDateTime end){
        this.name = name;
        this.start = start;
        this.end = end;
        this.progress = 0;
        this.status = "Waiting";
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

    public synchronized void setProgress(int progress){
        this.progress = progress;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

    public synchronized int getProgress(){
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