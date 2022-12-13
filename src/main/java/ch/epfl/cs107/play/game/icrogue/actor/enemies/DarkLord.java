package ch.epfl.cs107.play.game.icrogue.actor.enemies;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icrogue.actor.projectiles.FireBallDarkLord;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.HashMap;

public class DarkLord extends Enemy{
    private  Animation[] animationsMove;
    private Sprite[][] spritesMove;
    private final Orientation roomOrientation;
    private final static int COOLDOWN_SHOOT = 2;
    private float lastShotTime;
    public DarkLord(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, "zelda/darkLord", 1f);
        roomOrientation = orientation;
        System.out.println(orientation);

        Orientation[] orientations = new Orientation[]{Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT};
        // animations = Sprite.extractSprites("zelda/darkLord",3,1.5f,1.5f,this,32,32,orientations);
        spritesMove = Sprite.extractSprites("zelda/darkLord", 4, 1.5f, 1.5f, this,
                32, 32, new Vector(.15f, 0.3f), orientations);
        animationsMove = Animation.createAnimations(4, spritesMove);
        lastShotTime = 0.f;
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        animationsMove[roomOrientation.ordinal()].draw(canvas);
    }
    private Orientation[] chooseNextMove(){
        if (roomOrientation.equals(Orientation.DOWN) || roomOrientation.equals(Orientation.UP)){
            return new Orientation[]{Orientation.LEFT,Orientation.RIGHT};
        }
        return new Orientation[]{Orientation.DOWN,Orientation.UP};
    }
    private void summonFlameSkull(){
        Orientation[] flameSkullVectorsOrientation = chooseNextMove();
        for (Orientation orientation: flameSkullVectorsOrientation) {
            new FireBallDarkLord(getOwnerArea(),roomOrientation,getCurrentMainCellCoordinates().jump(orientation.toVector()));
        }
    }
    private float getProbaOfMove(){
        if (roomOrientation.equals(Orientation.UP) || roomOrientation.equals(Orientation.DOWN)){
            return (float)(0.1 * (4.5-getCurrentMainCellCoordinates().x));
        }
        return (float)(0.1 * (4.5-getCurrentMainCellCoordinates().y));
    }
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        lastShotTime += deltaTime;
        if (lastShotTime > COOLDOWN_SHOOT){
            summonFlameSkull();
            lastShotTime = 0;
        }
        if (isDisplacementOccurs()) {
            animationsMove[roomOrientation.ordinal()].update(deltaTime);
        } else {
            double randomMove = 0.5 + getProbaOfMove();
            System.out.println(randomMove);
            if (Math.random()>randomMove){
                orientate(chooseNextMove()[0]);
            }else{
                orientate(chooseNextMove()[1]);
            }
            move(24);
        }
    }
}
