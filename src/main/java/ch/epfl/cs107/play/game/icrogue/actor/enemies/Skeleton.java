package ch.epfl.cs107.play.game.icrogue.actor.enemies;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.List;

public class Skeleton extends Enemy implements Interactor {
    static final String spriteName = "other/skeleton";
    Animation[] animations;
    static float spriteSize = 0.6f;
    static float maxHealth = 5.f;
    static int damage = 1;
    static int MOVEDURATION = 10;
    private DiscreteCoordinates destination;
    private InteractionHandler handler;
    List<DiscreteCoordinates> fieldOfView = new ArrayList<>();


    /**
     * Init all useful attributes
     * @param area (Area): owner Area
     * @param orientation (Orientation): orientation of the skeleton
     * @param position (DiscreteCoordinates): position of the entity on the map
     */
    public Skeleton(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, spriteSize);
        handler = new InteractionHandler();
        // Add all cells of the map to the view of the skeleton, so he can find the player
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                fieldOfView.add(new DiscreteCoordinates(i, j));
            }
        }
        Orientation[] orientationSprite = new Orientation[]{Orientation.DOWN,Orientation.LEFT, Orientation.UP, Orientation.RIGHT};
        Sprite[][] sprites = Sprite.extractSprites(spriteName,4,.8f,.8f,this,16,16,orientationSprite);
        animations = Animation.createAnimations(4,sprites);

    }

    @Override
    public void update(float deltaTime) {
        // While de skeleton did not reach the player, try to move in his direction
        if (destination != null && destination != getCurrentMainCellCoordinates() && !isDisplacementOccurs()) {
            computeMove();
        }

        if (isDisplacementOccurs()) {
            animations[getOrientation().ordinal()].update(deltaTime);
        }
        super.update(deltaTime);
    }

    /**
     * Simple algorithme to move toward the player
     */
    private void computeMove() {
        // Get the dela x and y
        DiscreteCoordinates currentPos = getCurrentMainCellCoordinates();
        int dx = destination.x - currentPos.x;
        int dy = destination.y - currentPos.y;
        // Then compute the proba of X to be the axis the skeleton choose to move
        // There is more chance that the skeleton choose the axis where he has the
        // longest distance to parkour
        float probaOfX = (float)Math.abs(dx) / (float)(Math.abs(dy) + Math.abs(dx));
        double randomNumber = Math.random();
        // Orientate the skeleton in the right direction
        if (randomNumber < probaOfX) {
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
    public void draw(Canvas canvas) {
        animations[getOrientation().ordinal()].draw(canvas);
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
    public boolean isViewInteractable() {
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

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }


    private class InteractionHandler implements ICRogueInteractionHandler {
        @Override
        public void interactWith(ICRoguePlayer player, boolean isCellInteraction) {
            // because the skeleton has all cells of the map in his fieldOfView, when he interacts with
            // the player and has a view interaction, it just defines this position has the new
            // destination to reach it.
            // If it's a cell interaction it just damages the player and kill itself
            if (!isCellInteraction) {
                destination = new DiscreteCoordinates((int)player.getPosition().x, (int)player.getPosition().y);
            } else {
                player.damage(damage);
                die();
            }
        }
    }
}
