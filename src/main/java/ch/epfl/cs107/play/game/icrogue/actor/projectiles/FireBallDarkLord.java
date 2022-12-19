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
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Skeleton;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Turret;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.game.icrogue.visualEffects.MacronExplosion;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class FireBallDarkLord extends Projectiles {
    private final InteractionHandler handler;
    private final Animation[] animation;

    public FireBallDarkLord(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position.jump(orientation.toVector()), 1, 10);
        Orientation[] orientations = new Orientation[]{Orientation.UP,Orientation.LEFT,Orientation.DOWN,Orientation.RIGHT};
        float offset = 0.25f;
        Vector offsetVect = new Vector(Math.abs(orientation.toVector().x*offset), Math.abs(orientation.toVector().y* offset));
        Sprite[][] sprites = Sprite.extractSprites("zelda/flameskull", 4, 1.5f, 1.5f, this,
                32, 32, offsetVect, orientations);
        animation = Animation.createAnimations(4, sprites);
        area.registerActor(this);
        handler = new InteractionHandler();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animation[getOrientation().ordinal()].update(deltaTime);
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

    private void explode(DiscreteCoordinates coord){
        new MacronExplosion(getOwnerArea(), getOrientation(), coord);
    }

    private class InteractionHandler implements ICRogueInteractionHandler {
        @Override
        public void interactWith(ICRogueBehavior.ICRogueCell cell, boolean isCellInteraction) {
            if (cell.getType().equals(ICRogueBehavior.ICRogueCellType.WALL) ||
                    (cell.getType().equals(ICRogueBehavior.ICRogueCellType.HOLE) && isCellInteraction)) {
                consume();
                if (Math.random() < 0.33) {
                    new Skeleton(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates());
                }
            }

        }
        public void interactWith(Connector connector, boolean isCellInteraction){
            if (!connector.getState().equals(Connector.ConnectorType.OPEN)){
                consume();
            }
        }

        @Override
        public void interactWith(ICRoguePlayer player, boolean isCellInteraction) {
            player.damage(1);
            consume();
            explode(getCurrentMainCellCoordinates());
        }
    }
}
