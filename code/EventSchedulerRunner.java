import java.io.IOException;
import java.util.*;

class EventSchedulerRunner implements TextProperties{
    private static UserLoc userLoc = UserLoc.MENU;
    private static int cursorLoc = 0;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean blink = false;

        Thread inputThread = new Thread(new InputHandler());
        inputThread.start();

        System.out.println("Main started");
        while(userLoc != UserLoc.EXIT){
            if(userLoc == UserLoc.MENU){
                showMenu(blink);
                blink = !blink;
            }
            
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }

        try {
            inputThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void showMenu(boolean blink){
        clearConsole();
        System.out.println(NORMAL);
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║       >>>  Event Scheduler  <<<       ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║                                       ║");

        checkPromptState(" View Events", 0, blink);
        checkPromptState(" Add Event", 1, blink);
        checkPromptState(" Remove Event", 2, blink);
        checkPromptState(" Exit", 3, blink);
        
        System.out.println("║                                       ║");
        System.out.println("╚═══════════════════════════════════════╝\n");        
    }

    private static void checkPromptState(String prompt, int promptLoc, boolean blink){
        int spaces = 35 - prompt.length();

        System.out.print("║ ");
        if (cursorLoc == promptLoc){
            if (blink)
                System.out.print(SELECTED + prompt + "   ");
            else
                System.out.print(SELECTED + prompt + " <<");
        } else
            System.out.print(prompt);

        for(int counter = 0; counter < spaces; counter++){
            System.out.print(" ");
        }

        System.out.println(NORMAL + "║\n");
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
    
    private static class InputHandler implements Runnable{
        @Override
        public void run(){
            System.out.println("input thread started");
            try{
                while(userLoc == UserLoc.MENU){
                    if(System.in.available() > 0){
                        char input;
                        input = (char) System.in.read();
                        
                        if(input == 'w' && cursorLoc > 0)
                            cursorLoc--;
                        else if(input == 's' && cursorLoc < 3)
                            cursorLoc++;
                        else if(input == '\r' || input == '\n'){
                            switch (cursorLoc) {
                                case 0:
                                    userLoc = UserLoc.VIEW;
                                    break;
                                case 1:
                                    userLoc = UserLoc.ADD;
                                    break;
                                case 2:
                                    userLoc = UserLoc.REMOVE;
                                    break;
                                case 3:
                                    userLoc = UserLoc.EXIT;
                                    break;

                            }
                        }
                    }
                }
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    
}