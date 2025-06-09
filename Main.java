import arc.*;

public class Main {
    static Console con = new Console("Hangman by AJ", 1280, 720);

    public static void main(String[] args) {
        
        // Main menu loop to keep the program running and allow the user to choose an option
        while (true) {
            con.clear();
            con.println("=== HANGMAN ===");
            con.println("1. Play Game");
            con.println("2. View Leaderboard");
            con.println("3. Add Theme");
            con.println("4. Help");
            con.println("5. Quit");
            con.print("Enter choice: ");
            String strInput = con.readLine();
            
            // Check for secret menu option
            if (strInput.equalsIgnoreCase("joke")) {
                con.println("\nWhat does a baby computer call its dad? Data!");
                con.readLine();
                continue;
            }
            
            int intChoice = Integer.parseInt(strInput);

            if (intChoice == 1) {
                playGame();
            } else if (intChoice == 2) {
                viewLeaderboard();
            } else if (intChoice == 3) {
                addTheme();
            } else if (intChoice == 4) {
                showHelp();
            } else if (intChoice == 5) {
                System.exit(0);
            }
        }
    }
    // Method to show the help menu when the user chooses the help option
    static void showHelp() {
        con.clear();
        con.println("=== HOW TO PLAY HANGMAN ===");
        con.println("1. Choose a theme from the available options");
        con.println("2. You will be shown a word with letters hidden as underscores");
        con.println("3. Try to guess the word by typing it in");
        con.println("4. Each wrong guess adds a body part to the hangman");
        con.println("5. You lose if the hangman is completed (6 body parts)");
        con.println("6. You win if you guess the word correctly");
        con.println("\nSpecial Features:");
        con.println("- If you type 'statitan' as your guess, you get a free letter!");
        con.println("- Try to get the highest score by guessing multiple words");
        con.print("\nPress enter to return to main menu...");
        con.readLine();
    }
    // Method to play the game when the user chooses the play game option
    static void playGame() {
        con.clear();
        con.print("Enter your name: ");
        String strName = con.readLine();

        String[] strThemes = loadThemes();
        if (strThemes.length == 0) {
            con.println("No themes available.");
            con.readLine();
            return;
        }
        // Display the available themes to the user
        con.println("Available Themes:");
        for (int intI = 0; intI < strThemes.length; intI++) {
            con.println("- " + strThemes[intI]);
        }
        con.print("Enter theme filename (e.g., food.txt): ");
        String strThemeFile = con.readLine();
        // Calling the loadWords method to load the words from the theme file
        String[][] strWordPairs = loadWords(strThemeFile);
        if (strWordPairs.length == 0) {
            con.println("Theme file empty or not found.");
            con.readLine();
            return;
        }
        // Calling the bubbleSort method to sort the words by the random number generated
        bubbleSort(strWordPairs);
        // Initializing the number of wins to 0
        int intWins = 0;
        for (int intI = 0; intI < strWordPairs.length; intI++) {
            String strWord = strWordPairs[intI][0];
            if (playRound(strWord)) {
                intWins++;
            } else {
                break;
            }
            con.print("Play again? (yes/no): ");
            String strAgain = con.readLine();
            if (!strAgain.equalsIgnoreCase("yes")) {
                break;
            }
        }

        saveScore(strName, intWins);
    }

    static boolean playRound(String strWord) {
        String strExposed = "_".repeat(strWord.length());
        int intBodyParts = 0;

        while (intBodyParts < 6) {
            con.clear();
            drawHangman(intBodyParts);
            con.println("Word: " + strExposed);
            con.print("Guess the word: ");
            String strGuess = con.readLine();

            if (strGuess.equalsIgnoreCase(strWord)) {
                con.println("Correct! You win!");
                con.readLine();
                return true;
            } else {
                if (strGuess.equalsIgnoreCase("statitan")) {
                    strExposed = exposeRandomLetter(strWord, strExposed);
                    con.println("Special word detected! You get a free letter!");
                    con.readLine();
                } else {
                    strExposed = exposeRandomLetter(strWord, strExposed);
                    intBodyParts++;
                }
            }
        }

        con.println("You lost! The word was: " + strWord);
        con.readLine();
        return false;
    }

    static String exposeRandomLetter(String strWord, String strExposed) {
        int intIdx = (int)(Math.random() * strWord.length());
        while (strExposed.charAt(intIdx) != '_') {
            intIdx = (int)(Math.random() * strWord.length());
        }
        
        char[] chrExposedArray = strExposed.toCharArray();
        chrExposedArray[intIdx] = strWord.charAt(intIdx);
        return new String(chrExposedArray);
    }

    static void drawHangman(int intParts) {
        con.println(" +---+");
        con.println(" |   |");
        
        String strHead = " ";
        if (intParts > 0) 
        {
            strHead = "O";
        }
        con.println(" " + strHead + "   |");
        
        String strLeftArm = " ";
        String strBody = " ";
        String strRightArm = " ";
        if (intParts > 2) 
        {
            strLeftArm = "/";
        }
        if (intParts > 1) 
        {
            strBody = "|";
        }
        if (intParts > 3) 
        {
            strRightArm = "\\";
        }
        con.println(strLeftArm + strBody + strRightArm + "  |");
        
        String strLeftLeg = " ";
        String strRightLeg = " ";
        if (intParts > 4) 
        {
            strLeftLeg = "/";
        }
        if (intParts > 5) 
        {
            strRightLeg = "\\";
        }
        con.println(strLeftLeg + " " + strRightLeg + "  |");
        
        con.println("     |");
        con.println("=========");
    }

