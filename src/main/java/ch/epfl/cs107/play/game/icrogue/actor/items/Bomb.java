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
import ch.epfl.cs107.play.game.icrogue.visualEffects.Explosion;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bomb extends Item implements Interactor {
    private InteractionHandler handler;
    private boolean isPlaced = false;
    private boolean placing = false;
    private final static float COOLDOWN = 4.f;
    private float time = 0.f;
    private boolean exploded;

    public Bomb(Area area, Orientation orientation, DiscreteCoordinates position){
        super(area, orientation, position, "other/bomb", 0.6f);
        handler = new InteractionHandler();
        exploded = false;
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        List<DiscreteCoordinates> cells = new ArrayList<>();
        if (isCollected()) {
            if (isPlaced && exploded) {
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        if ((Math.abs(i) + Math.abs(j)) == 1)
                            cells.add(getCurrentMainCellCoordinates().jump(i, j));
                    }
                }
            } else {
                return Collections.singletonList(getCurrentMainCellCoordinates());
            }
        }
        return cells;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (isPlaced) {
            if (time < COOLDOWN) {
                time += deltaTime;
            } else {
                explode();
            }
        }
    }

    public void explode() {
        exploded = true;
        List<DiscreteCoordinates> coords = getFieldOfViewCells();
        for (DiscreteCoordinates coord : coords) {
            new Explosion(getOwnerArea(), getOrientation(), coord);
        }
        getOwnerArea().unregisterActor(this);
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isCollected() || isPlaced) {
            sprite.draw(canvas);
        }
    }


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
            if (isPlaced) {
                if (connector.getState().equals(Connector.ConnectorType.CRACKED)) {
                     connector.setState(Connector.ConnectorType.OPEN);
                }
            }
        }

        @Override
        public void interactWith(ICRoguePlayer player, boolean isCellInteraction) {
            if (isPlaced && exploded) {
                player.kill();
            }
        }

        @Override
        public void interactWith(Turret turret, boolean isCellInteraction) {
            if (isPlaced && exploded){
                turret.die();
            }
        }
        @Override
        public void interactWith(Skeleton skeleton, boolean isCellInteraction) {
            if (isPlaced && exploded) {
                skeleton.die();
            }
        }
    }
}
