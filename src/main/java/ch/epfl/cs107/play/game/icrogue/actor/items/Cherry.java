package ch.epfl.cs107.play.game.icrogue.actor.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Cherry extends Item{
    public Cherry(Area area, Orientation orientation, DiscreteCoordinates position){
        super(area, orientation, position, "icrogue/cherry", 0.6f);
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
