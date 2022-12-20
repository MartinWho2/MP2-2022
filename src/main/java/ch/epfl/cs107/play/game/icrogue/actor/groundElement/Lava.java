package ch.epfl.cs107.play.game.icrogue.actor.groundElement;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.ICRogueActor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class Lava extends ICRogueActor {
    private Sprite sprite;


    /**
     * Init all useful attributes
     * @param area (Area): owner Area
     * @param spawn (DiscreteCoordinates): position of the entity on the map
     */
    public Lava(Area area, DiscreteCoordinates spawn) {
        super(area, Orientation.DOWN, spawn);
        enterArea(area,spawn);
        sprite = new Sprite("other/lava",1f,1f,this);
    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        // is empty because Lava doesn't interact with anything
    }
}
