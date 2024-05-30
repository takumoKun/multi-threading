package com.multithreading.code;

import java.io.IOException;

public class testStuff {
    private static boolean running = true;
    private static int currentSelection = 0;
    private static final String[] menuItems = {"Option 1", "Option 2", "Option 3", "Exit"};

    public static void main(String[] args) {
        InputHandler inputHandler = new InputHandler(menuItems);
        Thread inputThread = new Thread(inputHandler);
        inputThread.start();

        while (running) {
            printMenu();
            try {
                Thread.sleep(100); // Update the menu every 100 milliseconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        try {
            inputThread.join(); // Wait for the input thread to finish
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void printMenu() {
        System.out.print("\033[H\033[2J"); // Clear the console
        System.out.flush();
        for (int i = 0; i < menuItems.length; i++) {
            if (i == currentSelection) {
                System.out.println("> " + menuItems[i]);
            } else {
                System.out.println("  " + menuItems[i]);
            }
        }
    }

    private static class InputHandler implements Runnable {
        private final String[] menuItems;

        public InputHandler(String[] menuItems) {
            this.menuItems = menuItems;
        }

        @Override
        public void run() {
            try {
                while (running) {
                    if (System.in.available() > 0) { // Check if there is input available
                        int ch = System.in.read();
                        switch (ch) {
                            case 'w': // move up
                                if (currentSelection > 0) {
                                    currentSelection--;
                                }
                                break;
                            case 's': // move down
                                if (currentSelection < menuItems.length - 1) {
                                    currentSelection++;
                                }
                                break;
                            case '\r': // enter key
                            case '\n':
                                if (menuItems[currentSelection].equals("Exit")) {
                                    running = false;
                                } else {
                                    System.out.println("Selected: " + menuItems[currentSelection]);
                                }
                                break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
