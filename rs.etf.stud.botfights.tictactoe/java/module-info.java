open module rs.etf.stud.botfights.tictactoe {
    requires rs.etf.stud.botfights.extend.java;
    provides rs.etf.stud.botfights.core.Game with rs.etf.stud.botfights.tictactoe.TicTacToe;
    provides rs.etf.stud.botfights.extend.java.JavaGameStateDeserializer with rs.etf.stud.botfights.tictactoe.Deserializer;
}