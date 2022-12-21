package ch.epfl.cs107.play.game.icrogue.actor.enemies;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.projectiles.FireBallDarkLord;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.io.XMLTexts;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.beans.XMLDecoder;

public class DarkLord extends Enemy{
    private final Animation[] animationsMove;
    private final Sprite[][] spritesMove;
    private final Orientation roomOrientation;
    private final static int COOLDOWN_SHOOT = 2;
    private final int MOVEMENT_FRAMES = 24;
    private float lastShotTime;
    private float hp;
    static final float MAX_HP = 7;
    private boolean tookRecentlyDamage = false;
    private float blinkTimer;
    private final static float BLINK_TIME = 0.8f;

    /**
     * Init all usefull elements of the class
     * @param area (Area): owner Area
     * @param orientation (Orientation): orientation of the character
     * @param position (DiscreteCoordinates): spawn coordinates in the room
     */
    public DarkLord(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, 1f);
        roomOrientation = orientation;

        Orientation[] orientations = new Orientation[]{Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT};
        // animations = Sprite.extractSprites("zelda/darkLord",3,1.5f,1.5f,this,32,32,orientations);
        spritesMove = Sprite.extractSprites("zelda/darkLord", 3, 1.5f, 1.5f, this,
                32, 32, new Vector(.15f, 0.3f), orientations);
        animationsMove = Animation.createAnimations(4, spritesMove);
        lastShotTime = 0.f;
        hp = MAX_HP;
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    /**
     * Damage the boss
     * @param damages (float): amount of damages
     */
    public void damage(float damages) {
        System.out.println("les hp sont " + hp);
        if (hp - damages <= 0) {
            // If the boss is dead, the challenge is succeeded
            ((ICRogueRoom)(getOwnerArea())).tryToFinishRoom();
            die();

        } else {
            tookRecentlyDamage = true;
            hp -= damages;
        }
    }

    @Override
    public void enterArea(Area area, DiscreteCoordinates position) {
        super.enterArea(area, position);
        speak(XMLTexts.getText("text-boss-spawn"), false);
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // Draws the dark lord
        // If he was recently touched he will blink
        if (!tookRecentlyDamage || ((blinkTimer*5)%2) >= 1)
            animationsMove[roomOrientation.ordinal()].draw(canvas);
    }

    /**
     * Determine possible axis of displacement of the boss depending on his roomOrientation
     * @return (Orientation): array of Orientation containing his possible displacement options
     */
    private Orientation[] chooseNextMove(){
        if (roomOrientation.equals(Orientation.DOWN) || roomOrientation.equals(Orientation.UP)){
            return new Orientation[]{Orientation.LEFT,Orientation.RIGHT};
        }
        return new Orientation[]{Orientation.DOWN,Orientation.UP};
    }

    /**
     * Creates a new FireBallDarkLord
     */
    private void summonFlameSkull(){
        Orientation[] flameSkullVectorsOrientation = chooseNextMove();
        for (Orientation orientation: flameSkullVectorsOrientation) {
            new FireBallDarkLord(getOwnerArea(),roomOrientation,getCurrentMainCellCoordinates().jump(orientation.toVector()));
        }
    }

    /**
     * Compute probability to choose his orientation of displacement, this function makes it more likely
     * to the boss to go to the middle than to the side where he stands
     * @return (float): probability to move in a direction
     */
    private float getProbaOfMove(){
        if (roomOrientation.equals(Orientation.UP) || roomOrientation.equals(Orientation.DOWN)){
            return (float)(0.1 * (4.5-getCurrentMainCellCoordinates().x));
        }
        return (float)(0.1 * (4.5-getCurrentMainCellCoordinates().y));
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // Makes the dark lord blink if he was touched
        if (tookRecentlyDamage){
            blinkTimer += deltaTime;
            if (blinkTimer > BLINK_TIME){
                blinkTimer = 0;
                tookRecentlyDamage = false;
            }
        }
        // Cooldown to avoid flameSkull spamming
        lastShotTime += deltaTime;
        if (lastShotTime > COOLDOWN_SHOOT){
            summonFlameSkull();
            lastShotTime = 0;
        }
        // Computation to get the movement of the boss
        if (isDisplacementOccurs()) {
            animationsMove[roomOrientation.ordinal()].update(deltaTime);
        } else {
            double randomMove = 0.5 + getProbaOfMove();
            if (Math.random()>randomMove){
                orientate(chooseNextMove()[0]);
            }else{
                orientate(chooseNextMove()[1]);
            }
            move(MOVEMENT_FRAMES);
        }
    }
}
