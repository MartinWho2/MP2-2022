package ch.epfl.cs107.play.game.icrogue.actor.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Staff extends Item {
    private Sprite sprite;
    public Staff(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, "zelda/staff_water.icon", .5f);
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }
    public boolean isCellInteractable(){return false;}

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }
}
