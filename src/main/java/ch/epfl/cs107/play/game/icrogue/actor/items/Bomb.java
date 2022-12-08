package ch.epfl.cs107.play.game.icrogue.actor.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.ICRogueBehavior;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bomb extends Item implements Interactor {
    private InteractionHandler handler;
    private boolean isPlaced = false;
    private boolean placing = false;

    public Bomb(Area area, Orientation orientation, DiscreteCoordinates position, String spriteName, float size){
        super(area, orientation, position, spriteName, size);
        sprite = new Sprite(spriteName, size, size, this);
        handler = new InteractionHandler();
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        if (!isPlaced) {
            List<DiscreteCoordinates> cells = new ArrayList<>();
            if (isCollected()) {
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        if ((Math.abs(i) + Math.abs(j)) == 1)
                            cells.add(getCurrentMainCellCoordinates().jump(i, j));
                    }
                }
            }
            return cells;
        } else {
            return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
        }

    }

    public void placeBomb(DiscreteCoordinates coordinates, Area area) {
        area.registerActor(this);
        setOwnerArea(area);
        System.out.println(getOwnerArea());
        setCurrentPosition(coordinates.toVector());
        isPlaced = true;
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }
    // The bomb
    @Override
    public boolean wantsViewInteraction() {
        return isCollected();
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler , isCellInteraction);
    }
    private class InteractionHandler implements ICRogueInteractionHandler {
        @Override
        public void interactWith(Connector connector, boolean isCellInteraction) {
            if (isPlaced) {
                if (connector.getState().equals(Connector.ConnectorType.CRACKED)) {
                    connector.setState(Connector.ConnectorType.OPEN);
                }

            }
        }

    }
}
