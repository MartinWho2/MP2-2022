package ch.epfl.cs107.play.game.icrogue.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.ICRogueBehavior;
import ch.epfl.cs107.play.game.icrogue.actor.characters.Forgeron;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Turret;
import ch.epfl.cs107.play.game.icrogue.actor.items.*;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.game.icrogue.handler.ItemUseListener;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class ICRoguePlayer extends ICRogueActor implements Interactor {
    private float hp;
    private static final float HP_MAX = 5;
    /// Animation duration in frame number
    private final static int MOVE_DURATION = 8;
    private Sprite sprite;
    private Sprite[] healthBarSprites;
    private InteractionHandler handler;
    private boolean wantsInteractionInFront;
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
    private final Animation[] animationsMove;
    private final Animation[] animationsStaff;
    private final Animation[] animationsSword;
    private Animation currentStaffAnimation;
    private Animation currentSwordAnimation;
    private boolean swordAnimationOn;
    private boolean staffAnimationOn;
    private float shootTimeDiff = 0;
    private final static float RELOAD_COOLDOWN = 0.8f;
    private final ItemHandler itemHandler;
    private final Inventory inventory;
    private boolean isInvicible;
    private final static float INVINCIBILITY_TIME = 1f;
    private float immunityTimer;
    private Orientation orientationAiming;
    private Orientation currentOrientation;
    private boolean isAiming;

    /**
     * Constructor of the Player, initialize all animations, and useful attribut
     * @param owner (Area): not null
     * @param orientation (Orientation): not null
     * @param coordinates (DiscreteCoordinates): not null
     */
    public ICRoguePlayer(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        super(owner, orientation, coordinates);
        this.hp = HP_MAX;
        wantsInteractionInFront = false;

        resetMotion();
        handler = new InteractionHandler();
        healthBarSprites = Sprite.extractSprites("other/health_bar", 5, 1.f, .1f, this, new Vector(0, -.1f), 16, 5);
        animationsMove = PlayerAnimations.MOVE.createAnimations(this);
        animationsStaff = PlayerAnimations.SHOOT.createAnimations(this);
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

    /**
     * apply the given amount of damage to the actor, if the actor is damaged below 0HP,
     * he is killed
     * @param damages (float): amount of damages, not null
     */
    public void damage(float damages) {
        if (!isInvicible){
            this.hp = this.hp >= damages ? this.hp - damages : 0;
            isInvicible = true;
        }
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
        currentOrientation = getOrientation();
        // Set the orientation of the animation if the player aim with the aiming arrows
        if (orientationAiming != null){
            currentOrientation = orientationAiming;
        }
        // Update moving animation if there is displacement
        if (isDisplacementOccurs()) {
            animationsMove[getOrientation().ordinal()].update(deltaTime);
        }

        // Manage all displacement
        Keyboard keyboard = getOwnerArea().getKeyboard();
        displacementManagement();
        // Compute aiming arrows direction
        orientateAiming();


        Animation currentStaffAnimation = animationsStaff[currentOrientation.ordinal()];
        Animation currentSwordAnimation = animationsSword[currentOrientation.ordinal()];
        if (staffAnimationOn) {
            currentStaffAnimation.update(deltaTime);
        }
        if (swordAnimationOn) {
            currentSwordAnimation.update(deltaTime);
        }
        if (currentSwordAnimation.isCompleted()){
            swordAnimationOn = false;
            currentAnimation = animationsMove;
            orientationAiming = null;
            currentSwordAnimation.reset();
        }
        if (currentStaffAnimation.isCompleted()) {
            staffAnimationOn = false;
            currentAnimation = animationsMove;
            orientationAiming = null;
            currentStaffAnimation.reset();
        }

        shootTimeDiff += (shootTimeDiff < RELOAD_COOLDOWN) ? deltaTime : 0;
        if (keyboard.get(Keyboard.X).isPressed()){
            inventory.useCurrentItem();
        }
        if (keyboard.get(Keyboard.F).isPressed() || keyboard.get(Keyboard.F).isReleased()){
            wantsInteractionInFront = !wantsInteractionInFront;
        }
        if ((keyboard.get(Keyboard.E).isPressed())) {
            inventory.changeItem(Inventory.HorizontalDirection.RIGHT);
        }

        if (keyboard.get(Keyboard.Q).isPressed()){
            inventory.changeItem(Inventory.HorizontalDirection.LEFT);
        }

        // Manage invincibility when the user is damaged
        if (isInvicible){
            immunityTimer += deltaTime;
            if ( immunityTimer >= INVINCIBILITY_TIME){
                immunityTimer = 0;
                isInvicible = false;
            }
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

    /**
     * Manage all displacement of the player
     */
    private void displacementManagement() {
        Keyboard keyboard = getOwnerArea().getKeyboard();
        // Check which keys are pressed and move in consequences
        moveIfPressed(Orientation.LEFT, keyboard.get(Keyboard.A));
        moveIfPressed(Orientation.UP, keyboard.get(Keyboard.W));
        moveIfPressed(Orientation.RIGHT, keyboard.get(Keyboard.D));
        moveIfPressed(Orientation.DOWN, keyboard.get(Keyboard.S));
    }

    /**
     * This function compute in which direction the player is aiming with the arrow keys
     */
    private void orientateAiming(){
        Keyboard keyboard = getOwnerArea().getKeyboard();
        if (keyboard.get(Keyboard.LEFT).isPressed()){
            useItemWithDirection(Orientation.LEFT);
        }else if (keyboard.get(Keyboard.RIGHT).isPressed()){
            useItemWithDirection(Orientation.RIGHT);
        }else if (keyboard.get(Keyboard.UP).isPressed()){
            useItemWithDirection(Orientation.UP);
        }else if (keyboard.get(Keyboard.DOWN).isPressed()){
            useItemWithDirection(Orientation.DOWN);
        }
    }

    /**
     * Use the item with an arrow key instead of using the X key
     * @param orientation (Orientation): Use direction, not null
     */
    private void useItemWithDirection(Orientation orientation){
        setOrientationAiming(orientation);
        if (!inventory.useCurrentItem()){
            setOrientationAiming(null);
        }
    }

    /**
     * Set the current orientation to the direction in which to use the item
     * @param orientation (Orientation): Use orientation, not null
     */
    public void setOrientationAiming(Orientation orientation){
        orientationAiming = orientation;
        if (orientation != null){
            currentOrientation = orientation;
        }else{
            currentOrientation = getOrientation();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        // Blink if player has immunity
        if (!isInvicible || (((immunityTimer * 5)%2) >= 1)){
            if (orientationAiming != null)
                currentOrientation = orientationAiming;
            currentAnimation[currentOrientation.ordinal()].draw(canvas);
        }
        // Display the right health bar
        if ((int)hp > 0) {
            healthBarSprites[(int)hp - 1].draw(canvas);
        }
    }


    public boolean isWeak() {
        return (hp <= 0.f);
    }

    public void kill() {
        this.hp = 0;
    }

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

    /**
     * @return (String): The name of the destination name
     */
    public String getNewRoomName(){
        return currentConnector.getDestinationRoom();
    }

    /**
     * @return (List<DiscreteCoordinates>): A List containing the cell in front of the player
     */
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    public boolean wantsCellInteraction() {
        return true;
    }

    public boolean wantsViewInteraction() {
        // The return is always true because we need to know every frame what kind of tile is in front of the player
        return true;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler , isCellInteraction);
    }

    /**
     * Classical acceptInteraction method as given in the pdf
     * @param v (AreaInteractionVisitor) : the visitor
     * @param isCellInteraction (Specify whether it's a cell interaction)
     */
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

    /**
     * Enum class used to clean up the constructor.
     * This class is used to create the animations of movements, staff and sword of the player.
     */
    public enum PlayerAnimations{
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
        private int[] imageSizes;
        Animation[] animations;

        /**
         * This constructor initialize the class and animations for all types
         * @param priority (Priority): Represents the priority of the action, not null
         * @param spriteName (String): path to the file containing the sprite, not null
         * @param nbFrames (int): number of different animations, not null
         * @param frameDuration (int): number of frame per animations, not null
         * @param repeat (boolean): whether the animation should be running infinitely, not null
         * @param offset (Vector): Specify manual offset, not null
         * @param orientations (Orientation): Array that specify in which order are given the animations, not null
         * @param imageSizes (int): width and height of the image in an array, not null
         */
        PlayerAnimations(Priority priority, String spriteName, int nbFrames, int frameDuration, boolean repeat,
                         Vector offset, Orientation[] orientations,int[] imageSizes){
            this.priority = priority;
            this.spriteName = spriteName;
            this.nbFrames = nbFrames;
            this.frameDuration = frameDuration;
            this.repeat = repeat;
            this.offset = offset;
            this.orientations = orientations;
            this.imageSizes = imageSizes;
        }

        /**
         * This method is used to create an animation for the given type of the enum which is then returned
         * @param player (ICRoguePlayer): The ICRoguePlayer
         * @return (Animation): An array of the 4 animations (for the 4 directions)
         */
        public Animation[] createAnimations(ICRoguePlayer player){
            int maxSize = Math.max(imageSizes[0], imageSizes[1]);
            float[] newSize = new float[]{((float) imageSizes[0]/maxSize)*1.5f,((float) imageSizes[1]/maxSize)*1.5f};
            Sprite[][] sprites = Sprite.extractSprites(spriteName,nbFrames,newSize[0],newSize[1],player, imageSizes[0],
                    imageSizes[1], offset, orientations );
            animations = Animation.createAnimations(frameDuration,sprites, repeat);
            return animations;
        }
    }

    private class InteractionHandler implements ICRogueInteractionHandler {
        /**
         * Collects the staff if the player wants the front interaction and is standing in front of the staff
         * @param staff (Staff): not null
         * @param isCellInteraction (boolean): If the cell is an interaction
         */
        @Override
        public void interactWith(Staff staff, boolean isCellInteraction) {
            if (wantsInteractionInFront && !isCellInteraction){
                ICRogueRoom area = (ICRogueRoom) getOwnerArea();
                staff.collect(itemHandler);
                inventory.addItem(staff);
                area.tryToFinishRoom();
            }
        }

        /**
         * Collects the cherry if the player stands on it
         * @param cherry (Cherry): not null
         * @param isCellInteraction (boolean): If the cell is an interaction
         */
        @Override
        public void interactWith(Cherry cherry, boolean isCellInteraction) {
            if (isCellInteraction){
                inventory.addItem(cherry);
                cherry.collect(itemHandler);
                ICRogueRoom area = (ICRogueRoom) getOwnerArea();
                area.tryToFinishRoom();
            }
        }

        /**
         *  If the player is on the key, it gets collected and adds the key to a list
         * @param key (Key): not null
         * @param isCellInteraction (boolean): If the cell is an interaction
         */
        @Override
        public void interactWith(Key key, boolean isCellInteraction) {
            if (isCellInteraction){
                keysCollected.add(key.getKEY_ID());
                ICRogueRoom area = (ICRogueRoom) getOwnerArea();
                key.collect();
                area.tryToFinishRoom();
            }
        }

        /**
         * If the player has the correct key and uses it in front of the connector, it opens it
         * Otherwise, if the player is in the connector (which means that it was open), he get transferred in the
         * corresponding room
         * @param connector (Connector): not null
         * @param isCellInteraction (boolean): If the cell is an interaction
         */
        @Override
        public void interactWith(Connector connector, boolean isCellInteraction) {

            if (!isCellInteraction && keysCollected.contains(connector.getKEY_ID()) && connector.getState().equals(Connector.ConnectorType.LOCKED) && wantsInteractionInFront){
                ICRogueRoom area = (ICRogueRoom)getOwnerArea();
                area.setConnectorOpen(connector);
            }else if (isCellInteraction && !isDisplacementOccurs()){
                currentConnector = connector;
                connector.setDestinationCoord(Connector.getSpawnPositionWithEnterCoordinates(getCurrentMainCellCoordinates()));
                isChangingRoom = true;
            }
        }

        /**
         * If the player is walking on the turret, the turret will die
         * @param turret (Turret): not null
         * @param isCellInteraction (boolean): If the cell is an interaction
         */
        @Override
        public void interactWith(Turret turret, boolean isCellInteraction) {
            if (isCellInteraction){
                turret.die();
                ICRogueRoom area = (ICRogueRoom) getOwnerArea();
                area.tryToFinishRoom();
            }
        }

        /**
         * If the bomb is not collected, add it to the inventory
         * @param bomb (Bomb): not null
         * @param isCellInteraction (boolean): If the cell is an interaction
         */
        @Override
        public void interactWith(Bomb bomb, boolean isCellInteraction) {
            if (!bomb.isCollected()) {
                bomb.collect(itemHandler);
                inventory.addItem(bomb);
                ICRogueRoom area = (ICRogueRoom) getOwnerArea();
                area.tryToFinishRoom();
            }
        }

        /**
         * If the sword is not collected yet, collect it and add it to the inventory
         * @param sword (Sword): not null
         * @param isCellInteraction (boolean): If the cell is an interaction
         */
        @Override
        public void interactWith(Sword sword, boolean isCellInteraction) {
            if (!sword.isCollected()){
                sword.collect(itemHandler);
                inventory.addItem(sword);
                ((ICRogueRoom)getOwnerArea()).tryToFinishRoom();
            }
        }

        /**
         * Used to get what type of cell is in front of the player at any time
         * @param cell (Cell): not null
         * @param isCellInteraction (boolean): If the cell is an interaction
         */
        @Override
        public void interactWith(ICRogueBehavior.ICRogueCell cell, boolean isCellInteraction) {
            if (!isCellInteraction ){
                cellInFront = cell.getType();
            }
        }

        /**
         * If the player interacts with the forgeron, the forgeron displays the next text
         * @param forgeron (Forgeron): not null
         * @param isCellInteraction (boolean): If the cell is an interaction
         */
        @Override
        public void interactWith(Forgeron forgeron, boolean isCellInteraction) {
            if (wantsInteractionInFront) {
                forgeron.updateText();
                ICRogueRoom area = (ICRogueRoom) getOwnerArea();
                area.tryToFinishRoom();
            }
        }
    }
    // This class is used the same way the ICRogueHandler works
    private class ItemHandler implements ItemUseListener {
        /**
         * Tries to pose the bomb either on the cell in front of the player or directly its cells
         * @param bomb (Bomb): not null
         */
        @Override
        public void canUseItem(Bomb bomb) {
            if (cellInFront.equals(ICRogueBehavior.ICRogueCellType.WALL) || cellInFront.equals(ICRogueBehavior.ICRogueCellType.HOLE)){
                bomb.useItem(getOwnerArea(),currentOrientation,getCurrentMainCellCoordinates());
            }else{
                bomb.useItem(getOwnerArea(),currentOrientation,getCurrentMainCellCoordinates().jump(currentOrientation.toVector()));
            }
            inventory.removeItem(bomb);
            setOrientationAiming(null);
        }

        /**
         * If the cooldown for the staff is over, uses the staff to shoot a new fireball
         * @param staff (Staff): not null
         */
        @Override
        public void canUseItem(Staff staff) {
            if (shootTimeDiff >= RELOAD_COOLDOWN) {
                shootTimeDiff = 0;
                currentAnimation = animationsStaff;
                currentStaffAnimation = animationsStaff[currentOrientation.ordinal()];
                currentStaffAnimation.reset();
                staffAnimationOn = true;
                DiscreteCoordinates fireBallSpawn = isDisplacementOccurs() ?
                        getCurrentMainCellCoordinates().jump(getOrientation().toVector()) :
                        getCurrentMainCellCoordinates();
                staff.useItem(getOwnerArea(), currentOrientation, fireBallSpawn);
            }
        }

        /**
         * If the player is not using the sword or the staff already, uses the useItem method of the sword and
         * begins the animation
         * @param sword (Sword): not null
         */
        @Override
        public void canUseItem(Sword sword) {
            if (!staffAnimationOn && !swordAnimationOn){
                currentAnimation = animationsSword;
                currentSwordAnimation = currentAnimation[currentOrientation.ordinal()];
                currentSwordAnimation.reset();
                swordAnimationOn = true;
                sword.useItem(getOwnerArea(), currentOrientation, getCurrentMainCellCoordinates());
            }
        }

        @Override
        public void canUseItem(Cherry cherry) {
            if (hp < HP_MAX){
                hp++;
            }
            inventory.removeItem(cherry);
            orientateAiming();
        }
    }
}
