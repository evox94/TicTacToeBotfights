package rs.etf.stud.botfights.tictactoe;

import javafx.animation.Animation;
import javafx.scene.Node;

import java.util.Collection;

public interface MyCustomAnimatedShape {
    Collection<Node> getNodes();
    Animation getAnimation();
}
