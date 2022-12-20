package ch.epfl.cs107.play.game.icrogue.actor.characters;

import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.ICRogueActor;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.io.XMLTexts;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.*;
import java.beans.XMLDecoder;

public class Forgeron extends ICRogueActor {
    private  Animation[] animationsMove;
    private Sprite[][] spritesMove;
    private final TextGraphics message;
    private final Sprite sprite;
    private boolean displayNextText;
    private String textKey;
    private int counter;
    static final String[] dialogKey = new String[]{"text-forgeron-1", "text-forgeron-2", "text-forgeron-3", "text-forgeron-4"};
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
        message = new TextGraphics(XMLTexts.getText(dialogKey[dialogIndex++]), 0.4f, Color.WHITE);
        message.setParent(this);
        message.setAnchor(new Vector(0, 1f));
        sprite = new Sprite("zelda/player", 1.f, 1.f, this, new RegionOfInterest(0, 0, 16, 32));
        enterArea(area, new DiscreteCoordinates(spawn.x, spawn.y));
        displayNextText = false;
        counter = 0;
        finishedDialog = false;
    }

    /**
     * Reset the dialog if the player leave the room
     */
    public void resetDialog() {
        dialogIndex = 0;
        message.setText(XMLTexts.getText(dialogKey[dialogIndex++]));
        finishedDialog = false;
    }

    public boolean getFinishedDialog() { return finishedDialog; }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (displayNextText && !finishedDialog) {
            if (counter < XMLTexts.getText(dialogKey[dialogIndex]).length()) {
                message.setText(XMLTexts.getText(dialogKey[dialogIndex]).substring(0,++counter));
            } else {
                displayNextText = false;
                dialogIndex++;
                counter = 0;
            }
            if (dialogIndex == dialogKey.length) {
                finishedDialog = true;
                ICRogueRoom area = (ICRogueRoom) getOwnerArea();
                area.tryToFinishRoom();
            }
        }
        //animationsMove[getOrientation().ordinal()].update(deltaTime);
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }


    public void updateText() {
        displayNextText = true;
    }

    @Override
    public void draw(Canvas canvas) {
        // animationsMove[getOrientation().ordinal()].draw(canvas);
        message.draw(canvas);
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
