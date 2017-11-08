import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Kaden Cringle
 * 10/19/2017
 */

/*
    https://wordfinder-001.appspot.com?game=<1-3>&pos=<column><row>
    Columns: a-e
    Rows: 1-5

    Use to find letter at a given spot in the game.
    Check if any words in our list, start with that letter.
    Check the letters below and to the right, to see if they match the next letter in the potential word.
    Repeat.
 */

public class WordFinder {

    private int numAttempts = 0;

    /*
        This method takes an integer 0-5 for column and row and converts it to a string
        to be appended to the URL
     */
    public String convertIntPosToString(int column, int row) {
        String colNum;
        String rowNum;
        switch (column) {
            case 1:
                colNum = "a";
                break;
            case 2:
                colNum = "b";
                break;
            case 3:
                colNum = "c";
                break;
            case 4:
                colNum = "d";
                break;
            case 5:
                colNum = "e";
                break;
            default:
                colNum = null;
        }
        rowNum = String.valueOf(row);
        if (colNum != null && row < 6 && row > 0)
            return "&pos=" + colNum + rowNum;
        return "&pos=a0";
    }

    public String getDataFromDomain(String domain) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(domain);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK)
                return null;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                result.append(line).append("\n");
            bufferedReader.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result.toString();
    }

    public void run() {
        String[] readWordsFromWeb = getDataFromDomain("https://wordfinder-001.appspot.com/word.txt").split("\n");
        List<String> words = Arrays.asList(readWordsFromWeb);
        ArrayList<String> letters = new ArrayList<>();
        for (int col = 1; col < 6; col++) {
            for (int row = 1; row < 6; row++) {
                String letterToAdd;
                while ((letterToAdd = getDataFromDomain("https://wordfinder-001.appspot.com/wordfinder?game=1" + convertIntPosToString(col, row))) == null) {
                    numAttempts++;
                    if (numAttempts > 4)
                        System.exit(-1);
                }
                letters.add(letterToAdd);
            }
        }
        for (String word : words) {
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 5; x++) {
                    if (letters.get(x + y * 5).equals(word.substring(0, 1))) {
                        System.out.println(word);
                        System.out.println(letters.get(x + y * 5));
                    }
                }
            }
        }
    }
}
