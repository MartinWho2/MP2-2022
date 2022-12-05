package ch.epfl.cs107.play.game.icrogue.actor.projectiles;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.ICRogueBehavior;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Turret;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class FireBall extends Projectiles {
    private Sprite sprite;
    private InteractionHandler handler;
    private Sprite[] sprites;
    private Animation animation;

    public FireBall(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, 1, 5);
        sprites = new Sprite[7];
        for (int i = 0; i < 7; i++) {
            sprites[i] = new Sprite("zelda/fire", 1.f, 1.f, this,
                    new RegionOfInterest(16*i, 0, 16,16), new Vector(0, 0));
        }
        //sprite = new Sprite("zelda/fire", 1.f, 1.f, this, new RegionOfInterest(0, 0, 16,16), new Vector(0, 0));
        animation = new Animation(7, sprites);
        animation.setSpeedFactor(3);
        area.registerActor(this);
        handler = new InteractionHandler();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animation.update(deltaTime);
        System.out.println(getOwnerArea());
    }
    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
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

    private class InteractionHandler implements ICRogueInteractionHandler {
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

        @Override
        public void interactWith(Turret turret, boolean isCellInteraction) {
            if (!isCellInteraction){
                turret.die();
                consume();
                ICRogueRoom area = (ICRogueRoom) getOwnerArea();
                area.tryToFinishRoom();
            }
        }
    }

}
