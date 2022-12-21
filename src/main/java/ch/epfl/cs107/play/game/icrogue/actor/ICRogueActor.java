package ch.epfl.cs107.play.game.icrogue.actor;

import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.Collections;
import java.util.List;

public abstract class ICRogueActor extends MovableAreaEntity {
    private TextGraphics dialogs;
    public ICRogueActor(Area area, Orientation orientation, DiscreteCoordinates spawn) {
        super(area, orientation, spawn);
    }

    /**
     * set if the entity is the only one allowed on a cell
     * @return If it takes all the space
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     *
     * @return The cells currently occupied
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }
    /**
     *
     * @param area (Area): initial area, not null
     * @param position (DiscreteCoordinates): initial position, not null
     */
    public void enterArea(Area area, DiscreteCoordinates position) {
        area.registerActor(this);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        resetMotion();
    }

    /**
     * unregister actor from current area
     */
    public void leaveArea() {
        getOwnerArea().unregisterActor(this);
    }

    /**
     * set contact interaction
     * @return If it accepts contact interaction or not
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

}
