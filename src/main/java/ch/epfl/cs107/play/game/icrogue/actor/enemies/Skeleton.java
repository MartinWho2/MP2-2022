package ch.epfl.cs107.play.game.icrogue.actor.enemies;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.List;

public class Skeleton extends Enemy implements Interactor {
    static String spriteName = "other/skeleton";
    static float spirteSize = 0.6f;
    static float maxHealth = 5.f;
    static int damage = 1;
    static int MOVEDURATION = 8;
    private DiscreteCoordinates destination;
    private InteractionHandler handler;
    List<DiscreteCoordinates> fieldOfView = new ArrayList<>();



    public Skeleton(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, spriteName, spirteSize);
        handler = new InteractionHandler();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                fieldOfView.add(new DiscreteCoordinates(i, j));
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        if (destination != null && destination != getCurrentMainCellCoordinates() && !isDisplacementOccurs()) {
            computeMove();
        }
        super.update(deltaTime);

    }
    private void computeMove() {
        DiscreteCoordinates currentPos = getCurrentMainCellCoordinates();
        int dx = destination.x - currentPos.x;
        int dy = destination.y - currentPos.y;
        float probaOfX = (float)Math.abs(dx) / (float)(Math.abs(dy) + Math.abs(dx));
        double randomNumber = Math.random();
        System.out.println(probaOfX + " is proba of x");
        if (randomNumber < probaOfX) {
            System.out.println("I was here");
            if (dx > 0) {
                orientate(Orientation.RIGHT);
            } else {
                orientate(Orientation.LEFT);
            }
            move(MOVEDURATION);
        } else {
            if (dy > 0) {
                orientate(Orientation.UP);
            } else {
                orientate(Orientation.DOWN);
            }
            move(MOVEDURATION);
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return super.getCurrentCells();
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return fieldOfView;
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return true;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    private class InteractionHandler implements ICRogueInteractionHandler {
        @Override
        public void interactWith(ICRoguePlayer player, boolean isCellInteraction) {
            if (!isCellInteraction) {
                destination = new DiscreteCoordinates((int)player.getPosition().x, (int)player.getPosition().y);
            } else {
                player.damage(damage);
                die();
            }
        }
    }
}
