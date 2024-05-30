package com.multithreading;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jline.console.ConsoleReader;

public class EventScheduler implements TextProperties, EventSchedulerInterface {
    //global variables
    //create a DateTimeFormatter format
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    //create an enum for the current user location
    private volatile UserLoc userLoc = UserLoc.MENU;
    //create a cursor location
    private volatile int cursorLoc = 0;
    //create a scanner object
    private Scanner scanner = new Scanner(System.in);
    //create a boolean for updating the screen
    private volatile boolean updateScreen = true;

    //create a list of events
    private  volatile List<Event> events = new ArrayList<>();
    //create temporary data holders for event
    private volatile String eventName;
    private volatile String eventStart;
    private volatile String eventEnd;

    @Override
    public  void removeEvent(int index){
        promptMaker("Event Removed Successfully!", false, true, true, SELECTED);
        events.remove(index);
    }

    @Override
    public  void addEvent(){
        promptMaker("Event Added Successfully!", false, true, true, SELECTED);
        events.add(new Event(eventName, LocalDateTime.parse(eventStart, format), LocalDateTime.parse(eventEnd, format)));
    }

    public void start() {
        //start input thread
        Thread inputThread = new Thread(new InputHandler());
        inputThread.start();
        // Thread eventThread = new Thread(new EventHandler());
        // eventThread.start();

        int millisecCounter = 0;
        //main loop
        while(userLoc != UserLoc.EXIT){
            //check if need to update screen
            if(updateScreen){
                //check the current user location and display the appropriate screen
                if(userLoc == UserLoc.MENU){
                    showMenu();

                    System.out.println("\n\nUse W and S to navigate and Enter to select.");
                } else if (userLoc == UserLoc.ADD){
                    showAddEvent();

                    System.out.println("\n\nUse W and S to navigate, Enter to select, and Esc to go back.");
                } else if(userLoc == UserLoc.ADD_NAME){
                    //call promptMaker to display the prompt
                    promptMaker("Name: " + eventName, true, false, false, NORMAL);
                
                } else if(userLoc == UserLoc.ADD_START){
                    //call promptMaker to display the prompt
                    promptMaker("Start [YYYY-MM-DD HH:MM]: " + eventStart, true, false, false, NORMAL);
                } else if(userLoc == UserLoc.ADD_END){
                    //call promptMaker to display the prompt
                    promptMaker("End [YYYY-MM-DD HH:MM]: " + eventEnd, true, false, false, NORMAL);
                } else if(userLoc == UserLoc.VIEW){
                    //call showView to display the events
                    showEvents();
                    
                    System.out.println("\n\nUse W and S to navigate and Enter or Esc to go back.");
                } else if(userLoc == UserLoc.REMOVE){
                    //call showView to display the events
                    showEvents();
                    
                    System.out.println("\n\nUse W and S to navigate, Backspace to remove, and Esc to go back.");
                }
                updateScreen = false;
            }

            //call the editEvent method to update the progress and status of the events
            editEvent();

            try {
                Thread.sleep(100);

                //automatically update the screen every 5 seconds
                if(userLoc != UserLoc.PAUSED)
                    if(millisecCounter >= 50){
                        updateScreen = true;
                        millisecCounter = 0;
                    } else 
                        millisecCounter += 1;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        //join the input thread to the main thread
        try {
            inputThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void editEvent(){
        //get current date and time
        LocalDateTime now = LocalDateTime.now();
            //check if there are events
            if(!events.isEmpty()){
                //loop through the events
                for(int eventCtr = 0; eventCtr < events.size(); eventCtr++){
                    //check if current event is done
                    if(events.get(eventCtr).getStatus().equals("Done"))
                        continue;
                    //check if the current date and time is between the start and end date and time of the event
                    if (!now.isBefore(events.get(eventCtr).getStart()) && !now.isAfter(events.get(eventCtr).getEnd())) {
                        //set the status of the event to ongoing
                        events.get(eventCtr).setStatus("Ongoing");

                        //calculate the progress of the event
                        Duration timeElapsed = Duration.between(events.get(eventCtr).getStart(), now);
                        Duration totalDuration = Duration.between(events.get(eventCtr).getStart(), events.get(eventCtr).getEnd());
                    
                        long progress = 100L * timeElapsed.toSeconds() / totalDuration.toSeconds();
                        events.get(eventCtr).setProgress((int) progress);

                    }
                    if(now.isAfter(events.get(eventCtr).getEnd())){
                        events.get(eventCtr).setStatus("Done");
                        events.get(eventCtr).setProgress(100);
                    } else{
                        //if the event is ongoing, skip the next events
                        break;
                    }
                }
            }
    }

    //method for displaying the menu
    private void showMenu(){
        clearConsole();
        //display them menu header
        System.out.println(NORMAL);
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║       >>>  Event Scheduler  <<<       ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║                                       ║");

        //call checkPromptState to display the menu prompts and check if the cursor is on the prompt
        checkPromptState(" View Events", 0);
        checkPromptState(" Add Event", 1);
        checkPromptState(" Remove Event", 2);
        checkPromptState(" Exit", 3);
        
        System.out.println("║                                       ║");
        System.out.println("╚═══════════════════════════════════════╝\n");        
    }

    //method for checking the prompt state
    private void checkPromptState(String prompt, int promptLoc){
        //count the number of trailing spaces based on the length of the prompt
        int spaces = 35 - prompt.length();

        System.out.print("║ ");
        //check if the cursor is on the prompt
        if (cursorLoc == promptLoc){
            //change the color of the prompt if the cursor is on the prompt
            System.out.print(SELECTED + "  >" + prompt);
        } else{
            //display the prompt normally if the cursor is not on the prompt
            System.out.print(prompt + "   ");
        }

        //display the trailing spaces
        for(int counter = 0; counter < spaces; counter++){
            System.out.print(" ");
        }

        //display the border
        System.out.println(NORMAL + "║");
    }

    //method for displaying the add event screen
    private void showAddEvent(){
        clearConsole();
        //display the add event header
        System.out.println(NORMAL);
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║          >>>  Add Event  <<<          ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║                                       ║");
        //call checkPromptState to display the add event prompts and check if the cursor is on the prompt
        if(eventName == null)
            checkPromptState(" Name: (Empty)", 0);
        else 
            checkPromptState(" Name: " + eventName, 0);
        if(eventStart == null)
            checkPromptState(" Start: (Empty)", 1);
        else 
            checkPromptState(" Start: " + eventStart, 1);
        if(eventEnd == null)
            checkPromptState(" End: (Empty)", 2);
        else 
            checkPromptState(" End: " + eventEnd, 2);
        
        checkPromptState("", -1);

        checkPromptState(" Finish ", 3);

        //display the bottom border
        System.out.println("║                                       ║");
        System.out.println("╚═══════════════════════════════════════╝\n");
    }
    
    //method for creating a prompt
    public void promptMaker(String prompt, Boolean isTitle, boolean isCentered, boolean pause, String color) {
        clearConsole();
        // set font color to normal
        System.out.print(NORMAL);
        // get the length of the prompt
        int len = prompt.length();
        int maxSpace;

        // determine the type of prompt and change border types and length of spaces
        // accordingly
        if (isTitle) {
            // 39 is the number of spaces inside the box for titles
            maxSpace = 48 - len;
            System.out.println("\n╔════════════════════════════════════════════════╗");
            System.out.println(  "║                                                ║");
            System.out.print("║");
        } else {
            // 33 is the number of spaces inside the box of ordinary prompts
            maxSpace = 46 - len;
            System.out.println("\n   ┌──────────────────────────────────────────────┐");
            System.out.print("   │");
        }

        if(isCentered){
            // calculate where the title needs to be printed on
            int titleStart = maxSpace / 2;

            // loop through the number of spaces
            for (int spaceCtr = 1; spaceCtr <= maxSpace; spaceCtr++) {
                if (spaceCtr == titleStart)
                    // print the title if space counter is equal to the start title
                    System.out.print(color + " " + prompt);
                else
                    System.out.print(" ");
            }
        } else{
            System.out.print(color + " " + prompt);

            for (int spaceCtr = 1; spaceCtr < maxSpace; spaceCtr++) {
                System.out.print(" ");
            }
        }

        // determine the type of prompt and change border types accordingly and
        // reverting the color to normal
        if (isTitle) {
            System.out.print(NORMAL + "║\n");
            System.out.println("║                                                ║");
            System.out.println("╚════════════════════════════════════════════════╝\n");
        } else {
            System.out.print(NORMAL + "│\n");
            System.out.println("   └──────────────────────────────────────────────┘\n");
        }

        // check if need to pause the screen
        if(pause){
            UserLoc tempLoc = userLoc;
            userLoc = UserLoc.PAUSED;

            System.out.print("Press enter to continue...");
            scanner.nextLine();
            updateScreen = true;

            userLoc = tempLoc;
        }
    }

    //method for creating a view section
    @Override
    public  void showEvents(){

        clearConsole();
        System.out.println(NORMAL);
        //display headers
        System.out.println("╔══════════════════════════╦══════════════════════════════════════╦════════════════════╦══════════╗");
        System.out.println("║        Event Name        ║            Start and End             ║      Progress      ║  Status  ║");          
        System.out.println("╠══════════════════════════╬══════════════════════════════════════╬════════════════════╬══════════╣");
        //sample output
        //System.out.println("║ 1. Morning Run           ║ 2022:02:02 02:02 to 2022:02:02 02:02 ║█████████           ║ Ongoing  ║");

        boolean highlighted = false;
        //loop through the events and display them
        for(int counter = 0; counter < events.size(); counter++){

            //check if the event is being selected then change color
            if(counter == cursorLoc){
                highlighted = true;
            } else{
                highlighted = false;
            }
            //call eventTabMaker to display the event/s
            eventTabMaker(counter + 1, events.get(counter), highlighted);
            
            //check if there are more events to display to display a divider
            if(counter < events.size() - 1){
                System.out.println(NORMAL + "╠══════════════════════════╬══════════════════════════════════════╬════════════════════╬══════════╣");
            }
        }
        //display the bottom border
        System.out.println(NORMAL + "╚══════════════════════════╩══════════════════════════════════════╩════════════════════╩══════════╝");       
    }

    //method for creating an event tab in the view section and remove section
    private void eventTabMaker(int id, Event event, boolean highlighted){
        System.out.print(NORMAL);
        
        String status = event.getStatus(); 
        String eventName = event.getName();
        String start = event.getStart().format(format); 
        String end = event.getEnd().format(format); 
        int progress = event.getProgress(); 
        
        if(status.equals("Waiting"))
            System.out.print(INACTIVE);
        
        if(highlighted){
            System.out.print(SELECTED);
        } 
        //check if the event name is too long
        if(eventName.length() >= 23){
            eventName = eventName.substring(0, 20) + "...";
        }

        //display the event name
        System.out.print("║ " + id + ". ");
        System.out.print(eventName);

        //add trailing spaces
        for(int counter = 0; counter < 22 - eventName.length(); counter++){
            System.out.print(" ");
        }

        //display the start and end date and time
        System.out.print("║ ");
        System.out.print(start + " to " + end);

        //display progress
        System.out.print(" ║");
        // Print the progress bar by 5 percent per block 
        for(int counter = 0; counter < 20; counter++){
            if(counter < progress / 5)
                System.out.print("█");
            else
                System.out.print(" ");
        }
        System.out.print("║");

        //display the status
        if(status.equals("Done")){
            System.out.print("   Done   ");
        } else if(status.equals("Ongoing")){
            System.out.print(" Ongoing  ");
        } else{
            System.out.print(" Waiting  ");
        }
        System.out.println("║");
    }

    //method for clearing the console (stolen from the internet :D )
    private void clearConsole() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); // Clear the console
        } catch (Exception e) {
            try {
                System.out.println("Failed to clear console. Attempting to clear console using alternative method...");
                Runtime.getRuntime().exec("clear");
            } catch (IOException e1) {
                //e1.printStackTrace();
                System.out.println("Unable to clearConsole the screen. Using the final method...");
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        }
    }
    
    //method for checking if the date and time is valid
    private boolean isValidDateTime(CharSequence dateTime){
        try{
            LocalDateTime.parse(dateTime, format);
            return true;
        } catch(Exception e){
            return false;
        }
    }

    //input handler class
    //Lilipat ko po sana into different class kaso ang hirap (nakakatamad) mag reference na mga variables hehe
    private class InputHandler implements Runnable{
        //set default prompt number (in menu)
        private int promptNum = 4;

        //run method
        @Override
        public void run(){
            //encapsulate the input handling in a try-catch block
            try{
                //create a console reader
                ConsoleReader reader = new ConsoleReader();
                reader.setExpandEvents(false);
                
                //loop for handling input
                while(userLoc != UserLoc.EXIT){
                    //read the input
                    int input = reader.readCharacter();

                    //check if input is not empty
                    if(input == 0)
                        continue;
                    
                    //check the current user location if their are in the ADD EVENT section
                    if(userLoc == UserLoc.ADD_NAME || userLoc == UserLoc.ADD_START || userLoc == UserLoc.ADD_END){
                        //check if the input is backspace or delete
                        if(input == '\b' || input == 127){
                            //check the current user location and remove the last character of the input
                            if(userLoc == UserLoc.ADD_NAME && eventName.length() > 0)
                                eventName = (eventName.substring(0, eventName.length() - 1));
                            else if(userLoc == UserLoc.ADD_START && eventStart.length() > 0)
                                eventStart = (eventStart.substring(0, eventStart.length() - 1));
                            else if(userLoc == UserLoc.ADD_END && eventEnd.length() > 0)
                                eventEnd = (eventEnd.substring(0, eventEnd.length() - 1));
                            
                            //update the screen
                            updateScreen = true;
                        } 
                        //check if the input is enter or return or tab
                        else if(input == '\r' || input == '\n' || input == '\t'){
                            //check the current user location and validate the input
                            if(userLoc == UserLoc.ADD_NAME && eventName.length() <= 0){
                                //show a prompt saying the error and pause the thread
                                promptMaker("Name should have at least 1 character!", false, true, true, WARN);
                            }
                            //check if the start date and time is valid        
                            else if(userLoc == UserLoc.ADD_START){
                                //show a prompt saying the error and pause the thread if the start date and time is invalid
                                if(!isValidDateTime(eventStart))
                                    promptMaker("Start Date and Time is invalid!", false, true, true, WARN);
                                //check if the end date and time of the last event does not overlap with the start date and time of the new event
                                else if(!events.isEmpty() && LocalDateTime.parse(eventStart, format).isBefore(events.get(events.size() - 1).getEnd())){
                                    promptMaker("Start Date overlaps with previous event!", false, true, true, WARN);
                                } else{
                                    //move back to the add event section if the start date and time is valid
                                    userLoc = UserLoc.ADD;
                                }
                            }
                            //check if the end date and time is valid
                            else if(userLoc == UserLoc.ADD_END)
                                //check if the end date and time is valid
                                if(!isValidDateTime(eventEnd)){
                                    promptMaker("End Date and Time is invalid!", false, true, true, WARN);
                                } 
                                //check if the start date and time is valid or not empty
                                else if(!isValidDateTime(eventStart)){
                                    promptMaker("Please Enter the Start first!", false, true, true, WARN);
                                }
                                //check if the end date and time is before the start date and time
                                else if(LocalDateTime.parse(eventEnd, format).isBefore(LocalDateTime.parse(eventStart, format))){
                                    promptMaker("End Date is after Start Date!", false, true, true, WARN);
                                } else{
                                    //move back to the add event section if the end date and time is valid
                                    userLoc = UserLoc.ADD;
                                }
                            else{
                                //move back to the add event section
                                userLoc = UserLoc.ADD;
                            }
                            
                            updateScreen = true;
                        } 
                        //check if the input is a valid character
                        else if(input >= 32 && input <= 126){
                            //check the current user location and add the input to the specific temporary event variable
                            if(userLoc == UserLoc.ADD_NAME && eventName.length() < 30)
                                eventName += (char) input;
                            else if(userLoc == UserLoc.ADD_START)
                                eventStart += (char) input;
                            else if(userLoc == UserLoc.ADD_END)
                                eventEnd += (char) input;
                            updateScreen = true;
                        } else if(input == 27){

                            userLoc = UserLoc.ADD;
                            updateScreen = true;
                        }
                    }
                    //check if the user location is in the menu or view section
                    else if(userLoc == UserLoc.MENU || userLoc == UserLoc.ADD || userLoc == UserLoc.VIEW || userLoc == UserLoc.REMOVE){
                        //check if the input is w and if the cursor location is greater than 0
                        if(input == 'w' && cursorLoc > 0){
                            //change the cursor location and update the screen
                            cursorLoc--;
                            updateScreen = true;
                        } 
                        //check if the input is s and if the cursor location is less than the prompt number 
                        else if(input == 's' && cursorLoc < promptNum - 1){
                            //change the cursor location and update the screen
                            cursorLoc++;
                            updateScreen = true;
                        } 
                        //check if the input is enter or return
                        else if(input == '\r' || input == '\n'){
                            //check the current user location
                            if(userLoc == UserLoc.MENU){
                                // determine the cursor location and change the user location accordingly
                                switch (cursorLoc) {
                                    case 0:
                                        //go to the view section
                                        userLoc = UserLoc.VIEW;
                                        promptNum = events.size();
                                        break;
                                    case 1:
                                        //go to the add event section
                                        userLoc = UserLoc.ADD;
                                        promptNum = 4;
                                        break;
                                    case 2:
                                        //go to the remove section
                                        userLoc = UserLoc.REMOVE;
                                        promptNum = events.size(); 
                                        break;
                                    case 3:
                                        //exit the program
                                        promptMaker("Thank you for using our code!", true, true, false, SELECTED);
                                        userLoc = UserLoc.EXIT;
                                        break;
                                }
                            }
                            //check if the user location is in the add event section
                            else if(userLoc == UserLoc.ADD){
                                // determine the cursor location and change the user location accordingly
                                switch (cursorLoc) {
                                    //case 0 is for the name
                                    case 0:
                                        userLoc = UserLoc.ADD_NAME;
                                        //set the temporary event name to empty
                                        eventName = "";
                                        break;
                                    //case 1 is for the start date and time
                                    case 1:
                                        userLoc = UserLoc.ADD_START;
                                        //set the temporary event start to empty
                                        eventStart = "";
                                        break;
                                    //case 2 is for the end date and time
                                    case 2:
                                        userLoc = UserLoc.ADD_END;
                                        //set the temporary event end to empty
                                        eventEnd = "";
                                        break;
                                    //case 3 is for finishing the event
                                    case 3:
                                        //return to the menu and add the event to the list
                                        if(eventName == null || eventStart == null || eventEnd == null){
                                            promptMaker("Please fill out all fields!", false, true, true, WARN);
                                            break;
                                        }

                                        if(!isValidDateTime(eventStart) || !isValidDateTime(eventEnd)){
                                            promptMaker("Invalid Date and Time!", false, true, true, WARN);
                                            break;
                                        }

                                        userLoc = UserLoc.MENU;
                                        addEvent();

                                        //set the temporary event variables to null
                                        eventName = null;
                                        eventStart = null;
                                        eventEnd = null;

                                        break;
                                }
                            }
                            //reset the cursor location and update the screen
                            else{
                                userLoc = UserLoc.MENU;
                                promptNum = 4;
                            }
                            cursorLoc = 0;
                            updateScreen = true;
                        } 
                        //check if the input is backspace and if the user location is in the remove section
                        else if(input == '\b' && userLoc == UserLoc.REMOVE){
                            //check if there are events to remove
                            if(!events.isEmpty()){
                                //remove the event and update the screen and the number of prompts (in this case the number of events)
                                removeEvent(cursorLoc);
                                if(cursorLoc > 0)
                                    cursorLoc--;
                                updateScreen = true;
                                promptNum = events.size();
                            }
                        }
                        //return to the menu if the input is escape
                        else if(input == 27){
                            userLoc = UserLoc.MENU;
                            eventName = null;
                            eventStart = null;
                            eventEnd = null;

                            updateScreen = true;
                            promptNum = 4;
                            cursorLoc = 0;
                        }
                        
                    }
                }
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
 
}