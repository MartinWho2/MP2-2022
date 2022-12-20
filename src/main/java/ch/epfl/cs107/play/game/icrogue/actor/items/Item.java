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

    /**
     * Init all useful attributes
     * @param area (Area): owner Area
     * @param orientation (Orientation): orientation of the skeleton
     * @param position (DiscreteCoordinates): position of the entity on the map
     * @param spriteName (String): file path
     * @param size (float): size of the image on the map
     */
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

    /**
     * unregister and manage how to deal with item using the ItemUseListener
     * @param handler (ItemUseListener): the handler pass by the player when he uses an item
     */
    public void collect(ItemUseListener handler) {
        super.collect();
        itemUseListener = handler;
    }

    /**
     * This function allow a user to use the item. It must be overridden for each item
     */
    public abstract void tryToUseItem();

    /**
     * Action that must be operated when the item is used. Must be overridden by each item
     * @param area (Area): owner area
     * @param orientation (Orientation): set his orientation
     * @param coords (DiscreteCoordinates): set coordinates on spawn in the room
     */
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
