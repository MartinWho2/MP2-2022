package ch.epfl.cs107.play.game.icrogue.actor.items;


import ch.epfl.cs107.play.game.actor.Entity;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class Inventory extends Entity {
    private Item[] inventory;
    private ImageGraphics[] itemImages;
    private int currentItem;
    private Sprite sprite;
    private Animation selectorAnimations;
    private Sprite[] selectorSprites;
    private final static float[] inventorySizeTiles = new float[]{2.6f,0.8f};
    private final static int INVENTORY_SIZE = 4;
    public Inventory(){
        super(new Vector(2f-(inventorySizeTiles[0]/2),0f));
        inventory = new Item[INVENTORY_SIZE];
        currentItem = 0;
        sprite = new Sprite("other/inventory",inventorySizeTiles[0],inventorySizeTiles[1],this);
        itemImages = new ImageGraphics[INVENTORY_SIZE];
        selectorSprites = Sprite.extractSprites("zelda/selector",4,0.8f,0.8f,this,32,32);
        selectorAnimations = new Animation(4,selectorSprites);
        selectorAnimations.setAnchor(new Vector(0,0));
    }
    private void updateSprite(){
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            if (inventory[i] != null) {
                itemImages[i] = new ImageGraphics(inventory[i].getTitle(), 0.4f, 0.4f,
                        new RegionOfInterest(0, 0, 67, 20), true);
                itemImages[i].setParent(this);
                itemImages[i].setAnchor(new Vector(inventorySizeTiles[0] / 4.45f * i + inventorySizeTiles[0] / 12,
                        0.17f));
            }else{
                itemImages[i] = null;
            }

        }
    }
    private int findFirstEmptySlot(){
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            if (inventory[i] == null){
                return i;
            }
        }
        return -1;
    }
    public void addItem(Item item){
        int slot = findFirstEmptySlot();
        if (slot == -1){
            return;
        }
        inventory[slot] = item;
        updateSprite();
    }
    public void removeItem(Item item){
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            if (item == inventory[i]){
                inventory[i] = null;
                updateSprite();
            }
        }
    }
    public void useCurrentItem(){
        if (inventory[currentItem] == null){
            return;
        }
        inventory[currentItem].tryToUseItem();
    }
    public void changeItem(HorizontalDirection a){
        currentItem =  (currentItem + a.getType()) % INVENTORY_SIZE;
        currentItem += currentItem < 0 ? 4 : 0;
        System.out.println(currentItem);
        selectorAnimations.setAnchor(new Vector(inventorySizeTiles[0]/4.45f *currentItem,
                0));
    }


    @Override
    public void update(float deltaTime) {
        selectorAnimations.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
        for (ImageGraphics image: itemImages){
            if (image != null){
                image.draw(canvas);
            }
        }
        selectorAnimations.draw(canvas);
    }
    public enum HorizontalDirection {
        LEFT(-1),
        RIGHT(1);
        private int type;
        HorizontalDirection(int i) {
            this.type = i;
        }

        public int getType() {
            return type;
        }
    }
}
