package ch.epfl.cs107.play.game.icrogue.actor.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.game.icrogue.handler.ItemUseListener;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Cherry extends Item{

    /**
     * Init all useful attributes
     * @param area (Area): owner Area
     * @param orientation (Orientation): orientation of the skeleton
     * @param position (DiscreteCoordinates): position of the entity on the map
     */
    public Cherry(Area area, Orientation orientation, DiscreteCoordinates position){
        super(area, orientation, position, "icrogue/cherry", 0.6f);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }

    /**
     * Cherry can't be used
     */
    @Override
    public void tryToUseItem() {
        getItemUseListener().canUseItem(this);
    }

    /**
     * Cherry can not be used
     * @param area (Area): owner area
     * @param orientation (Orientation): set his orientation
     * @param coords (DiscreteCoordinates): set coordinates on spawn in the room
     */
    @Override
    public void useItem(Area area, Orientation orientation, DiscreteCoordinates coords) {
    }

    @Override
    public void collect(ItemUseListener handler) {
        super.collect(handler);
    }
}
