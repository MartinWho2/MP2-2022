package ch.epfl.cs107.play.game.icrogue.actor;

import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.ICRogueBehavior;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Turret;
import ch.epfl.cs107.play.game.icrogue.actor.items.*;
import ch.epfl.cs107.play.game.icrogue.actor.projectiles.FireBall;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.game.icrogue.handler.ItemUseListener;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class ICRoguePlayer extends ICRogueActor implements Interactor {
    private float hp;
    private static final float HP_MAX = 10;
    /// Animation duration in frame number
    private final static int MOVE_DURATION = 8;
    private Sprite sprite;
    private TextGraphics message;
    private InteractionHandler handler;
    private boolean wantsInteraction;
    private boolean canShootFireBall = false;
    private boolean hasBomb = false;
    private boolean canPlaceBomb = false;
    private HashMap<Orientation,Sprite> orientationToSprite = new HashMap<>();
    private ArrayList<Integer> keysCollected = new ArrayList<>();
    private boolean isChangingRoom = false;
    private Connector currentConnector;
    private final static int TRIGGER_MOVE_MAX = 3;
    private int triggerMove;
    private Sprite[][] spritesMove;
    private Sprite[][] spritesStaff;
    private Animation[] currentAnimation;
    private  Animation[] animationsMove;
    private  Animation[] animationsStaff;
    private boolean staffAnimationOn;
    private float shootTimeDiff = 0;
    private float RELOAD_COOLDOWN = 0.8f;
    private ArrayList<Item> bombList;
    private ImageGraphics bombAttached;
    private TextGraphics nbrBombs;
    private ItemHandler itemHandler;
    private Inventory inventory;

    /**
     * Demo actor
     *
     */
    public ICRoguePlayer(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        super(owner, orientation, coordinates);
        this.hp = 1;
        wantsInteraction = false;

        message = new TextGraphics(Integer.toString((int) hp), 0.4f, Color.BLUE);
        bombList = new ArrayList<>();
        bombAttached = new ImageGraphics("images/sprites/other/bomb.png", 0.3f, 0.3f, new RegionOfInterest(0, 0, 16, 16));
        bombAttached.setParent(this);
        bombAttached.setAnchor(new Vector(0, 0));
        nbrBombs = new TextGraphics(Integer.toString( bombList.size()), 0.4f, Color.BLUE);
        nbrBombs.setParent(bombAttached);
        nbrBombs.setAnchor(new Vector(0, 0));

        message.setParent(this);
        message.setAnchor(new Vector(-0.3f, 0.1f));
        resetMotion();
        handler = new InteractionHandler();
        Orientation[] spriteOrientation = new Orientation[]{Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT};
        spritesMove = Sprite.extractSprites("zelda/player", 4, .75f, 1.5f, this, 16, 32, new Vector(.15f, -.15f), spriteOrientation);
        animationsMove = Animation.createAnimations(4, spritesMove);
        Orientation[] spriteOrientation2 = new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT};
        spritesStaff = Sprite.extractSprites("zelda/player.staff_water", 4, 1.5f, 1.5f, this, 32, 32, new Vector(-.25f, -.15f), spriteOrientation2);
        animationsStaff = Animation.createAnimations(4, spritesStaff, false);
        staffAnimationOn = false;
        currentAnimation = animationsMove;
        itemHandler = new ItemHandler();
        inventory = new Inventory();
    }

    @Override
    public void enterArea(Area area, DiscreteCoordinates position) {
        super.enterArea(area, position);
        ICRogueRoom room = (ICRogueRoom) getOwnerArea();
        room.playerEnters();
        area.registerActor(inventory);
    }

    @Override
    public void leaveArea() {
        super.leaveArea();
        getOwnerArea().unregisterActor(inventory);
    }

    /**
     * Center the camera on the player
     */
    public void centerCamera() {
        getOwnerArea().setViewCandidate(this);
    }
    public boolean getIsChangingRoom(){
        return isChangingRoom;
    }

    public void setChangingRoom(boolean state) {
        isChangingRoom = state;
    }

    @Override
    public void update(float deltaTime) {
        if (isDisplacementOccurs()) {
            animationsMove[getOrientation().ordinal()].update(deltaTime);
        }

        Keyboard keyboard= getOwnerArea().getKeyboard();

        moveIfPressed(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
        moveIfPressed(Orientation.UP, keyboard.get(Keyboard.UP));
        moveIfPressed(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
        moveIfPressed(Orientation.DOWN, keyboard.get(Keyboard.DOWN));

        Animation currentStaffAnimation = animationsStaff[getOrientation().ordinal()];
        if (staffAnimationOn) {
            currentStaffAnimation.update(deltaTime);
        }
        if (currentStaffAnimation.isCompleted()) {
            staffAnimationOn = false;
            currentAnimation = animationsMove;
        }

        shootTimeDiff = (shootTimeDiff < RELOAD_COOLDOWN) ? shootTimeDiff + deltaTime : shootTimeDiff;
        if (keyboard.get(Keyboard.X).isPressed()){

            if (shootTimeDiff >= RELOAD_COOLDOWN) {
                currentAnimation = animationsStaff;
                currentStaffAnimation.reset();
                shootTimeDiff = 0;
                staffAnimationOn = true;
                inventory.useCurrentItem();
            }
        }
        if ((keyboard.get(Keyboard.C).isPressed() || keyboard.get(Keyboard.C).isReleased()) && hasBomb){
            canPlaceBomb = !canPlaceBomb;
        }
        if (!hasBomb) canPlaceBomb = false;
        if (keyboard.get(Keyboard.W).isPressed() || keyboard.get(Keyboard.W).isReleased() || canPlaceBomb ){
            wantsInteraction = !wantsInteraction;
        }
        if ((keyboard.get(Keyboard.E).isPressed())) {
            inventory.changeItem(Inventory.HorizontalDirection.RIGHT);
        }


        super.update(deltaTime);

    }
    /**
     * Orientate and Move this player in the given orientation if the given button is down
     * Also changes the sprite depending on the orientation of the player
     * @param orientation (Orientation): given orientation, not null
     * @param b (Button): button corresponding to the given orientation, not null
     */
    private void moveIfPressed(Orientation orientation, ch.epfl.cs107.play.window.Button b){
        if(b.isDown()) {

            if (!isDisplacementOccurs()) {
                if (!orientation.equals(getOrientation())){
                    triggerMove = 0;
                }
                triggerMove++;
                orientate(orientation);
                sprite = orientationToSprite.get(orientation);
                if (triggerMove >= TRIGGER_MOVE_MAX){
                    move(MOVE_DURATION);

                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        currentAnimation[getOrientation().ordinal()].draw(canvas);
        message.draw(canvas);

        if (bombList.size() > 0){
            bombAttached.draw(canvas);
            nbrBombs.draw(canvas);
        }
    }

    public boolean isWeak() {
        return (hp <= 0.f);
    }

    public void strengthen() {
        hp = HP_MAX;
    }

    public void kill() {
        this.hp--;
    }

    ///Ghost implements Interactable

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }
    public DiscreteCoordinates getNewSpawnPosition(){
        return currentConnector.getDestinationCoord();
    }
    public String getNewRoomName(){
        return currentConnector.getDestinationRoom();
    }


    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }


    public boolean wantsCellInteraction() {
        return true;
    }



    public boolean wantsViewInteraction() {
        return wantsInteraction;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler , isCellInteraction);
    }
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }

    private class InteractionHandler implements ICRogueInteractionHandler {
        @Override
        public void interactWith(Staff staff, boolean isCellInteraction) {
            ICRogueRoom area = (ICRogueRoom) getOwnerArea();
            staff.collect(itemHandler);
            inventory.addItem(staff);
            canShootFireBall = true;

            area.tryToFinishRoom();
            }
        @Override
        public void interactWith(Cherry cherry, boolean isCellInteraction) {
            cherry.collect();
            ICRogueRoom area = (ICRogueRoom) getOwnerArea();
            area.tryToFinishRoom();
        }

        @Override
        public void interactWith(Key key, boolean isCellInteraction) {
            keysCollected.add(key.getKEY_ID());
            ICRogueRoom area = (ICRogueRoom) getOwnerArea();
            key.collect();
            area.tryToFinishRoom();
        }
        @Override
        public void interactWith(Connector connector, boolean isCellInteraction) {

            if (!isCellInteraction && keysCollected.contains(connector.getKEY_ID()) && connector.getState().equals(Connector.ConnectorType.LOCKED)){
                ICRogueRoom area = (ICRogueRoom)getOwnerArea();
                area.setConnectorOpen(connector);
            }else if (isCellInteraction && !isDisplacementOccurs()){
                currentConnector = connector;
                connector.setDestinationCoord(Connector.getSpawnPositionWithEnterCoordinates(getCurrentMainCellCoordinates()));
                isChangingRoom = true;
            }
        }

        @Override
        public void interactWith(Turret turret, boolean isCellInteraction) {
            if (isCellInteraction){
                turret.die();
                ICRogueRoom area = (ICRogueRoom) getOwnerArea();
                area.tryToFinishRoom();
            }
        }
        private void updateBombText(){
            nbrBombs.setText(Integer.toString(bombList.size()));
        }

        @Override
        public void interactWith(Bomb bomb, boolean isCellInteraction) {
            if (!bomb.isPlaced()) {
                bomb.collect(itemHandler);
                inventory.addItem(bomb);
                hasBomb = true;
                bombList.add(bomb);
                updateBombText();
                ICRogueRoom area = (ICRogueRoom) getOwnerArea();
                area.tryToFinishRoom();
            }
        }

        @Override
        public void interactWith(ICRogueBehavior.ICRogueCell cell, boolean isCellInteraction) {
            if (!isCellInteraction && cell.getType().equals(ICRogueBehavior.ICRogueCellType.WALL)) System.out.println("view interact");
            if (canPlaceBomb) {
                if (bombList.size() > 0) {
                    Bomb bomb = (Bomb) bombList.get(0);

                    if (!isCellInteraction && cell.getType().equals(ICRogueBehavior.ICRogueCellType.WALL) || cell.getType().equals(ICRogueBehavior.ICRogueCellType.HOLE)) {
                        bomb.placeBomb(getCurrentMainCellCoordinates(), getOwnerArea());
                        bombList.remove(bomb);
                        updateBombText();
                        canPlaceBomb = false;
                    } else if (!isCellInteraction){
                        bomb.placeBomb(getCurrentMainCellCoordinates().jump(getOrientation().toVector()), getOwnerArea());
                        bombList.remove(bomb);
                        updateBombText();
                        canPlaceBomb = false;
                    }
                }
            }
        }
    }

    private class ItemHandler implements ItemUseListener {
        @Override
        public void canUseItem(Bomb bomb) {
            bomb.useItem(getOwnerArea(),getOrientation(),getCurrentMainCellCoordinates());
            inventory.removeItem(bomb);
        }

        @Override
        public void canUseItem(Staff staff) {
            if (shootTimeDiff >= RELOAD_COOLDOWN) {
                shootTimeDiff = 0;
                Animation currentStaffAnimation = animationsStaff[getOrientation().ordinal()];
                DiscreteCoordinates fireBallSpawn = isDisplacementOccurs() ?
                        getCurrentMainCellCoordinates().jump(getOrientation().toVector()) :
                        getCurrentMainCellCoordinates();
                staff.useItem(getOwnerArea(), getOrientation(), fireBallSpawn);
            }
        }
    }
}
