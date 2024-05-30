package com.multithreading.code;

import java.time.*;;

public class EventHandler implements Runnable{
    @Override
    public void run() {
        System.out.println("Event Handler is running");
        while(true){
            ZoneId zoneId = ZoneId.of("Asia/Manila");
            ZonedDateTime now = ZonedDateTime.now(zoneId);

            if(EventSchedulerRunner.events.size() > 0){
                System.out.println("Event list is not empty");
                Event currEvent = EventSchedulerRunner.events.get(0);
                if((now.isAfter(currEvent.getStart()) || now.isEqual(currEvent.getStart())) && (now.isBefore(currEvent.getEnd()) || now.isEqual(currEvent.getEnd()))){
                    Duration currDuration = Duration.between(currEvent.getStart(), now);
                    Duration totalDuration = Duration.between(currEvent.getStart(), currEvent.getEnd());

                    long progress = (currDuration.toMinutes() * 100) / totalDuration.toMinutes();
                    currEvent.setProgress((int)progress);
                    System.out.println("Progress: " + progress + "%");
                }

                if(now.isAfter(currEvent.getEnd())){
                    //tell main function that an event finished
                    EventSchedulerRunner.events.get(0).setStatus("Done");
                }
            }

            try{
                Thread.sleep(250);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
