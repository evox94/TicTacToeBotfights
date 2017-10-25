package rs.etf.stud.botfights.tictactoeblack;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Collection;
import java.util.List;

public class O implements MyCustomAnimatedShape {
    private Arc arc;
    private Circle circle;


    public O(double cx, double cy, double radius, int strokeWidth, Color strokeColor, Color fillColor) {
        arc = new Arc(cx, cy, radius, radius, 0, 0);
        arc.setStrokeWidth(strokeWidth);
        arc.setStroke(strokeColor);
        arc.setType(ArcType.OPEN);
        arc.setFill(fillColor);
        arc.setStartAngle(90);
    }

    @Override
    public Collection<Node> getNodes() {
        return List.of(arc);
    }

    @Override
    public Animation getAnimation() {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        KeyValue kv = new KeyValue(arc.lengthProperty(), 360);
        KeyFrame kf = new KeyFrame(Duration.millis(400), kv);
        timeline.getKeyFrames().add(kf);
        return timeline;
    }
}
