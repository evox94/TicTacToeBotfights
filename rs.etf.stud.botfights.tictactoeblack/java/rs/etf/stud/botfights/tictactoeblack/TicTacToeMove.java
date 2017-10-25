package rs.etf.stud.botfights.tictactoeblack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TicTacToeMove {
    private int mx;
    private int my;

    private static String movePattern = "^([0-2]),?\\s?([0-2])$";
    private static Pattern p = Pattern.compile(movePattern);

    private TicTacToeMove(int mx, int my) {
        this.mx = mx;
        this.my = my;
    }

    public int getMx() {
        return mx;
    }

    public int getMy() {
        return my;
    }



    public static TicTacToeMove parseMove(String move){
        Matcher matcher = p.matcher(move);
        if (matcher.find()) {
            int mx = Integer.parseInt(matcher.group(1));
            int my = Integer.parseInt(matcher.group(2));
            return new TicTacToeMove(mx, my);
        }
        return null;
    }
}
