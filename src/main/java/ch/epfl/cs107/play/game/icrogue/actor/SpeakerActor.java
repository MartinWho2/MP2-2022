package ch.epfl.cs107.play.game.icrogue.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public abstract class SpeakerActor extends ICRogueActor{
    private Dialog currentDialog;
    private boolean isSpeaking;


    /**
     * Basic constructor
     * @param area (Area): Area of the speaker
     * @param orientation (Orientation): orientation of the speaker
     * @param spawn (DiscreteCoordinates): position of the speaker
     */
    public SpeakerActor(Area area, Orientation orientation, DiscreteCoordinates spawn) {
        super(area, orientation, spawn);
    }

    @Override
    public void draw(Canvas canvas) {
        if (isSpeaking){
            currentDialog.draw(canvas);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (isSpeaking)
            updateText(deltaTime);
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }

    /**
     * Updates the text
     * @param deltaTime (float): time since last frame
     */
    public void updateText(float deltaTime){
        currentDialog.update(deltaTime);
        if (currentDialog.isNeedsToBeRemoved()){
            currentDialog = null;
            isSpeaking = false;
        }
    }

    /**
     * @return true if the current dialog is finished or if there is no current dialog
     */
    public boolean isCurrentDialogFinished(){
        if (currentDialog == null){
            return true;
        }
        return currentDialog.isFinished();
    }

    /**
     * Creates a new Dialog if the previous one is finished
     * @param dialog (String): The text of the dialog
     * @param instantDisplay (boolean): if the text needs to be entirely displayed intantly
     * @return true if the dialog could be created, false otherwise
     */
    public boolean speak(String dialog, boolean instantDisplay) {
        if (isCurrentDialogFinished()){
            isSpeaking = true;
            currentDialog = new Dialog(this,getCurrentMainCellCoordinates(),dialog,instantDisplay);
            return true;
        }
        return false;
    }
}
