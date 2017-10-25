package rs.etf.stud.botfights.tictactoeblack;

import rs.etf.stud.botfights.extend.java.JavaGameStateDeserializer;

public class Deserializer implements JavaGameStateDeserializer {
    @Override
    public Object[] toParams(String gameStateString) {
        int[][] params = new int[3][3];
        String[] parts = gameStateString.split(" ");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                params[i][j] = Integer.parseInt(parts[i * 3 + j]);
            }
        }
        return new Object[]{params};
    }
}
