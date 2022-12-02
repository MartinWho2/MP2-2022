package ch.epfl.cs107.play.game.icrogue.actor.projectiles;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.ICRogueActor;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public abstract class Projectiles extends ICRogueActor implements Consumable, Interactor {
    private Sprite sprite;
    private boolean isConsumed;
    private static int DEFAULT_MOVE_DURATION = 8;
    private static int DEFAULT_DAMAGE = 1;
    public int MOVE_DURATION;
    private int damage;


    public Projectiles(Area area, Orientation orientation, DiscreteCoordinates position, int damage, int duration) {
        super(area, orientation, position);
        this.damage = damage;
        this.MOVE_DURATION = duration;
        isConsumed = false;
    }
    public Projectiles(Area area, Orientation orientation, DiscreteCoordinates position) {
        this(area, orientation, position, DEFAULT_DAMAGE, DEFAULT_MOVE_DURATION);
    }


    @Override
    public void update(float deltaTime) {
        move(MOVE_DURATION);
        super.update(deltaTime);
    }

    private void setDamage(int damage) {
        this.damage = damage;
    }
    public void consume() {
        isConsumed = true;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return true;
    }

    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }
}
