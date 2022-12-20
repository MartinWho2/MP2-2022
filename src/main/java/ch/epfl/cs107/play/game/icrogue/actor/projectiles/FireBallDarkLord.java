package ch.epfl.cs107.play.game.icrogue.actor.projectiles;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.ICRogueBehavior;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.DarkLord;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Skeleton;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.game.icrogue.visualEffects.Explosion;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FireBallDarkLord extends Projectiles {
    private final InteractionHandler handler;
    private final Animation[] animation;
    private boolean exploded;
    private boolean shouldExplose;
    private boolean multipleExplosion;
    private boolean switchedDirection;

    public FireBallDarkLord(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position.jump(orientation.toVector()), 1, 10);
        Orientation[] orientations = new Orientation[]{Orientation.UP,Orientation.LEFT,Orientation.DOWN,Orientation.RIGHT};
        float offset = 0.25f;
        Vector offsetVect = new Vector(Math.abs(orientation.toVector().x*offset), Math.abs(orientation.toVector().y* offset));
        Sprite[][] sprites = Sprite.extractSprites("zelda/flameskull", 3, 1.5f, 1.5f, this,
                32, 32, offsetVect, orientations);
        animation = Animation.createAnimations(4, sprites);
        area.registerActor(this);
        handler = new InteractionHandler();
        exploded = false;
        shouldExplose = false;
        switchedDirection = false;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animation[getOrientation().ordinal()].update(deltaTime);
        if (shouldExplose) {
            explode();
        }

    }
    @Override
    public void draw(Canvas canvas) {
        animation[getOrientation().ordinal()].draw(canvas);
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public void consume() {
        super.consume();
        getOwnerArea().unregisterActor(this);
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler , isCellInteraction);
    }
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        List<DiscreteCoordinates> cells = new ArrayList<>();
        getOrientation().hisLeft();

        if (exploded) {
            if (multipleExplosion){
                cells.add(getCurrentMainCellCoordinates().jump(getOrientation().hisRight().toVector()));
                cells.add(getCurrentMainCellCoordinates().jump(getOrientation().hisLeft().toVector()));
            }
            cells.add(getCurrentMainCellCoordinates());
        } else {
            return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
        }
        return cells;
    }

    private void explode(){
        exploded = true;
        shouldExplose = false;
        List<DiscreteCoordinates> coords = getFieldOfViewCells();
        for (DiscreteCoordinates coord : coords) {
            new Explosion(getOwnerArea(), getOrientation(), coord);
        }
        System.out.println(getFieldOfViewCells());
        consume();
    }


    public void repulse() {
        if (!switchedDirection) {
            resetMotion();
            setCurrentPosition(getCurrentMainCellCoordinates().toVector());
            System.out.println( newOrientation);
            multipleExplosion = true;
            orientate(getOrientation().opposite());
            switchedDirection = true;
        }
    }
    private class InteractionHandler implements ICRogueInteractionHandler {
        @Override
        public void interactWith(ICRogueBehavior.ICRogueCell cell, boolean isCellInteraction) {
            if ((cell.getType().equals(ICRogueBehavior.ICRogueCellType.WALL) ||
                    (cell.getType().equals(ICRogueBehavior.ICRogueCellType.HOLE) && isCellInteraction))  && !exploded) {
                shouldExplose = true;
                if (Math.random() < 0.33) {
                    new Skeleton(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates());
                }
            }

        }
        public void interactWith(Connector connector, boolean isCellInteraction){
            if (!connector.getState().equals(Connector.ConnectorType.OPEN)){
                shouldExplose = true;
            }
        }

        @Override
        public void interactWith(ICRoguePlayer player, boolean isCellInteraction) {
            if (exploded) {
                player.damage(getDamages());
            }
        }

        @Override
        public void interactWith(DarkLord darkLord, boolean isCellInteraction) {
            System.out.println();
            if (exploded) {
                darkLord.damage(getDamages());
            }
        }
    }
}
