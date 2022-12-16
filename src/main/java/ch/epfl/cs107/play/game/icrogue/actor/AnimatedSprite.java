package ch.epfl.cs107.play.game.icrogue.actor;

import ch.epfl.cs107.play.game.Updatable;
import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.window.Canvas;


public class AnimatedSprite implements Graphics {
    private Animation[] animations;
    public AnimatedSprite(Animation[] animations){
        this.animations = animations;
    }

    public void update(float deltaTime, Orientation spriteOrientation) {
        animations[spriteOrientation.ordinal()].update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {

    }
}
