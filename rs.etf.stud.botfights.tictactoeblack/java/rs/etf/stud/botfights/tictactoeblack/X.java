package rs.etf.stud.botfights.tictactoeblack;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class X implements MyCustomAnimatedShape {
    private final Color color;
    private Line l1;
    private Line l2;
    private double startX, startY, endX, endY;

    public X(double startX, double startY, double endX, double endY, Color color, int strokeWidth) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.color = color;

        l1 = new Line(startX, startY, startX, startY);
        l2 = new Line(endX, startY, endX, startY);

        l1.setStroke(color);

        l2.setStroke(Color.TRANSPARENT);

        Stream.of(l1, l2).forEach(line -> {
            line.setStrokeLineCap(StrokeLineCap.ROUND);
            line.setStrokeWidth(strokeWidth);
        });
    }

    @Override
    public Collection<Node> getNodes() {
        return List.of(l1,l2);
    }

    @Override
    public Animation getAnimation() {
        SequentialTransition transition = new SequentialTransition();
        transition.getChildren().add(getTimelineForLine(l1, endX, endY));
        transition.getChildren().add(getTimelineForLine(l2, startX, endY));
        return transition;
    }

    private Timeline getTimelineForLine(Line l, double endX, double endY) {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        KeyValue kv1 = new KeyValue(l.endXProperty(), endX);
        KeyValue kv2 = new KeyValue(l.endYProperty(), endY);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(200), kv1, kv2);
        timeline.getKeyFrames().add(keyFrame);
        if (l == l1) timeline.setOnFinished(event -> {
            l2.setStroke(Paint.valueOf(color.toString()));
        });
        return timeline;
    }
}
