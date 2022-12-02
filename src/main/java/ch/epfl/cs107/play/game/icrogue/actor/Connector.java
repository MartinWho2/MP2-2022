package ch.epfl.cs107.play.game.icrogue.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.HashMap;
import java.util.List;

public class Connector extends AreaEntity {
    public static int NO_KEY_ID = 0;
    private String destination;
    private DiscreteCoordinates destinationCoord;
    private final int KEY_ID;
    private ConnectorType state = ConnectorType.INVISIBLE;
    private Sprite sprite;
    private HashMap<ConnectorType, Sprite> typeToSprite;


    @Override
    public void draw(Canvas canvas) {
        if (!state.isWalkable){
            sprite.draw(canvas);
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        DiscreteCoordinates coord = getCurrentMainCellCoordinates ();
        return List.of(coord , coord .jump(new Vector((getOrientation().ordinal()+1) % 2,
                getOrientation().ordinal()%2)));
    }

    public Connector(Area area, Orientation orientation, DiscreteCoordinates position, int key_id) {
        super(area, orientation, position);
        this.KEY_ID = key_id;
        typeToSprite.put(ConnectorType.INVISIBLE, new Sprite("icrogue/invisibleDoor_"+ orientation.ordinal (),
                (orientation.ordinal () +1) %2+1 , orientation.ordinal()%2+1 , this));
        typeToSprite.put(ConnectorType.CLOSED, new Sprite ("icrogue/door_"+ orientation.ordinal () ,
                (orientation.ordinal () +1) %2+1 , orientation . ordinal () %2+1 , this));
        typeToSprite.put(ConnectorType.LOCKED, new Sprite ("icrogue/lockedDoor_"+ orientation.ordinal (),
                    (orientation.ordinal () +1) %2+1 , orientation.ordinal () %2+1 , this));
        sprite = typeToSprite.get(state);
    }

    public  Connector(Area area, Orientation orientation, DiscreteCoordinates position) {
        this(area, orientation, position, NO_KEY_ID);
    }

    public enum ConnectorType {
        OPEN(1, false),
        CLOSED(2, true),
        LOCKED(3, true),
        INVISIBLE(4, true);
        final int type;
        final boolean isWalkable;
        ConnectorType(int type, boolean isWalkable) {
            this.type = type;
            this.isWalkable = isWalkable;
        }
    }

    @Override
    public boolean takeCellSpace() {
        return state.isWalkable;
    }

    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return state.equals(ConnectorType.LOCKED);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }
}
