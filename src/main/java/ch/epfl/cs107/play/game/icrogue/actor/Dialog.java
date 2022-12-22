package ch.epfl.cs107.play.game.icrogue.actor;

import ch.epfl.cs107.play.game.actor.Entity;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.TextAlign;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.*;


public class Dialog extends Entity {
    private final TextGraphics dialog;
    private final String text;
    private final boolean instantDisplay;
    private boolean finishedDisplaying;
    private boolean needsToBeRemoved;
    private float cooldownBeforeDisappearing;
    private float cooldown;
    private int characterCounter = 0;
    private SpeakerActor parent;

    /**
     * Init useful class attributs
     * @param parent (SpeakerActor): The owner of the text
     * @param position (DiscreteCoordinates): position of the text on the map (useful for the super init)
     * @param text (String): text to display
     * @param instantDisplay (boolean): Whether the text should be displayed all at once or letter by letter
     */
    public Dialog(SpeakerActor parent, DiscreteCoordinates position, String text, boolean instantDisplay) {
        super(position.toVector());
        this.parent = parent;
        this.text = text;
        dialog = new TextGraphics("", 0.4f, Color.WHITE,Color.BLACK,0f,
                false,false, new Vector(0.5f,1), TextAlign.Horizontal.CENTER, TextAlign.Vertical.BOTTOM,1,1);
        dialog.setParent(parent);
        // The default cooldown is 2 seconds
        cooldownBeforeDisappearing = 2;
        // Otherwise it depends on the length of the text
        if (instantDisplay) {
            dialog.setText(text);
            cooldownBeforeDisappearing = 1 + text.length() / 25f;
        }
        this.instantDisplay = instantDisplay;
        this.finishedDisplaying = instantDisplay;
        this.needsToBeRemoved = false;
        cooldown= 0;
    }

    @Override
    public void update(float deltaTime) {
        // display letter by letter
        if (!instantDisplay && !finishedDisplaying){
            // If the substring is not complete, then add a character
            if (characterCounter < text.length()){
                dialog.setText(text.substring(0,++characterCounter));
            }// Otherwise dialog is finished
            else{
                finishedDisplaying = true;
            }
        }
        // After the dialog is finished, a countdown is set to remove the text from being displayed
        if (finishedDisplaying){
            cooldown += deltaTime;
            if (cooldown >= cooldownBeforeDisappearing){
                needsToBeRemoved = true;
            }
        }
    }
    public boolean isNeedsToBeRemoved() {
        return needsToBeRemoved;
    }

    public boolean isFinished(){
        return finishedDisplaying;
    }

    @Override
    public void draw(Canvas canvas) {
        dialog.draw(canvas);
    }
    // Debug purposes
    @Override
    public String toString() {
        return "Dialog : Text="+text+" , Instant display : "+instantDisplay+ " , cooldown : "+cooldownBeforeDisappearing ;
    }
}
