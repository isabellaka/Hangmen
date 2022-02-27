/*
 * Hangmen
 * This project allows the user to play several rounds of hangman, based on a list of words in a file. The file is passed as a command line parameter.
 * Author: Isabella Kainer
 * Last Change: 27/02/2022
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Hangmen {
    static private Scanner scanner = new Scanner(System.in);

    //reads file from path given as command line argument
    public static List readFile(String filepath) throws FileNotFoundException, IOException {
        List wordList = new ArrayList();
        BufferedReader buffer = new BufferedReader(new FileReader(filepath));
        String line;
        while ((line = buffer.readLine()) != null) {
            if (!line.isEmpty()) {
                wordList.add(line);
            }
            for (char c : line.toCharArray()) {
                if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
                    throw new IOException("Error: Corrupt file!");
                }
            }
        }

        return wordList;
    }

    //prints a round header, which contains a separator line and the round count
    public static void printRoundHeader(int roundCount) {
        System.out.println("-".repeat(80));
        System.out.printf("Word #%d:\n", roundCount);
    }

    //prints the guessed word with a space character between every character
    public static void printWord(String guessedWord) {
        System.out.println();

        System.out.print("Word:");

        for (char c : guessedWord.toCharArray()) {

            System.out.print(" " + c);
        }
        System.out.println();
    }

    //prints all misses that are saved in the passed list, the characters are divided by a comma and a space character
    public static void printMisses(List<Character> misses) {
        System.out.printf("Misses (%d/11)", misses.size());
        if (misses.size() > 0) {
            System.out.print(":");
            boolean firstChar = true;
            for (char c : misses) {
                if (firstChar) {
                    System.out.printf(" %c", c);
                    firstChar = false;
                } else {
                    System.out.printf(", %c", c);
                }

            }
            System.out.println();
        } else {
            System.out.println();
        }
    }

    //gets the user input for the guesses and returns the entered character
    public static char userInput(List alreadyGuessed) {

        System.out.print("Next guess: ");
        String input = scanner.nextLine();
        //if the user enters no character or more than 1, then a corresponding message is printed and the space character is returned; the main method interprets the space character as a flag to repeat the input process
        if (input.length() != 1) {
            System.out.println("\nInvalid input!");
            return ' ';
        } else {
            char currentGuess = input.charAt(0);
            //if the user enters a character that is not between A-Z or a-z, then a corresponding message is printed and the space character is returned; the main method interprets the space character as a flag to repeat the input process
            if (!((currentGuess >= 'A' && currentGuess <= 'Z') || (currentGuess >= 'a' && currentGuess <= 'z'))) {
                System.out.println("\nInvalid character!");
                return ' ';
            } else if (alreadyGuessed.contains(currentGuess)) { //if the user enters a character that has already been guessed (--> the alreadyGuessed-List contains this character), then a corresponding message is printed and the space character is returned; the main method interprets the space character as a flag to repeat the input process
                System.out.println("\nCharacter already guessed!");
                return ' ';
            } else {
                return currentGuess;
            }

        }


    }

    //draws the hangman; the drawing depends on the amount of misses
    public static void drawHangman(int amountMisses) {
        System.out.println();
        if (amountMisses >= 3) {
            System.out.println("  ____");
        }
        if (amountMisses >= 2) {
            for (int i = 1; i <= 4; i++) {
                System.out.print(" |");
                if (i == 1) {
                    if (amountMisses >= 4) {
                        System.out.print("/");
                    }
                    if (amountMisses >= 5) {
                        System.out.print("   |");
                    }
                }

                if (i == 2 && amountMisses >= 6) {
                    System.out.print("    O");
                }

                if (i == 3) {
                    if (amountMisses >= 7) {
                        System.out.print("   ");
                        if (amountMisses >= 10) {
                            System.out.print("/");
                        } else {
                            System.out.print(" ");
                        }
                        System.out.print("|");
                    }
                    if (amountMisses >= 11) {
                        System.out.print("\\");
                    }
                }

                if (i == 4) {
                    if (amountMisses >= 8) {
                        System.out.print("   /");
                    }
                    if (amountMisses >= 9) {
                        System.out.print(" \\");
                    }
                }
                System.out.println();
            }
        }
        if (amountMisses >= 1) {
            System.out.println("===");
        }
    }

    //main method
    public static void main(String[] args) {
        List wordList;
        try {
            wordList = readFile(args[0]);

            //if the file contains no words, then the wordList is empty and the error message "Empty file!" is printed; afterwards the program stops
            if (wordList.isEmpty()) {
                System.out.println("Error: Empty file!");
                return;
            }
        } catch (ArrayIndexOutOfBoundsException e) { //ArrayIndexOutOfBoundsException occurs, when we try to access an element of the array "args" (args[0]), but the array "args" doesn't contain any elements --> so the user has not given a command line parameter/filename
            System.out.println("Error: No file name given!");
            return;
        } catch (FileNotFoundException e) { //FileNotFoundException occurs, when file does not exist or the permission is denied --> differentiation between those two cases has to be done additionally
            File file = new File(args[0]);

            //if file exists, the exception was raised because permission was denied
            if (file.exists()) {
                System.out.println("Error: Could not read file!");
            } else {
                System.out.println("Error: File not found!");
            }
            return;
        } catch (IOException e) { //the method "readFile" might throw an IOException with the message "Corrupt file!"
            System.out.println(e.getMessage());
            return;
        }

        //prints game title and the amount of words
        System.out.println("=".repeat(80));
        System.out.printf("HANGMEN (%d Word(s))\n", wordList.size());

        //arranges all words in the list "wordList" randomly
        Collections.shuffle(wordList);
        String wordOfRound;
        String guessedWord;


        List<Character> misses = new ArrayList<Character>(); //list that saves all missed characters
        List<Character> alreadyGuessed = new ArrayList<Character>(); //list that saves all already guessed characters
        boolean roundFinished = false;
        int wins = 0;
        for (int roundCount = 1; roundCount <= wordList.size(); roundCount++) {
            printRoundHeader(roundCount);
            wordOfRound = (String) wordList.get(roundCount - 1);
            guessedWord = "_".repeat(wordOfRound.length());

            misses.clear(); //reset misses for every round
            alreadyGuessed.clear(); //reset alreadyGuessed for every round
            roundFinished = false; //reset roundFinished for every round
            while (!roundFinished) {

                drawHangman(misses.size());
                printWord(guessedWord);
                printMisses(misses);
                char currentGuess = userInput(alreadyGuessed);

                //as the space character is not a valid character for a word, it can be used as a red flag to check whether the userInput method failed or not; if the userInput method returns the space character, then the input process is repeated by skipping all following commands in the while loop by using "continue"
                if (currentGuess == ' ') {
                    continue;
                }

                int noMatch = 0; //counter for the amount of no matches
                //checks for every position of the wordOfRound whether the entered character matches with the character at that position and updates the guessedWord; it does not matter if the user enters the letter lower case or upper case
                for (int i = 0; i < wordOfRound.length(); i++) {
                    if (Character.toLowerCase(currentGuess) == wordOfRound.charAt(i)) {
                        if (wordOfRound.length() == 1) {
                            guessedWord = Character.toString(Character.toLowerCase(currentGuess));
                        } else if (i == 0) {
                            guessedWord = Character.toLowerCase(currentGuess) + guessedWord.substring(1);
                        } else if (i == wordOfRound.length() - 1) {
                            guessedWord = guessedWord.substring(0, i) + Character.toLowerCase(currentGuess);
                        } else {
                            guessedWord = guessedWord.substring(0, i) + Character.toLowerCase(currentGuess) + guessedWord.substring(i + 1);
                        }

                    } else if (Character.toUpperCase(currentGuess) == wordOfRound.charAt(i)) {
                        if (wordOfRound.length() == 1) {
                            guessedWord = Character.toString(Character.toUpperCase(currentGuess));
                        } else if (i == wordOfRound.length() - 1) {
                            guessedWord = guessedWord.substring(0, i) + Character.toUpperCase(currentGuess);
                        } else {
                            guessedWord = guessedWord.substring(0, i) + Character.toUpperCase(currentGuess) + guessedWord.substring(i + 1);
                        }
                    } else {
                        noMatch++;
                    }
                }
                if (noMatch == wordOfRound.length()) {
                    misses.add(Character.toUpperCase(currentGuess));
                }
                alreadyGuessed.add(Character.toLowerCase(currentGuess));
                alreadyGuessed.add(Character.toUpperCase(currentGuess));
                if (misses.size() >= 11) { //if the user makes 11 mistakes (misses), then he loses
                    drawHangman(11);
                    printWord(guessedWord);
                    printMisses(misses);
                    System.out.println("\nYOU LOSE!");
                    roundFinished = true;
                } else if (guessedWord.equals(wordOfRound)) { //if the guessed word of the user equals the wordOfRound, the user wins
                    drawHangman(misses.size());
                    printWord(guessedWord);
                    printMisses(misses);
                    System.out.println("\nYOU WIN!");
                    wins++;
                    roundFinished = true;
                }

            }

        }

        System.out.println("=".repeat(80));
        System.out.printf("WINS: %d/%d\n", wins, wordList.size());
        scanner.close();

    }
}
