package ch.epfl.cs107.play.game.icrogue.actor.projectiles;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.ICRogueBehavior;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.actor.items.Cherry;
import ch.epfl.cs107.play.game.icrogue.actor.items.Staff;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class FireBall extends Projectiles {
    private Sprite sprite;
    private InteractionHandler handler;

    public FireBall(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, 1, 5);
        sprite = new Sprite("zelda/fire", 1.f, 1.f, this, new RegionOfInterest(0, 0, 16,16), new Vector(0, 0));
        area.registerActor(this);
        handler = new InteractionHandler();
    }

    @Override
    public void update(float deltaTime) {
        move(super.MOVE_DURATION);
        super.update(deltaTime);
    }



    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }


    @Override
    public void consume() {
        getOwnerArea().unregisterActor(this);
    }

    @Override
    public boolean isConsumed() {
        return false;
    }
    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler , isCellInteraction);
    }
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {

        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);

    }

    private class InteractionHandler implements ICRogueInteractionHandler {
        @Override
        public void interactWith(ICRogueBehavior.ICRogueCell cell, boolean isCellInteraction) {
            System.out.println("walll");
            if (cell.getType() == ICRogueBehavior.ICRogueCellType.WALL || cell.getType() == ICRogueBehavior.ICRogueCellType.HOLE) {
                consume();
            }

        }
    }

}
