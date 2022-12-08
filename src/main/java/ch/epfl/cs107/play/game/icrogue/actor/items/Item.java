package ch.epfl.cs107.play.game.icrogue.actor.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

abstract public class Item extends CollectableAreaEntity{
    private boolean isCollected;
    Sprite sprite;
    public Item(Area area, Orientation orientation, DiscreteCoordinates position, String spriteName, float size){
        super(area, orientation, position);
        sprite = new Sprite(spriteName,size,size,this);
        area.registerActor(this);
        isCollected = false;
    }


    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void collect() {
        isCollected = true;
    }

    @Override
    public boolean isCollected() {
        return isCollected;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isCollected()) {
            sprite.draw(canvas);
        }
    }
}
