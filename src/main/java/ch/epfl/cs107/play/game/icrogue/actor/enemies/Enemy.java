package ch.epfl.cs107.play.game.icrogue.actor.enemies;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.ICRogueActor;
import ch.epfl.cs107.play.game.icrogue.actor.items.Bomb;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;


public abstract class Enemy extends ICRogueActor {
    private boolean isAlive;
    private Sprite sprite;
    public Enemy(Area area, Orientation orientation, DiscreteCoordinates position, String spriteName, float size){
        super(area, orientation,position);
        enterArea(area, position);
        isAlive = true;
        sprite = new Sprite(spriteName,size,size,this);
    }
    public boolean getIsAlive(){
        return isAlive;
    }
    public void die(){
        leaveArea();
        isAlive = false;

    }
    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {

    }

}