    static void viewLeaderboard() {
        con.clear();
        String[][] strScores = new String[100][2];
        int intScoreCount = 0;
        
        TextInputFile in = new TextInputFile("leaderboard.txt");
        while (!in.eof()) {
            String strLine = in.readLine();
            if (strLine == null || strLine.length() == 0) {
                continue;
            }
            
            String strName = "";
            String strScore = "";
            boolean blnFoundComma = false;
            
            for (int intI = 0; intI < strLine.length(); intI++) {
                if (strLine.charAt(intI) == ',') {
                    blnFoundComma = true;
                }
                else if (!blnFoundComma) {
                    strName = strName + strLine.charAt(intI);
                }
                else {
                    strScore = strScore + strLine.charAt(intI);
                }
            }
            
            if (strName.length() > 0 && strScore.length() > 0) {
                strScores[intScoreCount][0] = strName;
                strScores[intScoreCount][1] = strScore;
                intScoreCount++;
            }
        }
        in.close();

        bubbleSortScores(strScores, intScoreCount);
        con.println("Leaderboard:");
        for (int intI = 0; intI < intScoreCount; intI++) {
            con.println(strScores[intI][0] + " - " + strScores[intI][1]);
        }
        con.print("Press enter to return...");
        con.readLine();
    }

    static void addTheme() {
        con.clear();
        con.print("Enter new theme filename (e.g., starwars.txt): ");
        String strFile = con.readLine();
        TextOutputFile out = new TextOutputFile(strFile, true);
        while (true) {
            con.print("Enter a word (or 'stop' to finish): ");
            String strWord = con.readLine();
            if (strWord.equalsIgnoreCase("stop")) {
                break;
            }
            if (strWord.length() >= 7) {
                out.println(strWord.toLowerCase());
            } else {
                con.println("Word must be at least 7 letters.");
            }
        }
        out.close();

        boolean blnFound = false;
        TextInputFile in = new TextInputFile("themes.txt");
        String[] strThemeList = new String[100];
        int intThemeCount = 0;
        while (!in.eof()) {
            String strLine = in.readLine();
            strThemeList[intThemeCount] = strLine;
            if (strLine.equals(strFile)) {
                blnFound = true;
            }
            intThemeCount++;
        }
        in.close();

        if (!blnFound) {
            TextOutputFile append = new TextOutputFile("themes.txt", true);
            append.println(strFile);
            append.close();
        }
    }

    static void saveScore(String strName, int intScore) {
        TextOutputFile out = new TextOutputFile("leaderboard.txt", true);
        out.println(strName + "," + intScore);
        out.close();
    }

    static String[] loadThemes() {
        String[] strThemes = new String[100];
        int intThemeCount = 0;
        TextInputFile in = new TextInputFile("themes.txt");
        while (!in.eof()) {
            strThemes[intThemeCount] = in.readLine();
            intThemeCount++;
        }
        in.close();
        
        String[] strResult = new String[intThemeCount];
        for (int intI = 0; intI < intThemeCount; intI++) {
            strResult[intI] = strThemes[intI];
        }
        return strResult;
    }

    static String[][] loadWords(String strFileName) {
        String[][] strWords = new String[100][2];
        int intWordCount = 0;
        TextInputFile in = new TextInputFile(strFileName);
        while (!in.eof()) {
            String strWord = in.readLine();
            if (strWord.length() >= 7) {
                int intRandNum = (int)(Math.random() * 100) + 1;
                strWords[intWordCount][0] = strWord;
                strWords[intWordCount][1] = Integer.toString(intRandNum);
                intWordCount++;
            }
        }
        in.close();
        
        String[][] strResult = new String[intWordCount][2];
        for (int intI = 0; intI < intWordCount; intI++) {
            strResult[intI][0] = strWords[intI][0];
            strResult[intI][1] = strWords[intI][1];
        }
        return strResult;
    }

    static void bubbleSort(String[][] strArr) {
        int intN = strArr.length;
        for (int intI = 0; intI < intN - 1; intI++) {
            for (int intJ = 0; intJ < intN - intI - 1; intJ++) {
                int intA = Integer.parseInt(strArr[intJ][1]);
                int intB = Integer.parseInt(strArr[intJ + 1][1]);
                if (intA > intB) {
                    String[] strTemp = strArr[intJ];
                    strArr[intJ] = strArr[intJ + 1];
                    strArr[intJ + 1] = strTemp;
                }
            }
        }
    }

    static void bubbleSortScores(String[][] strScores, int intCount) {
        for (int intI = 0; intI < intCount - 1; intI++) {
            for (int intJ = 0; intJ < intCount - intI - 1; intJ++) {
                int intA = Integer.parseInt(strScores[intJ][1]);
                int intB = Integer.parseInt(strScores[intJ + 1][1]);
                if (intA < intB) {
                    String[] strTemp = strScores[intJ];
                    strScores[intJ] = strScores[intJ + 1];
                    strScores[intJ + 1] = strTemp;
                }
            }
        }
    }
}
