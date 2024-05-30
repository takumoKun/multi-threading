package com.multithreading;

public interface EventSchedulerInterface {
    public void addEvent();
    public void showEvents();
    public void removeEvent(int index);
    public void start();
}