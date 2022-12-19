package ch.epfl.cs107.play.game.icrogue.actor;

import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.ICRogueBehavior;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Turret;
import ch.epfl.cs107.play.game.icrogue.actor.items.*;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.game.icrogue.handler.ItemUseListener;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
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
    private boolean wantsKeyInteraction;
    private HashMap<Orientation,Sprite> orientationToSprite = new HashMap<>();
    private ArrayList<Integer> keysCollected = new ArrayList<>();
    private boolean isChangingRoom = false;
    private Connector currentConnector;
    private final static int TRIGGER_MOVE_MAX = 3;
    private int triggerMove;
    private ICRogueBehavior.ICRogueCellType cellInFront;
    private Sprite[][] spritesMove;
    private Sprite[][] spritesStaff;
    private Sprite[][] spritesSword;
    private Animation[] currentAnimation;
    private  Animation[] animationsMove;
    private  Animation[] animationsStaff;
    private  Animation[] animationsSword;
    private Animation currentStaffAnimation;
    private Animation currentSwordAnimation;
    private boolean swordAnimationOn;
    private boolean staffAnimationOn;
    private float shootTimeDiff = 0;
    private float RELOAD_COOLDOWN = 0.8f;
    private ItemHandler itemHandler;
    private Inventory inventory;
    private Orientation orientationAiming;

    /**
     * Demo actor
     *
     */
    public ICRoguePlayer(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        super(owner, orientation, coordinates);
        this.hp = HP_MAX;
        wantsKeyInteraction = false;

        message = new TextGraphics(Integer.toString((int) hp), 0.4f, Color.BLUE);

        message.setParent(this);
        message.setAnchor(new Vector(-0.3f, 0.1f));
        resetMotion();
        handler = new InteractionHandler();
        //Orientation[] spriteOrientation = new Orientation[]{Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT};
        //spritesMove = Sprite.extractSprites("zelda/player", 4, .75f, 1.5f, this, 16, 32, new Vector(.15f, -.15f), spriteOrientation);
        //animationsMove = Animation.createAnimations(4, spritesMove);
        animationsMove = PlayerAnimations.MOVE.createAnimations(this);
        //Orientation[] spriteOrientation2 = new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT};
        //spritesStaff = Sprite.extractSprites("zelda/player.staff_water", 4, 1.5f, 1.5f, this, 32, 32, new Vector(-.25f, -.15f), spriteOrientation2);
        //animationsStaff = Animation.createAnimations(4, spritesStaff, false);
        animationsStaff = PlayerAnimations.SHOOT.createAnimations(this);
        //Orientation[] spriteOrientation3 = new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT};
        //spritesSword = Sprite.extractSprites("zelda/player.sword", 4, 1.5f, 1.5f, this, 32, 32, new Vector(-.25f, -.15f), spriteOrientation3);
        //animationsSword = Animation.createAnimations(4, spritesSword, false);
        animationsSword = PlayerAnimations.SWORD.createAnimations(this);
        staffAnimationOn = false;
        swordAnimationOn = false;
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

    public void damage(int damages) {
        this.hp = this.hp >= damages ? this.hp - damages : 0;
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
        message.setText(hp + "");
        if (isDisplacementOccurs()) {
            animationsMove[getOrientation().ordinal()].update(deltaTime);
        }

        Keyboard keyboard= getOwnerArea().getKeyboard();

        moveIfPressed(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
        moveIfPressed(Orientation.UP, keyboard.get(Keyboard.UP));
        moveIfPressed(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
        moveIfPressed(Orientation.DOWN, keyboard.get(Keyboard.DOWN));

        Animation currentStaffAnimation = animationsStaff[getOrientation().ordinal()];
        Animation currentSwordAnimation = animationsSword[getOrientation().ordinal()];
        if (staffAnimationOn) {
            currentStaffAnimation.update(deltaTime);
        }
        if (swordAnimationOn) {
            currentAnimation[getOrientation().ordinal()].update(deltaTime);
        }
        if (currentSwordAnimation.isCompleted()){
            swordAnimationOn = false;
            currentAnimation = animationsMove;
        }
        if (currentStaffAnimation.isCompleted()) {
            staffAnimationOn = false;
            currentAnimation = animationsMove;
        }

        shootTimeDiff += (shootTimeDiff < RELOAD_COOLDOWN) ? deltaTime : 0;
        if (keyboard.get(Keyboard.X).isPressed()){
            inventory.useCurrentItem();
        }
        if (keyboard.get(Keyboard.W).isPressed() || keyboard.get(Keyboard.W).isReleased()){
            wantsKeyInteraction = !wantsKeyInteraction;
        }
        if ((keyboard.get(Keyboard.E).isPressed())) {
            inventory.changeItem(Inventory.HorizontalDirection.RIGHT);
        }

        if (keyboard.get(Keyboard.Q).isPressed()){
            inventory.changeItem(Inventory.HorizontalDirection.LEFT);
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

    }

    public boolean isWeak() {
        return (hp <= 0.f);
    }

    public void strengthen() {
        hp = HP_MAX;
    }

    public void kill() {
        this.hp = 0;
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
        return true;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler , isCellInteraction);
    }
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }
    public enum Priority{
        VERY_LOW(1),
        LOW(2),
        MEDIUM(3),
        HIGH(4),
        VERY_HIGH(5);
        private final int priority;
        Priority(int priority){
            this.priority = priority;
        }
        public int getPriority() {
            return priority;
        }
    }
    public enum PlayerAnimations{
        WAIT(Priority.VERY_LOW,"zelda/player",1,5,true,new Vector(.15f, -.15f),
                new Orientation[]{Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT},new int[]{16,32}),
        MOVE(Priority.MEDIUM,"zelda/player",4,4,true,new Vector(.15f, -.15f),
                new Orientation[]{Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT},new int[]{16,32}),
        SHOOT(Priority.HIGH,"zelda/player.staff_water",4,4,false,
                new Vector(-.25f, -.15f),
                new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT},new int[]{32,32}),
        SWORD(Priority.HIGH,"zelda/player.sword",4,4,false,new Vector(-.25f, -.15f),
                new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT},new int[]{32,32});
        private Priority priority;
        private String spriteName;
        private int nbFrames;
        private int frameDuration;
        private boolean repeat;
        private Vector offset;
        private Orientation[] orientations;
        private int[] imageSize;
        Animation[] animations;
        PlayerAnimations(Priority priority, String spriteName, int nbFrames, int frameDuration, boolean repeat,
                         Vector offset, Orientation[] orientations,int[] imageSize){
            this.priority = priority;
            this.spriteName = spriteName;
            this.nbFrames = nbFrames;
            this.frameDuration = frameDuration;
            this.repeat = repeat;
            this.offset = offset;
            this.orientations = orientations;
            this.imageSize = imageSize;
        }
        public Animation[] createAnimations(ICRoguePlayer player){
            int maxSize = Math.max(imageSize[0],imageSize[1]);
            float[] newSize = new float[]{((float)imageSize[0]/maxSize)*1.5f,((float)imageSize[1]/maxSize)*1.5f};
            Sprite[][] sprites = Sprite.extractSprites(spriteName,nbFrames,newSize[0],newSize[1],player,imageSize[0],
                    imageSize[1], offset, orientations );
            animations = Animation.createAnimations(frameDuration,sprites, repeat);
            return animations;
        }
    }

    private class InteractionHandler implements ICRogueInteractionHandler {
        @Override
        public void interactWith(Staff staff, boolean isCellInteraction) {
            if (wantsKeyInteraction){
                ICRogueRoom area = (ICRogueRoom) getOwnerArea();
                staff.collect(itemHandler);
                inventory.addItem(staff);
                area.tryToFinishRoom();
            }
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

            if (!isCellInteraction && keysCollected.contains(connector.getKEY_ID()) && connector.getState().equals(Connector.ConnectorType.LOCKED) && wantsKeyInteraction){
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

        @Override
        public void interactWith(Bomb bomb, boolean isCellInteraction) {
            if (!bomb.isPlaced()) {
                bomb.collect(itemHandler);
                inventory.addItem(bomb);
                ICRogueRoom area = (ICRogueRoom) getOwnerArea();
                area.tryToFinishRoom();
            }
        }

        @Override
        public void interactWith(Sword sword, boolean isCellInteraction) {
            sword.collect(itemHandler);
            inventory.addItem(sword);
            ((ICRogueRoom)getOwnerArea()).tryToFinishRoom();
        }

        @Override
        public void interactWith(ICRogueBehavior.ICRogueCell cell, boolean isCellInteraction) {
            if (!isCellInteraction ){
                cellInFront = cell.getType();
            }
        }

    }

    private class ItemHandler implements ItemUseListener {
        @Override
        public void canUseItem(Bomb bomb) {
            if (cellInFront.equals(ICRogueBehavior.ICRogueCellType.WALL) || cellInFront.equals(ICRogueBehavior.ICRogueCellType.HOLE)){
                bomb.useItem(getOwnerArea(),getOrientation(),getCurrentMainCellCoordinates());
            }else{
                bomb.useItem(getOwnerArea(),getOrientation(),getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
            }
            inventory.removeItem(bomb);
        }

        @Override
        public void canUseItem(Staff staff) {
            if (shootTimeDiff >= RELOAD_COOLDOWN) {
                shootTimeDiff = 0;
                currentAnimation = animationsStaff;
                currentStaffAnimation = animationsStaff[getOrientation().ordinal()];
                currentStaffAnimation.reset();
                staffAnimationOn = true;
                DiscreteCoordinates fireBallSpawn = isDisplacementOccurs() ?
                        getCurrentMainCellCoordinates().jump(getOrientation().toVector()) :
                        getCurrentMainCellCoordinates();
                staff.useItem(getOwnerArea(), getOrientation(), fireBallSpawn);
            }
        }

        @Override
        public void canUseItem(Sword sword) {
            if (!staffAnimationOn & !swordAnimationOn){
                currentAnimation = animationsSword;
                currentSwordAnimation = currentAnimation[getOrientation().ordinal()];
                currentSwordAnimation.reset();
                swordAnimationOn = true;
                sword.useItem(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates());
            }

        }
    }
}
