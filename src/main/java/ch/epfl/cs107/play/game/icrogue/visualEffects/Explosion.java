package ch.epfl.cs107.play.game.icrogue.visualEffects;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.ICRogueActor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
// Class to simulate the explosions
public class Explosion extends ICRogueActor {
    private Sprite[] sprites;
    private Animation animation;

    public Explosion(Area area, Orientation orientation, DiscreteCoordinates coordinates) {
        super(area, orientation, coordinates);
        // Loads the animated sprite
        sprites = Sprite.extractSprites("zelda/explosion", 7, 2.f, 2.f, this, new Vector(-.5f, -.5f), 32, 32);
        animation = new Animation(7, sprites, false);
        animation.setSpeedFactor(4);
        enterArea(area, coordinates);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animation.update(deltaTime);
        // If the animation is finished, remove it from the area
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
