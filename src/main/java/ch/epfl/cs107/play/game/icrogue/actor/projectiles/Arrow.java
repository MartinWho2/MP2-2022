package ch.epfl.cs107.play.game.icrogue.actor.projectiles;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.ICRogueBehavior;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class Arrow extends Projectiles{
    private Sprite sprite;
    private boolean isAlive;
    private InteractionHandler handler;


    /**
     * Init useful attributes
     * @param area (Area): owner Area
     * @param orientation (Orientate): orientation of the projectile
     * @param coord (DiscreteCoordinates): position of spawn
     */
    public Arrow(Area area, Orientation orientation, DiscreteCoordinates coord) {
        super(area, orientation, coord, 1, 8);
        sprite = new Sprite("zelda/arrow", 1f, 1f, this ,
                new RegionOfInterest(32* orientation.ordinal() , 0, 32 , 32) ,
                new Vector(0 , 0));
        area.registerActor(this);
        isAlive = true;
        handler = new InteractionHandler();
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
        super.consume();
        getOwnerArea().unregisterActor(this);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {

        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);

    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler , isCellInteraction);
    }



    private class InteractionHandler implements ICRogueInteractionHandler {
        @Override
        public void interactWith(ICRoguePlayer player, boolean isCellInteraction) {
            consume();
            player.kill();
            // more
        }

        @Override
        public void interactWith(ICRogueBehavior.ICRogueCell cell, boolean isCellInteraction) {
            if (cell.getType().equals(ICRogueBehavior.ICRogueCellType.WALL) ||
                    (cell.getType().equals(ICRogueBehavior.ICRogueCellType.HOLE) && isCellInteraction)) {
                consume();
            }

        }
        public void interactWith(Connector connector, boolean isCellInteraction){
            if (!connector.getState().equals(Connector.ConnectorType.OPEN)){
                consume();
            }
        }
    }
}
