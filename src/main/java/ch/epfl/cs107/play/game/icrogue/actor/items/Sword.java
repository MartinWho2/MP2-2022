package ch.epfl.cs107.play.game.icrogue.actor.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Sword extends Item{
    private boolean isBeingUsed;
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }
    public Sword(Area area, Orientation orientation, DiscreteCoordinates position){
        super(area, orientation, position,"zelda/sword.icon",0.6f);
        isBeingUsed = false;
    }
    @Override
    public void tryToUseItem() {
        itemUseListener.canUseItem(this);
    }

    @Override
    public boolean isViewInteractable() {
        return isBeingUsed;
    }

    @Override
    public void useItem(Area area, Orientation orientation, DiscreteCoordinates coords) {
        //area.registerActor(this);

    }

}
