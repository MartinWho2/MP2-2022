package ch.epfl.cs107.play.game.icrogue.actor.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

public class Staff extends Item {
    private Sprite[] sprites;
    private Animation animation;

    public Staff(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, "zelda/staff_water.icon", .5f);
        sprites = new Sprite[8];
        for (int i = 0; i < 8; i++) {
            sprites[i] = new Sprite("zelda/staff",0.5f,0.5f,this,
                    new RegionOfInterest(32*i, 0, 32, 32));
        }
        animation = new Animation(8, sprites, true);
        animation.setSpeedFactor(2);
    }

    @Override
    public void update(float deltaTime) {
        animation.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isCollected()) {
            animation.draw(canvas);
        }
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }
    public boolean isCellInteractable(){return false;}

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }
}
