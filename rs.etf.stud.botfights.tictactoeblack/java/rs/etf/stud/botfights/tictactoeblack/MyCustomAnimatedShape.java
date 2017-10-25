package rs.etf.stud.botfights.tictactoeblack;

import javafx.animation.Animation;
import javafx.scene.Node;

import java.util.Collection;

public interface MyCustomAnimatedShape {
    Collection<Node> getNodes();
    Animation getAnimation();
}
