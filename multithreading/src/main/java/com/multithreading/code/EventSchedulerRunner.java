package com.multithreading.code;

import jline.console.ConsoleReader;
import java.io.Console;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class EventSchedulerRunner implements TextProperties{
    private static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static UserLoc prevUserLoc = UserLoc.MENU;
    private static UserLoc userLoc = UserLoc.MENU;
    private static int cursorLoc = 0;
    private static Scanner scanner = new Scanner(System.in);
    private static boolean updateScreen = true;
    private static List<Event> events = new ArrayList<>();
    private static String eventName;
    private static String eventStart;
    private static String eventEnd;

    public static void main(String[] args) {

        Thread inputThread = new Thread(new InputHandler());
        inputThread.start();

        System.out.println("Main started");
        while(userLoc != UserLoc.EXIT){
            if(updateScreen){
                if(userLoc == UserLoc.MENU){
                    showMenu();
                } else if (userLoc == UserLoc.ADD){
                    showAddEvent();
                } else if(userLoc == UserLoc.ADD_NAME){
                    promptMaker("Name: " + eventName, true, false, false, NORMAL);
                } else if(userLoc == UserLoc.ADD_START){
                    promptMaker("Start [YYYY-MM-DD HH:MM]: " + eventStart, true, false, false, NORMAL);
                } else if(userLoc == UserLoc.ADD_END){
                    promptMaker("End [YYYY-MM-DD HH:MM]: " + eventEnd, true, false, false, NORMAL);
                }

                updateScreen = false;
            }

            try{
                Thread.sleep(100);
            } catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }

        try {
            inputThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void showMenu(){
        clearConsole();
        System.out.println(NORMAL);
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║       >>>  Event Scheduler  <<<       ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║                                       ║");

        checkPromptState(" View Events", 0);
        checkPromptState(" Add Event", 1);
        checkPromptState(" Remove Event", 2);
        checkPromptState(" Exit", 3);
        
        System.out.println("║                                       ║");
        System.out.println("╚═══════════════════════════════════════╝\n");        
    }

    private static void checkPromptState(String prompt, int promptLoc){
        int spaces = 35 - prompt.length();

        System.out.print("║ ");
        if (cursorLoc == promptLoc){
            System.out.print(SELECTED + "  >" + prompt);
        } else{
            System.out.print(prompt + "   ");
        }
        for(int counter = 0; counter < spaces; counter++){
            System.out.print(" ");
        }

        System.out.println(NORMAL + "║");
    }

    private static void showAddEvent(){
        clearConsole();
        System.out.println(NORMAL);
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║          >>>  Add Event  <<<          ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║                                       ║");
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


        System.out.println("║                                       ║");
        System.out.println("╚═══════════════════════════════════════╝\n");
    }
    //method for creating a prompt
    public static void promptMaker(String prompt, Boolean isTitle, boolean isCentered, boolean pause, String color) {
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
            maxSpace = 44 - len;
            System.out.println("\n╔════════════════════════════════════════════╗");
            System.out.println("║                                            ║");
            System.out.print("║");
        } else {
            // 33 is the number of spaces inside the box of ordinary prompts
            maxSpace = 40 - len;
            System.out.println("\n   ┌────────────────────────────────────────┐");
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
            System.out.println("║                                            ║");
            System.out.println("╚════════════════════════════════════════════╝\n");
        } else {
            System.out.print(NORMAL + "│\n");
            System.out.println("   └────────────────────────────────────────┘\n");
        }

        if(pause){
            System.out.print("Press enter to continue...");
            scanner.nextLine();
            updateScreen = true;
        }
    }

    private static void clearConsole() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); // Clear the console
        } catch (Exception e) {
            System.out.println("Unable to clearConsole the screen. Doing primitive way instead lol");
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }
    
    private static boolean isValidDateTime(CharSequence dateTime){
        try{
            LocalDateTime.parse(dateTime, format);
            return true;
        } catch(Exception e){
            return false;
        }
    }

    private static class InputHandler implements Runnable{
        private int promptNum = 4;
        @Override
        public void run(){
            try{
                ConsoleReader reader = new ConsoleReader();
                reader.setExpandEvents(false);
                while(true){
                    int input = reader.readCharacter();
                    if(userLoc == UserLoc.ADD_NAME || userLoc == UserLoc.ADD_START || userLoc == UserLoc.ADD_END){
                        if(input == '\b'){
                            if(userLoc == UserLoc.ADD_NAME && eventName.length() > 0)
                                eventName = (eventName.substring(0, eventName.length() - 1));
                            else if(userLoc == UserLoc.ADD_START && eventStart.length() > 0)
                                eventStart = (eventStart.substring(0, eventStart.length() - 1));
                            else if(userLoc == UserLoc.ADD_END && eventEnd.length() > 0)
                                eventEnd = (eventEnd.substring(0, eventEnd.length() - 1));
                            updateScreen = true;
                        } else if(input == '\r' || input == '\n'){
                            if(userLoc == UserLoc.ADD_NAME && eventName.length() <= 0)
                                    promptMaker("Name should have at least 1 character!", false, true, true, WARN);
                            else if(userLoc == UserLoc.ADD_START && !isValidDateTime(eventStart))
                                    promptMaker("Start Date and Time is invalid!", false, true, true, WARN);
                            else if(userLoc == UserLoc.ADD_END)
                                if(!isValidDateTime(eventEnd)){
                                    promptMaker("End Date and Time is invalid!", false, true, true, WARN);
                                } else if(!isValidDateTime(eventStart)){
                                    promptMaker("Please Enter the Start first!", false, true, true, WARN);
                                }else {
                                    if(LocalDateTime.parse(eventEnd, format).isBefore(LocalDateTime.parse(eventStart, format))){
                                        promptMaker("End Date and Time should be after Start Date and Time!", false, true, true, WARN);
                                    }
                                }
                            else{
                                userLoc = UserLoc.ADD;
                            }
                            
                            updateScreen = true;
                        } else if(input >= 32 && input <= 126){
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
                    else if(userLoc == UserLoc.MENU || userLoc == UserLoc.ADD || userLoc == UserLoc.REMOVE){
                        if(input == 'w' && cursorLoc > 0){
                            cursorLoc--;
                            updateScreen = true;
                        } else if(input == 's' && cursorLoc < promptNum - 1){
                            cursorLoc++;
                            updateScreen = true;
                        } else if(input == '\r' || input == '\n'){
                            if(userLoc == UserLoc.MENU)
                                switch (cursorLoc) {
                                    case 0:
                                        userLoc = UserLoc.VIEW;
                                        promptNum = events.size();
                                        break;
                                    case 1:
                                        userLoc = UserLoc.ADD;
                                        promptNum = 4;
                                        break;
                                    case 2:
                                        userLoc = UserLoc.REMOVE;
                                        promptNum = events.size(); 
                                        break;
                                    case 3:
                                        userLoc = UserLoc.EXIT;
                                        break;

                                }
                            else if(userLoc == UserLoc.ADD){
                                switch (cursorLoc) {
                                    case 0:
                                        userLoc = UserLoc.ADD_NAME;
                                        eventName = "";
                                        break;
                                    case 1:
                                        userLoc = UserLoc.ADD_START;
                                        eventStart = "";
                                        break;
                                    case 2:
                                        userLoc = UserLoc.ADD_END;
                                        eventEnd = "";
                                        break;
                                    case 3:
                                        userLoc = UserLoc.MENU;
                                        eventName = null;
                                        eventStart = null;
                                        eventEnd = null;
                                        break;
                                }
                            }
                            cursorLoc = 0;
                            updateScreen = true;
                        } else if(input == 27){
                            userLoc = UserLoc.MENU;
                            eventName = null;
                            eventStart = null;
                            eventEnd = null;

                            updateScreen = true;
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