package com.multithreading.code;

import java.time.*;

class Event{
    protected String name;
    protected LocalDateTime start;
    protected LocalDateTime end;

    public Event(String name, LocalDateTime start, LocalDateTime end){
        this.name = name;
        this.start = start;
        this.end = end;
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