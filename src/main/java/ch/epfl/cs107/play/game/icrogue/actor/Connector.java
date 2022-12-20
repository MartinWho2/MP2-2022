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
    private int KEY_ID;
    private ConnectorType state = ConnectorType.INVISIBLE;
    private Sprite sprite;
    private HashMap<ConnectorType, Sprite> typeToSprite = new HashMap<>();

    /**
     * Setup all connector types and their corresponding type, and other useful attributes
     * @param area (Area): Owner area of the connectors
     * @param orientation (Orientation): Orientation of the connectors
     * @param position (DiscreteCoordinates): Position of the connectors in the room
     * @param key_id (int): key id
     */
    public Connector(Area area, Orientation orientation, DiscreteCoordinates position, int key_id) {
        super(area, orientation, position);
        this.KEY_ID = key_id;
        typeToSprite.put(ConnectorType.INVISIBLE, new Sprite("icrogue/invisibleDoor_"+ orientation.ordinal(),
                (orientation.ordinal() + 1) % 2+1 , orientation.ordinal()%2+1 , this));
        typeToSprite.put(ConnectorType.CLOSED, new Sprite ("icrogue/door_"+ orientation.ordinal () ,
                (orientation.ordinal () +1) %2+1 , orientation . ordinal () %2+1 , this));
        typeToSprite.put(ConnectorType.LOCKED, new Sprite("icrogue/lockedDoor_"+ orientation.ordinal (),
                (orientation.ordinal () +1) %2+1 , orientation.ordinal () %2+1 , this));
        typeToSprite.put(ConnectorType.CRACKED, new Sprite("other/forgeronDoor_"+orientation.ordinal(),
                (orientation.ordinal()+1)%2+1,orientation.ordinal()%2+1,this));
        typeToSprite.put(ConnectorType.OPEN,null);
        sprite = typeToSprite.get(state);
    }

    /**
     *  Alternative constructor without KEY_ID
     * @param area (Area): Owner area of the connectors
     * @param orientation (Orientation): Orientation of the connectors
     * @param position (DiscreteCoordinates): Position of the connectors in the room
     */
    public  Connector(Area area, Orientation orientation, DiscreteCoordinates position) {
        this(area, orientation, position, NO_KEY_ID); // Set connector without key to unlock key
    }

    @Override
    public void draw(Canvas canvas) {
        if (sprite != null){
            sprite.draw(canvas);
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        DiscreteCoordinates coord = getCurrentMainCellCoordinates ();
        return List.of(coord , coord .jump(new Vector((getOrientation().ordinal()+1) % 2,
                getOrientation().ordinal()%2)));
    }

    /**
     * Set the room destination coordinates
     * @param coord (DiscreteCoordinates): destination of the coordinates
     */
    public void setDestinationCoord(DiscreteCoordinates coord) {
        destinationCoord = coord;
    }

    /**
     * Compute the room spawn coordinates given the position of an entity behind another connector
     * @param position (DiscreteCoordinates): position of the player behind the connector
     * @return (DiscreteCoordinates): the right spawn coordinates in the destination room
     */
    public static DiscreteCoordinates getSpawnPositionWithEnterCoordinates(DiscreteCoordinates position){
        if (position.x == 0){
            return new DiscreteCoordinates(8,position.y);
        }
        if (position.x == 9){
            return new DiscreteCoordinates(1, position.y);
        }
        if (position.y == 0){
            return new DiscreteCoordinates(position.x,8);
        }
        if (position.y == 9){
            return new DiscreteCoordinates(position.x,1);
        }
        return new DiscreteCoordinates(5,5);
    }

    public DiscreteCoordinates getDestinationCoord() {
        return destinationCoord;
    }

    /**
     * Set state of the connector
     * @param state (ConnectorType): new state of the connector
     */
    public void setState(ConnectorType state) {
        this.state = state;
        sprite = typeToSprite.get(state);
    }

    public ConnectorType getState() {
        return state;
    }

    public void setKEY_ID(int key_id){
        KEY_ID = key_id;
    }

    public int getKEY_ID() {
        return KEY_ID;
    }

    public String getDestinationRoom() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }


    /**
     * Enum that specify all connector's types
     */
    public enum ConnectorType {
        OPEN(1, false),
        CLOSED(2, true),
        LOCKED(3, true),
        INVISIBLE(4, true),
        CRACKED(5,true);
        final int type;
        final boolean takesSpace;
        ConnectorType(int type, boolean takesSpace) {
            this.type = type;
            this.takesSpace = takesSpace;
        }
    }

    @Override
    public boolean takeCellSpace() {
        return state.takesSpace;
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
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }
}
