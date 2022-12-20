package ch.epfl.cs107.play.game.icrogue.actor.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Skeleton;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Turret;
import ch.epfl.cs107.play.game.icrogue.actor.projectiles.FireBallDarkLord;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.game.icrogue.handler.ItemUseListener;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sword extends Item implements Interactor {
    private boolean isBeingUsed;
    private InteractionHandler handler;
    private final static float ANIMATION_TIME = .6f;
    private float timer;

    /**
     * Init all useful attributes
     * @param area (Area): owner Area
     * @param orientation (Orientation): orientation of the skeleton
     * @param position (DiscreteCoordinates): position of the entity on the map
     */
    public Sword(Area area, Orientation orientation, DiscreteCoordinates position){
        super(area, orientation, position,"zelda/sword.icon",0.6f);
        isBeingUsed = false;
        handler = new InteractionHandler();
        timer = 0;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }

    @Override
    public void tryToUseItem() {
        getItemUseListener().canUseItem(this);
    }

    @Override
    public boolean takeCellSpace() {
        return !isCollected();
    }

    @Override
    public boolean isViewInteractable() {
        // If it is not collected, always true
        if (!isCollected()){
            return true;
        }
        // Otherwise returns if it is currently being used
        return isBeingUsed;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // register the sword when it is use and unregister it after a short period of time
        if (isCollected()) {
            timer += deltaTime;
            if (timer > ANIMATION_TIME){
                getOwnerArea().unregisterActor(this);
                isBeingUsed = false;
                timer = 0;
            }
        }
    }

    @Override
    public void useItem(Area area, Orientation orientation, DiscreteCoordinates coords) {
        // register the item to the specified area
        area.registerActor(this);
        setOwnerArea(area);
        setCurrentPosition(coords.toVector());
        orientate(orientation);
        isBeingUsed = true;
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return isCollected();
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    private class InteractionHandler implements ICRogueInteractionHandler {
        @Override
        public void interactWith(Skeleton skeleton, boolean isCellInteraction) {
            // kill skeletons
            if (skeleton.getIsAlive() && !isCellInteraction) {
                skeleton.die();
            }
        }

        @Override
        public void interactWith(Turret turret, boolean isCellInteraction) {
            // kill turrets
            turret.die();
        }

        @Override
        public void interactWith(FireBallDarkLord fireBallDarkLord, boolean isCellInteraction) {
            // reorientate de fireball
            if (!isCellInteraction || getOrientation().opposite().equals(fireBallDarkLord.getOrientation())) {
                System.out.println("orientation : " + getOrientation());
                fireBallDarkLord.repulse(getOrientation());
            }
        }
    }
}
