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
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.game.icrogue.handler.ItemUseListener;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.List;

public class Sword extends Item implements Interactor {
    private boolean isBeingUsed;
    private InteractionHandler handler;
    private final static float ANIMATION_TIME = 1f;
    private float timer;

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }
    public Sword(Area area, Orientation orientation, DiscreteCoordinates position){
        super(area, orientation, position,"zelda/sword.icon",0.6f);
        isBeingUsed = false;
        handler = new InteractionHandler();
        timer = 0;
    }
    @Override
    public void tryToUseItem() {
        getItemUseListener().canUseItem(this);
    }

    @Override
    public boolean isViewInteractable() {
        return isBeingUsed;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (isCollected()) {

            timer += deltaTime;
            if (timer > ANIMATION_TIME){
                getOwnerArea().unregisterActor(this);
                timer = 0;
            }
        }
    }

    @Override
    public void useItem(Area area, Orientation orientation, DiscreteCoordinates coords) {
        area.registerActor(this);
        System.out.println("epeeeeeee");
        setCurrentPosition(coords.toVector());
        orientate(orientation);
        System.out.println("bonjour je suis olivier de chez carglass  "+getFieldOfViewCells());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        List<DiscreteCoordinates> coords = new ArrayList<>();
        coords.add(getCurrentMainCellCoordinates());
        coords.add(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
        return coords;
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
            System.out.println("Un couteau bien placé en moind de 2 je déboule j't'enlève la vie");
            System.out.println(getCurrentCells());
            skeleton.die();
        }

        @Override
        public void interactWith(Turret turret, boolean isCellInteraction) {

            turret.die();
        }
    }
}
