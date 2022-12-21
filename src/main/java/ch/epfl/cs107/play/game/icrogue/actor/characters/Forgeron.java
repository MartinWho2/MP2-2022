package ch.epfl.cs107.play.game.icrogue.actor.characters;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.SpeakerActor;
import ch.epfl.cs107.play.game.icrogue.actor.items.Key;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.area.level0.Level0;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.io.XMLTexts;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

public class Forgeron extends SpeakerActor {
    private  Animation[] animationsMove;
    private Sprite[][] spritesMove;
    private final Sprite sprite;
    private String textKey;
    static final String[] dialogKey = new String[]{"text-forgeron-1", "text-forgeron-2", "text-forgeron-3", "text-forgeron-4",
    "text-forgeron-5","text-forgeron-6"};
    private int dialogIndex;
    private boolean finishedDialog;

    /**
     * Init all useful class attributes
     * @param area (Area): owner Area
     * @param orientation (Orientation): orientation of the character
     * @param spawn (DiscreteCoordinates): spawn coordinates in the room
     */
    public Forgeron(Area area, Orientation orientation, DiscreteCoordinates spawn) {
        super(area, orientation, spawn);
        dialogIndex = 0;
        sprite = new Sprite("other/forgeron", 1.f, 1.f, this, new RegionOfInterest(0, 0, 16, 16));
        enterArea(area, new DiscreteCoordinates(spawn.x, spawn.y));
        finishedDialog = false;
    }

    /**
     * Reset the dialog if the player leave the room
     */
    public void resetDialog() {
        dialogIndex = 0;
        finishedDialog = false;
    }

    public boolean getFinishedTalking() { return finishedDialog; }
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // If the text needs to be changed
        if (!getFinishedTalking() && dialogIndex == dialogKey.length) {
            // Makes a Boss key spawn
            finishedDialog = true;
            new Key(getOwnerArea(), Orientation.DOWN,getCurrentMainCellCoordinates().jump(0,-2), Level0.BOSS_KEY_ID);
            ICRogueRoom area = (ICRogueRoom) getOwnerArea();
            area.tryToFinishRoom();
        }
        //animationsMove[getOrientation().ordinal()].update(deltaTime);
    }


    @Override
    public boolean takeCellSpace() {
        return true;
    }


    public void updateText() {
        if (isCurrentDialogFinished() && !getFinishedTalking()) {
            speak(XMLTexts.getText(dialogKey[dialogIndex]),false);
            dialogIndex++;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        sprite.draw(canvas);
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }
}
