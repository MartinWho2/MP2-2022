package ch.epfl.cs107.play.game.icrogue.visualEffects;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.ICRogueActor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class MacronExplosion extends ICRogueActor {
    private Sprite[] sprites;
    private Animation animation;

    public MacronExplosion(Area area, Orientation orientation, DiscreteCoordinates coordinates) {
        super(area, orientation, coordinates);
        /*sprites = new Sprite[7];
        for (int i = 0; i < 7; i++) {
            sprites[i] = new Sprite("zelda/explosion", 2.f, 2.f, this,
                    new RegionOfInterest(32*i, 0, 32,32), new Vector(-.5f, -.5f));
        }*/
        sprites = Sprite.extractSprites("zelda/explosion", 7, 2.f, 2.f, this, new Vector(-.5f, -.5f), 32, 32);
        animation = new Animation(7, sprites, false);
        animation.setSpeedFactor(4);
        enterArea(area, coordinates);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animation.update(deltaTime);
        if (animation.isCompleted()) {
            leaveArea();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
    }
}
