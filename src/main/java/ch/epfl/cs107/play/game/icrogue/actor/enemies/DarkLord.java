package ch.epfl.cs107.play.game.icrogue.actor.enemies;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.HashMap;

public class DarkLord extends Enemy{
    private double randomMove;
    private Sprite[][] animations;
    private Orientation[] orientations;
    private HashMap<Orientation,Sprite[]> orientationToSprite;
    public DarkLord(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, "zelda/darkLord", 1f);
        orientations = new Orientation[]{Orientation.UP,Orientation.LEFT,Orientation.DOWN,Orientation.RIGHT};
        animations = Sprite.extractSprites("zelda/darkLord",3,32,32,this,96,128,orientations);
        for (int i = 0; i < orientations.length; i++) {
            orientationToSprite.put(orientations[i],animations[i]);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        randomMove = Math.random();
        if (randomMove<0.1){
            orientate(Orientation.UP);
            move(10);
        }
        else if (randomMove>0.9){
            orientate(Orientation.DOWN);
            move(5);
        }

    }
}
