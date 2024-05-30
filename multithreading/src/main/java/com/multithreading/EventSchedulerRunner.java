/*  Members: Quennie Joy Almazan, Jasmin Latayan, Leonardo Magdarag Jr 
 *  Course: BSIT 2-1
 *  Date: May 30, 2024
 *  Description: A "simple" event scheduler that allows the user to add, view, and remove events.
 *  The program uses a console-based interface and uses a separate thread to update the progress 
 *  of the events as well as handling user inputs.
 * 
 * Note: Kung nababasa niyo po to sir... Sorry na po agad :D
 */

package com.multithreading;

public class EventSchedulerRunner {
    public static void main(String[] args) {
        EventScheduler eventScheduler = new EventScheduler();
        eventScheduler.start();
    }
       
}