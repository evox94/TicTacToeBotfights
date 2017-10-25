package rs.etf.stud.botfights.tictactoeblack;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import rs.etf.stud.botfights.core.*;
import rs.etf.stud.botfights.extend.SimpleGameFlow;
import rs.etf.stud.botfights.extend.java.JavaGameStateDeserializer;
import rs.etf.stud.botfights.extend.java.JavaOnlyGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class TicTacToe extends JavaOnlyGame {
    private enum OutcomeType {WIN, DRAW, OTHER}

    private OutcomeType outcomeType = OutcomeType.OTHER;

    private int[][] board;
    private Player playerX;
    private Player player0;
    private Player nextPlayer;
    private int moveCount = 0;
    private boolean end;
    private GameOutcome gameOutcome = null;
    private ViewHolder viewHolder;

    public TicTacToe() {
        board = new int[3][3];
        end = false;
        viewHolder = new ViewHolder();
    }

    @Override
    public Class<? extends JavaGameStateDeserializer> getDeserializerClass() {
        return Deserializer.class;
    }

    @Override
    protected String getTemplateFileName() {
        return "/rs/etf/stud/botfights/tictactoeblack/templates/TicTacToeBot.java";
    }

    @Override
    public String getName() {
        return "Tic Tac Toe Black";
    }

    public String getDescription() {

        final URL location = getClass().getResource("/rs/etf/stud/botfights/tictactoeblack/html/description.html");
        String content = null;
        try {
            InputStream in = location.openStream();
            InputStreamReader is = new InputStreamReader(in);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();
            while (read != null) {
                sb.append(read).append("\n");
                read = br.readLine();
            }
            content = sb.toString();
        } catch (IOException e) {
            System.err.println(e);
        }
        content = content.replace("\"localresource:",
                "\"" + location.toExternalForm().replace("description.html", ""));
        return content;
    }

    @Override
    public int getMinPlayers() {
        return 2;
    }

    @Override
    public int getMaxPlayers() {
        return 2;
    }

    @Override
    public GameState getGameStateForPlayer(Player player) {
        return new GameState() {
            @Override
            public Object serialize() {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (player.equals(player0) && board[i][j] != 0) {
                            builder.append(3 - board[i][j]);
                        } else {
                            builder.append(board[i][j]);
                        }
                        builder.append(" ");
                    }
                }
                return builder.toString();
            }
        };
    }

    @Override
    public GameFlow getFlow() {
        return new SimpleGameFlow(115);
    }


    @Override
    public void initGame() {
        playerX = players.get(0);
        player0 = players.get(1);
        nextPlayer = playerX;
    }

    @Override
    public void onPlayerMove(Move move, Player player) {
        try {
            if (end) throw new InvalidMoveException("Game has ended! No moves are allowed");
            if (!player.equals(nextPlayer))
                throw new InvalidMoveException("It's not players(" + player.getName() + ") turn to play");
            TicTacToeMove ticTacToeMove = TicTacToeMove.parseMove(move.getCommand());
            if (ticTacToeMove != null) {
                int mx = ticTacToeMove.getMx();
                int my = ticTacToeMove.getMy();
                if (board[mx][my] != 0)
                    throw new InvalidMoveException("Player(" + player.getName() + ") played on a already filled space");

                board[mx][my] = playerX.equals(player) ? 1 : 2;
                nextPlayer = playerX.equals(player) ? player0 : playerX;
                ++moveCount;

                checkEndCondition(mx, my);
            } else {
                throw new InvalidMoveException("Invalid command format: got" + move.getCommand() + " ,expected: two digits in the range from 0 to 2");
            }
        } catch (InvalidMoveException e) {
            end = true;
            gameOutcome = new GameOutcome(e.getMessage(), GameOutcome.OutcomeType.ERROR);
            e.printStackTrace();
        }
    }

    private void checkEndCondition(int mx, int my) {
        int s = board[mx][my];
        int n = 3;
        //https://stackoverflow.com/questions/1056316/algorithm-for-determining-tic-tac-toe-game-over
        for (int i = 0; i < n; i++) {
            if (board[mx][i] != s)
                break;
            if (i == n - 1) {
                win(s);
                viewHolder.winRow = mx;
                return;
            }
        }
        for (int i = 0; i < n; i++) {
            if (board[i][my] != s)
                break;
            if (i == n - 1) {
                win(s);
                viewHolder.winCol = my;
                return;
            }
        }
        if (mx == my) {
            //we're on a diagonal
            for (int i = 0; i < n; i++) {
                if (board[i][i] != s)
                    break;
                if (i == n - 1) {
                    win(s);
                    viewHolder.diag = true;
                    return;
                }
            }
        }
        if (mx + my == n - 1) {
            for (int i = 0; i < n; i++) {
                if (board[i][(n - 1) - i] != s)
                    break;
                if (i == n - 1) {
                    viewHolder.antidiag = true;
                    win(s);
                    return;
                }
            }
        }

        //check draw
        if (moveCount == 9) {
            draw();
        }
    }

    private void draw() {
        outcomeType = OutcomeType.DRAW;
        end = true;
        gameOutcome = new GameOutcome("It's a draw!", GameOutcome.OutcomeType.SUCCESS);
    }

    private void win(int s) {
        viewHolder.winningSymbol = s;
        outcomeType = OutcomeType.WIN;
        end = true;
        String playerName = s == 1 ? playerX.getName() : player0.getName();
        gameOutcome = new GameOutcome("Player(" + playerName + ") wins!", GameOutcome.OutcomeType.SUCCESS);
    }


    @Override
    public GameOutcome getGameOutcome() {
        return gameOutcome;
    }

    @Override
    public void cleanupGame() {

    }

    @Override
    public boolean endCondition() {
        return end;
    }

    /*
    *
    *
    * VIEW PART
    *
    *
    * */

    private class ViewHolder implements GameView {
        private Group boardGroup;
        private Timeline boardAnimation;
        private int offset = 30;
        private int gap = 220;
        private Color backgroundColor = Color.BLACK;
        private Color foregroundColor = Color.WHITE;
        private Color XColor = Color.rgb(139, 255, 255);
        private Color OColor = Color.rgb(255, 255, 139);
        private int tableStrokeWidth = 10;
        ;
        private double tableAnimationMs = 200;
        private VBox content;
        private StackPane boardBox;
        int winRow = -1;
        int winCol = -1;
        boolean diag = false;
        boolean antidiag = true;
        int winningSymbol;


        @Override
        public Scene initScene() {
            content = new VBox();

            Text player1Text = new Text(playerX.getName().toUpperCase());
            Text player2Text = new Text(player0.getName().toUpperCase());

            Stream.of(player1Text, player2Text).forEach(text -> {
                text.setFont(Font.font("Roboto Regular", 30));
                text.setFill(foregroundColor);
            });

            X x = new X(0, 0, 22, 20, XColor, 4);
            x.getAnimation().play();
            O o = new O(6, 10, 12, 4, OColor, Color.TRANSPARENT);
            o.getAnimation().play();

            GridPane gridPane = new GridPane();
            gridPane.add(player1Text,0,0);
            gridPane.add(player2Text, 0,1);
            gridPane.add(new Group(x.getNodes()),1,0);
            gridPane.add(new Group(o.getNodes()),1,1);
            gridPane.setHgap(20);
            gridPane.setVgap(5);
            gridPane.setPadding(new Insets(10));
            gridPane.setBackground(new Background(new BackgroundFill(backgroundColor,null,null)));

            content.getChildren().add(gridPane);

            boardBox = new StackPane();
            boardBox.setBackground(new Background(new BackgroundFill(backgroundColor, null, null)));
            VBox.setVgrow(boardBox, Priority.ALWAYS);
            content.getChildren().add(boardBox);

            boardGroup = new Group();

            Line h1 = new Line(gap, gap, 2 * gap, gap);
            Line h2 = new Line(gap, 2 * gap, 2 * gap, 2 * gap);
            Line v1 = new Line(gap, gap, gap, 2 * gap);
            Line v2 = new Line(2 * gap, gap, 2 * gap, 2 * gap);

            Line pillar1 = new Line(gap * 3 / 2, 0, gap * 3 / 2, 3 * gap);
            Line pillar2 = new Line(0, gap * 3 / 2, 3 * gap, gap * 3 / 2);

            pillar1.setStroke(Color.TRANSPARENT);
            pillar2.setStroke(Color.TRANSPARENT);

            Stream.of(h1, h2, v1, v2).forEach(line -> {
                line.setStroke(foregroundColor);
                line.setStrokeLineCap(StrokeLineCap.ROUND);
                line.setStrokeWidth(tableStrokeWidth);
            });

            boardAnimation = new Timeline();
            List<KeyValue> keyValues = new ArrayList<>();

            Stream.of(h1, h2).forEach(line -> {
                keyValues.add(new KeyValue(line.startXProperty(), 0));
                keyValues.add(new KeyValue(line.endXProperty(), 3 * gap));
            });

            Stream.of(v1, v2).forEach(line -> {
                keyValues.add(new KeyValue(line.startYProperty(), 0));
                keyValues.add(new KeyValue(line.endYProperty(), 3 * gap));
            });

            KeyFrame kf = new KeyFrame(Duration.millis(tableAnimationMs), keyValues.toArray(new KeyValue[keyValues.size()]));

            boardAnimation.setCycleCount(1);
            boardAnimation.getKeyFrames().add(kf);

            boardGroup.getChildren().addAll(h1, h2, v1, v2, pillar1, pillar2);
            boardBox.getChildren().add(boardGroup);

            return new Scene(content, 900, 900);
        }

        @Override
        public void onGameStart(ViewContext viewContext) {
            viewContext.pause(200, false);
            boardAnimation.play();
        }

        @Override
        public void onTurnStart(ViewContext viewContext) {

        }

        @Override
        public void beforePlayerTurn(ViewContext viewContext, Player player) {

        }

        @Override
        public void afterPlayerTurn(ViewContext viewContext, Player player, Move move) {
            TicTacToeMove tmove = TicTacToeMove.parseMove(move.getCommand());
            if (tmove != null) {
                int i = tmove.getMx();
                int j = tmove.getMy();
                MyCustomAnimatedShape shape;
                if (player.equals(playerX)) {
                    shape = new X(j * gap + offset,
                            i * gap + offset + 5,
                            j * gap - offset + gap,
                            i * gap - offset - 5 + gap, XColor, 10);
                } else {
                    shape = new O(j * gap + gap / 2, i * gap + gap / 2, gap / 2 - offset, 10, OColor, Color.TRANSPARENT);
                }
                boardGroup.getChildren().addAll(shape.getNodes());
                shape.getAnimation().play();
            }
            viewContext.pause(400, false);
        }

        @Override
        public void onTurnEnd(ViewContext viewContext) {

        }

        @Override
        public void onGameEnd(ViewContext viewContext) {
            if (outcomeType != OutcomeType.OTHER) {
                VBox endScreen = new VBox();
                endScreen.setAlignment(Pos.CENTER);
                endScreen.setScaleY(0);
                endScreen.setScaleX(0);

                HBox symbolBox = new HBox();
                symbolBox.setPrefHeight(gap + 50);
                symbolBox.setPrefWidth(3 * gap);
                symbolBox.setAlignment(Pos.CENTER);
                endScreen.getChildren().add(symbolBox);

                Text text = new Text(outcomeType.toString());
                text.setFont(Font.font("Roboto Regular", 100));
                text.setFill(foregroundColor);
                endScreen.getChildren().add(text);

                boardBox.getChildren().add(endScreen);

                SequentialTransition mainTransition = new SequentialTransition();
                mainTransition.setDelay(Duration.millis(200));
                if (outcomeType == OutcomeType.DRAW) {
                    mainTransition.getChildren().add(new PauseTransition(Duration.millis(500)));
                } else {
                    Timeline lineCrossAnimation = new Timeline();
                    lineCrossAnimation.setCycleCount(1);

                    Line line = new Line();
                    line.setStroke(Color.TRANSPARENT);
                    line.setStrokeLineCap(StrokeLineCap.ROUND);
                    line.setStrokeWidth(tableStrokeWidth + 2);
                    int lineoffset = 10;
                    List<KeyValue> keyValues = new ArrayList<>();
                    if (winRow != -1) {
                        line.setStartX(lineoffset);
                        line.setEndX(lineoffset);
                        line.setStartY(winRow * gap + gap / 2);
                        line.setEndY(winRow * gap + gap / 2);
                        keyValues.add(new KeyValue(line.endXProperty(), 3 * gap - lineoffset));
                    } else if (winCol != -1) {
                        line.setStartX(winCol * gap + gap / 2);
                        line.setStartY(lineoffset);
                        line.setEndX(winCol * gap + gap / 2);
                        line.setEndY(lineoffset);
                        keyValues.add(new KeyValue(line.endYProperty(), 3 * gap - lineoffset));
                    } else if (diag) {
                        line.setStartX(lineoffset);
                        line.setStartY(lineoffset);
                        line.setEndX(lineoffset);
                        line.setEndY(lineoffset);
                        keyValues.add(new KeyValue(line.endXProperty(), 3 * gap - lineoffset));
                        keyValues.add(new KeyValue(line.endYProperty(), 3 * gap - lineoffset));
                    } else {
                        line.setStartX(3 * gap - lineoffset);
                        line.setStartY(lineoffset);
                        line.setEndX(3 * gap - lineoffset);
                        line.setEndY(lineoffset);
                        keyValues.add(new KeyValue(line.endXProperty(), lineoffset));
                        keyValues.add(new KeyValue(line.endYProperty(), 3 * gap - lineoffset));
                    }
                    boardGroup.getChildren().add(line);
                    KeyFrame startKeyFrame = new KeyFrame(Duration.ZERO, event -> {
                        line.setStroke(foregroundColor);
                    }, (KeyValue) null);
                    lineCrossAnimation.getKeyFrames().addAll(startKeyFrame, new KeyFrame(Duration.millis(500), keyValues.toArray(new KeyValue[keyValues.size()])));
                    mainTransition.getChildren().add(lineCrossAnimation);
                }

                FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), boardGroup);
                fadeTransition.setDelay(Duration.millis(300));
                fadeTransition.setFromValue(1.0);
                fadeTransition.setToValue(0);
                mainTransition.getChildren().add(fadeTransition);

                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), endScreen);
                scaleTransition.setToX(1);
                scaleTransition.setToX(1);
                scaleTransition.setByX(1);
                scaleTransition.setByY(1);
                scaleTransition.setOnFinished(event -> {
                    if (outcomeType == OutcomeType.WIN) {
                        if (winningSymbol == 1) {
                            X x = new X(0, 0, gap - offset, gap - offset - 5, XColor, 10);
                            symbolBox.getChildren().addAll(new Group(x.getNodes()));
                            x.getAnimation().play();
                        } else {
                            O o = new O(0, 0, gap / 2, 10, OColor, Color.TRANSPARENT);
                            symbolBox.getChildren().addAll(new Group(o.getNodes()));
                            o.getAnimation().play();
                        }
                    } else {
                        X x = new X(0, 0, gap - offset, gap - offset - 5, XColor, 10);
                        O o = new O(0, 0, gap / 2, 10, OColor, Color.TRANSPARENT);
                        symbolBox.getChildren().addAll(new Group(x.getNodes()), new Group(o.getNodes()));
                        x.getAnimation().play();
                        o.getAnimation().play();
                    }
                });
                mainTransition.getChildren().add(scaleTransition);

                mainTransition.play();

                viewContext.pause(2500, false);
            }
        }
    }

    @Override
    public Scene initScene() {
        return viewHolder.initScene();
    }

    @Override
    public void onGameStart(ViewContext viewContext) {
        viewHolder.onGameStart(viewContext);
    }

    @Override
    public void onTurnStart(ViewContext viewContext) {
        viewHolder.onTurnStart(viewContext);
    }

    @Override
    public void beforePlayerTurn(ViewContext viewContext, Player player) {
        viewHolder.beforePlayerTurn(viewContext, player);
    }

    @Override
    public void afterPlayerTurn(ViewContext viewContext, Player player, Move move) {
        viewHolder.afterPlayerTurn(viewContext, player, move);
    }

    @Override
    public void onTurnEnd(ViewContext viewContext) {
        viewHolder.onTurnEnd(viewContext);
    }

    @Override
    public void onGameEnd(ViewContext viewContext) {
        viewHolder.onGameEnd(viewContext);
    }

    private class InvalidMoveException extends Exception {
        public InvalidMoveException(String message) {
            super(message);
        }
    }
}



