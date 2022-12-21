package ch.epfl.cs107.play.game.icrogue.actor;

import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public abstract class SpeakerActor extends ICRogueActor{
    private Dialog currentDialog;
    private boolean isSpeaking;



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

    public void updateText(float deltaTime){
        currentDialog.update(deltaTime);
        if (currentDialog.isNeedsToBeRemoved()){
            currentDialog = null;
            isSpeaking = false;
        }
    }
    public boolean isCurrentDialogFinished(){
        if (currentDialog == null){
            return true;
        }
        return currentDialog.isFinished();
    }

    public void speak(String dialog, boolean instantDisplay) {
        if (isCurrentDialogFinished()){
            isSpeaking = true;
            currentDialog = new Dialog(this,getCurrentMainCellCoordinates(),dialog,instantDisplay);
            System.out.println(currentDialog);
        }

    }
}
