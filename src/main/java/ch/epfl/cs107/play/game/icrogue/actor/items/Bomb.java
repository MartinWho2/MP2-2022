package ch.epfl.cs107.play.game.icrogue.actor.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.DarkLord;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Skeleton;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Turret;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.game.icrogue.visualEffects.Explosion;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bomb extends Item implements Interactor {
    private final InteractionHandler handler;
    private final Animation animation;
    private boolean isPlaced = false;
    private boolean exploded;

    /**
     * Init all useful attributes
     * @param area (Area): owner Area
     * @param orientation (Orientation): orientation of the skeleton
     * @param position (DiscreteCoordinates): position of the entity on the map
     */
    public Bomb(Area area, Orientation orientation, DiscreteCoordinates position){
        super(area, orientation, position, "other/bomb-icon", 0.6f);
        handler = new InteractionHandler();
        Sprite[] sprites = Sprite.extractSprites("other/bomb",7,.6f,.6f,this,16,16);
        animation = new Animation(6,sprites,false);
        exploded = false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        List<DiscreteCoordinates> cells = new ArrayList<>();
        if (isCollected()) {
            // If the bomb explose it interacts with all surrounding cells as well
            if (isPlaced && exploded) {
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        if ((Math.abs(i) + Math.abs(j)) == 1)
                            cells.add(getCurrentMainCellCoordinates().jump(i, j));
                    }
                }
            }
        }
        // If the bomb is not collected it has an empty field of view
        return cells;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // Cooldown before the explosion of the bomb
        if (isPlaced) {
            animation.update(deltaTime);
           if (animation.isCompleted()){
                explode();
            }
        }
    }

    /**
     * generate explosion graphics on all cells that are in the field of view
     */
    public void explode() {
        exploded = true;
        List<DiscreteCoordinates> coords = getFieldOfViewCells();
        coords.add(getCurrentMainCellCoordinates());
        for (DiscreteCoordinates coord : coords) {
            new Explosion(getOwnerArea(), getOrientation(), coord);
        }
        getOwnerArea().unregisterActor(this);
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isCollected()){
            super.draw(canvas);
        }
        if (isPlaced) {
            animation.draw(canvas);
        }
    }

    /**
     * Place a bomb on the ground after having collected it
     * @param coordinates (DiscreteCoordinates): coordinate of spawn of the bomb
     * @param area (Area): owner area of the bomb
     */
    public void placeBomb(DiscreteCoordinates coordinates, Area area) {
        area.registerActor(this);
        setOwnerArea(area);
        setCurrentPosition(coordinates.toVector());
        isPlaced = true;
    }

    @Override
    public void tryToUseItem() {
        getItemUseListener().canUseItem(this);
    }

    @Override
    public void useItem(Area area, Orientation orientation, DiscreteCoordinates coords) {
        placeBomb(coords, area);
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }
    // The bomb
    @Override
    public boolean wantsViewInteraction() {
        return isCollected();
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler , isCellInteraction);
    }
    private class InteractionHandler implements ICRogueInteractionHandler {
        @Override
        public void interactWith(Connector connector, boolean isCellInteraction) {
            // Open connectors that are of type "CRACKED" if the bomb explose
            if (isPlaced) {
                if (connector.getState().equals(Connector.ConnectorType.CRACKED)) {
                     connector.setState(Connector.ConnectorType.OPEN);
                }
            }
        }

        @Override
        public void interactWith(ICRoguePlayer player, boolean isCellInteraction) {
            // damage the player when it exploses
            if (isPlaced && exploded) {
                player.damage(1);
            }
        }

        @Override
        public void interactWith(Turret turret, boolean isCellInteraction) {
            // damage the turret when bomb exploses
            if (isPlaced && exploded){
                turret.die();
            }
        }
        @Override
        public void interactWith(Skeleton skeleton, boolean isCellInteraction) {
            // damage skeletons when bomb exploses
            if (isPlaced && exploded) {
                skeleton.die();
            }
        }

        @Override
        public void interactWith(DarkLord darkLord, boolean isCellInteraction) {
            // damages the dark lord when the bomb explodes
            darkLord.damage(2);
        }
    }
}
