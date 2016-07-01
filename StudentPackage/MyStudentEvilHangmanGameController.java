package cs240.byu.edu.evilhangman_android.StudentPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by afjersta on 7/1/16.
 */
public class MyStudentEvilHangmanGameController implements StudentEvilHangmanGameController {
    //Consistent variables
    private int numberOfGuessesToStart;

    //State variables
    private GAME_STATUS gameStatus;

    private int numberOfGuessesLeft;

    private TreeSet<Character> usedLetters;

    private String currentWord;

    private TreeSet<String> currentWords;

    public MyStudentEvilHangmanGameController() {
        setNumberOfGuesses(0);
        setGameStatus(GAME_STATUS.NORMAL);
        resetNumberOfGuessesLeft();
        resetUsedLetters();
        this.currentWord = null;
        this.currentWords = new TreeSet<>();
    }

    @Override
    public GAME_STATUS getGameStatus() {
        return this.gameStatus;
    }

    private void setGameStatus(GAME_STATUS gameStatus) {
        this.gameStatus = gameStatus;
    }

    @Override
    public int getNumberOfGuessesLeft() {
        return this.numberOfGuessesLeft;
    }

    private void resetNumberOfGuessesLeft() {
        this.numberOfGuessesLeft = getNumberOfGuesses();
    }

    private void decrementNumberOfGuessesLeft() {
        if(getNumberOfGuessesLeft() > 0)
            this.numberOfGuessesLeft--;
    }

    @Override
    public String getCurrentWord() {
        return this.currentWord;
    }

    private void resetCurrentWord(int wordLength) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < wordLength; i++)
            sb.append('-');

        this.currentWord = sb.toString();
    }

    private String getKey(String word, Character c) {
        //TODO: Generate key
        return null;
    }

    private TreeSet<String> getCurrentWords() {
        return this.currentWords;
    }

    private void resetCurrentWords() {
        this.currentWords.clear();
    }

    @Override
    public Set<Character> getUsedLetters() {
        return this.usedLetters;
    }

    private void resetUsedLetters() {
        this.usedLetters = new TreeSet<>();
        getUsedLetters().clear();
    }

    private void addUsedLetter(Character c) {
        Character lower = Character.toLowerCase(c);
        if(!(getUsedLetters().contains(lower)))
            getUsedLetters().add(lower);
    }

    private boolean letterIsUsed(Character c) {
        try {
            return getUsedLetters().contains(Character.toLowerCase(c));
        } catch (Exception e) {
            return true;
        }
    }

    private int getNumberOfGuesses() {
        return numberOfGuessesToStart;
    }

    @Override
    public void setNumberOfGuesses(int numberOfGuessesToStart) {
        this.numberOfGuessesToStart = numberOfGuessesToStart;
    }

    @Override
    public void startGame(InputStreamReader dictionary, int wordLength) {
        setGameStatus(GAME_STATUS.NORMAL);
        resetNumberOfGuessesLeft();
        resetUsedLetters();
        resetCurrentWord(wordLength);
        resetCurrentWords();

        try {
            BufferedReader reader = new BufferedReader(dictionary);
            String line;

            while((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                if(line.length() == wordLength)
                    this.currentWords.add(line);
            }
        } catch (Exception e) {}
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        if(letterIsUsed(guess))
            throw new GuessAlreadyMadeException();

        addUsedLetter(guess);

        //Mux words into differing sets
        HashMap<String, TreeSet<String>> initialSets = new HashMap<>();
        for(String word : getCurrentWords()) {
            String key = getKey(word, guess);
            if(!initialSets.containsKey(key))
                initialSets.put(key, new TreeSet<String>());

            initialSets.get(key).add(word);
        }

        //If there's only one set, nothing has changed and we're done
        if(initialSets.size() == 1) {
            return getCurrentWords();
        }

        //Otherwise, select the largest set(s)
        int biggestGroupSize = 0;
        HashMap<String, TreeSet<String>> biggestSets = new HashMap<>();

        for(String key : initialSets.keySet()) {
            TreeSet<String> currentSet = initialSets.get(key);
            if(currentSet.size() > biggestGroupSize) {
                biggestGroupSize = currentSet.size();
                biggestSets.clear();
                biggestSets.put(key, currentSet);
            }
            else if(currentSet.size() == biggestGroupSize)
                biggestSets.put(key, currentSet);
        }

        //If that narrowed it to one set, change the current words and current word
        if(biggestSets.size() == 1) {
            for(String key : biggestSets.keySet()) {
                this.currentWord = key;
                this.currentWords = biggestSets.get(this.currentWords);
            }
            return getCurrentWords();
        }



        return getCurrentWords();
    }
}
