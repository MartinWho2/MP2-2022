package ch.epfl.cs107.play.game.icrogue.actor.enemies;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.SpeakerActor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;


public abstract class Enemy extends SpeakerActor {
    private boolean isAlive;

    /**
     * Init all useful class attributes
     *
     * @param area        (Area): owner Area
     * @param orientation (Orientation): orientation of the character
     * @param position    (DiscreteCoordinates): spawn coordinates in the room
     * @param size        (float): size of the sprite on the map
     */
    public Enemy(Area area, Orientation orientation, DiscreteCoordinates position, float size){
        super(area, orientation,position);
        enterArea(area, position);
        isAlive = true;
    }

    public boolean getIsAlive(){
        return isAlive;
    }

    /**
     * Try to kill the entity and to unregister her
     */
    public void die(){
        if (isAlive) {
            resetMotion();
            leaveArea();
            isAlive = false;
        }
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {

    }

}
