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
    static String spriteName = "other/skeleton";
    Animation[] animations;
    static float spirteSize = 0.6f;
    static float maxHealth = 5.f;
    static int damage = 1;
    static int MOVEDURATION = 10;
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
        Orientation[] orientationSprite = new Orientation[]{Orientation.DOWN,Orientation.LEFT, Orientation.UP, Orientation.RIGHT};
        Sprite[][] sprites = Sprite.extractSprites(spriteName,4,.8f,.8f,this,16,16,orientationSprite);
        animations = Animation.createAnimations(4,sprites);

    }

    @Override
    public void update(float deltaTime) {
        if (destination != null && destination != getCurrentMainCellCoordinates() && !isDisplacementOccurs()) {
            computeMove();
        }
        if (isDisplacementOccurs()) {
            animations[getOrientation().ordinal()].update(deltaTime);
        }
        super.update(deltaTime);

    }
    private void computeMove() {
        DiscreteCoordinates currentPos = getCurrentMainCellCoordinates();
        int dx = destination.x - currentPos.x;
        int dy = destination.y - currentPos.y;
        float probaOfX = (float)Math.abs(dx) / (float)(Math.abs(dy) + Math.abs(dx));
        double randomNumber = Math.random();
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
            if (!isCellInteraction) {
                destination = new DiscreteCoordinates((int)player.getPosition().x, (int)player.getPosition().y);
            } else {
                player.damage(damage);
                die();
            }
        }
    }
}
