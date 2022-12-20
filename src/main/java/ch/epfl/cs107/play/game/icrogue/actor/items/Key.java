package ch.epfl.cs107.play.game.icrogue.actor.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.game.icrogue.handler.ItemUseListener;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Key extends Item{
    private final int KEY_ID;

    /**
     * Init all useful attributes
     * @param area (Area): owner Area
     * @param orientation (Orientation): orientation of the skeleton
     * @param position (DiscreteCoordinates): position of the entity on the map
     * @param ID (int): id of the locked connector it can open
     */
    public Key(Area area, Orientation orientation, DiscreteCoordinates position, int ID){
        super(area, orientation, position, "icrogue/key", 0.6f);
        KEY_ID = ID;
    }

    public int getKEY_ID() {
        return KEY_ID;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }

    @Override
    public void tryToUseItem() {

    }

    @Override
    public void useItem(Area area, Orientation orientation, DiscreteCoordinates coords) {

    }
}
