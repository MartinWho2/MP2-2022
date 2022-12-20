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
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
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
    private boolean wantToSwitch;
    private Orientation nextOrientation;
    private boolean wantToExplod;
    public static final float MAX_TIMER = 0.4f;
    private float timer = 0;

    /**
     * Init useful attributes
     * @param area (Area): owner Area
     * @param orientation (Orientate): orientation of the projectile
     * @param position (DiscreteCoordinates): position of spawn
     */
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
        wantToSwitch = false;
        wantToExplod = false;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animation[getOrientation().ordinal()].update(deltaTime);
        if (shouldExplose) {
            explode();
        }

        if (wantToExplod) {
            timer += deltaTime;
            if (timer >= MAX_TIMER && !switchedDirection) {
                explode();
                wantToExplod = false;
                timer = 0;
            }
        }

        if (wantToSwitch) {
            if (isTargetReached()) {
                resetMotion();
                setCurrentPosition(getCurrentMainCellCoordinates().toVector());
                System.out.println(nextOrientation);
                multipleExplosion = true;
                orientate(nextOrientation);
                //animation[getOrientation().ordinal()].setAnchor(new Vector(0.69f, 0));
                switchedDirection = true;
                wantToSwitch = false;
            }
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
        // change field of view if it exploded
        // else, just return the front cell
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

    /**
     * Spawn explosion on cells in field of view and consume the projectile
     */
    private void explode(){
        exploded = true;
        shouldExplose = false;
        List<DiscreteCoordinates> coords = getFieldOfViewCells();
        for (DiscreteCoordinates coord : coords) {
            new Explosion(getOwnerArea(), getOrientation(), coord);
        }
        consume();
    }

    /**
     * reorientate the fireball when it is it by reseting motion, recentering fireball in the center
     * of a cell and chaneg it's orientation
     * @param newOrientation (Orientation): new orientation
     */
    public void repulse(Orientation newOrientation) {

        if (!switchedDirection) {
            if (isTargetReached()) {
                System.out.println("is target reached: " + isTargetReached());
                System.out.println(isTargetReached());
                resetMotion();
                setCurrentPosition(getCurrentMainCellCoordinates().toVector());
                System.out.println(newOrientation);
                multipleExplosion = true;
                orientate(newOrientation);

                //animation[getOrientation().ordinal()].setAnchor(new Vector(0.69f, 0));
                switchedDirection = true;
            }
            nextOrientation = newOrientation;
            wantToSwitch = true;
            switchedDirection = true;
        }
    }

    private class InteractionHandler implements ICRogueInteractionHandler {
        @Override
        public void interactWith(ICRogueBehavior.ICRogueCell cell, boolean isCellInteraction) {
            // explose if it touches a wall or a connector and spawn a skeleton sometimes
            if ((cell.getType().equals(ICRogueBehavior.ICRogueCellType.WALL) ||
                    (cell.getType().equals(ICRogueBehavior.ICRogueCellType.HOLE) && isCellInteraction))  && !exploded) {
                shouldExplose = true;
                if (Math.random() < 0.33 && !switchedDirection) {
                    new Skeleton(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates());
                }
            }

        }
        public void interactWith(Connector connector, boolean isCellInteraction){
            // explose when it touches a connector
            if (!connector.getState().equals(Connector.ConnectorType.OPEN)){
                shouldExplose = true;
            }
        }

        @Override
        public void interactWith(ICRoguePlayer player, boolean isCellInteraction) {
            // damage the player
            if (exploded) {
                player.damage(getDamages());
            } else if (isCellInteraction && !switchedDirection) {
                wantToExplod = true;
    /*            player.damage(getDamages());
                explode();*/
            }
        }

        @Override
        public void interactWith(DarkLord darkLord, boolean isCellInteraction) {
            // damage the darklord
            if (exploded) {
                darkLord.damage(getDamages());
                ((ICRogueRoom)getOwnerArea()).tryToFinishRoom();
            }
        }
    }
}
