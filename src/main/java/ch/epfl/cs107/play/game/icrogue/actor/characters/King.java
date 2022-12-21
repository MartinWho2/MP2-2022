package ch.epfl.cs107.play.game.icrogue.actor.characters;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.SpeakerActor;
import ch.epfl.cs107.play.game.icrogue.actor.items.Diplome;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.io.XMLTexts;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

public class King extends SpeakerActor {
    private Sprite sprite;
    private final String[] dialogKeys = new String[]{"text-king-1", "text-king-2", "text-king-3"};
    private int dialogIndex = 0;
    public King(Area area, DiscreteCoordinates spawn) {
        super(area, Orientation.DOWN, spawn);
        sprite = new Sprite("zelda/king",.5f,1f,this,new RegionOfInterest(0,64,16,32));
        enterArea(area, spawn);
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }
    public void nextDialog(){
        if (speak(XMLTexts.getText(dialogKeys[dialogIndex]),false)){
          dialogIndex++;
            if (dialogIndex == 1){
                new Diplome(getOwnerArea(),getOrientation(),getCurrentMainCellCoordinates().jump(0,-2));

            }
        }
        if (dialogIndex >2)
            dialogIndex = 2;
    }
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        sprite.draw(canvas);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }
}
