package ch.epfl.cs107.play.game.icrogue.actor.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icrogue.handler.ItemUseListener;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

abstract public class Item extends CollectableAreaEntity{
    protected Sprite sprite;
    private String spriteName;
    private ItemUseListener itemUseListener;
    public Item(Area area, Orientation orientation, DiscreteCoordinates position, String spriteName, float size){
        super(area, orientation, position);
        sprite = new Sprite(spriteName,size,size,this);
        this.spriteName = spriteName;
        area.registerActor(this);
    }

    public ItemUseListener getItemUseListener() {
        return itemUseListener;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }


    public void collect(ItemUseListener handler) {
        super.collect();
        itemUseListener = handler;
    }
    public abstract void tryToUseItem();
    public abstract void useItem(Area area, Orientation orientation, DiscreteCoordinates coords);
    public String getTitle(){
        return "images/sprites/"+spriteName+".png";
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
    public void draw(Canvas canvas) {
        if (!isCollected()) {
            sprite.draw(canvas);
        }
    }
}
