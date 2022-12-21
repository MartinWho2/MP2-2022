package ch.epfl.cs107.play.game.icrogue.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.ICRogue;
import ch.epfl.cs107.play.game.icrogue.ICRogueBehavior;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.List;

public abstract class ICRogueRoom extends Area implements Logic {
    private ICRogueBehavior behavior;
    private final String behaviorName;
    private final DiscreteCoordinates roomCoordinates;
    private final ArrayList<Connector> connectors = new ArrayList<>();
    private boolean hasPlayerEntered = false;
    private boolean challengeSucceeded = false;

    /**
     * Init useful attributes
     * @param connectorsCoordinates (List<DiscreteCoordinates>): connector's coordinates
     * @param orientations (List<Orientation>): connector's orientation
     * @param behaviorName (String): Path to the behavior map
     * @param roomCoordinates (DiscreteCoordinates): room coordinates on the roomMap
     */
    public ICRogueRoom(List<DiscreteCoordinates > connectorsCoordinates ,
                       List<Orientation> orientations ,
                       String behaviorName, DiscreteCoordinates roomCoordinates){
        this.behaviorName = behaviorName;
        this.roomCoordinates = roomCoordinates;
        for (int i = 0; i < connectorsCoordinates.size(); i++) {
            connectors.add(new Connector(this,orientations.get(i),connectorsCoordinates.get(i)));
        }
    }

    /**
     * Create the area by adding it all actors
     * called by begin method
     * Note it set the Behavior as needed !
     */
    protected abstract void createArea();

    public boolean getHasPlayerEntered() {
        return hasPlayerEntered;
    }

    public boolean getChallengeSucceeded() { return challengeSucceeded; }

    public void playerEnters() {
        this.hasPlayerEntered = true;
        tryToFinishRoom();
        //openConnectorsClosed();
    }

    public DiscreteCoordinates getRoomCoordinates(){
        return roomCoordinates;
    }

    /**
     * Define the destination of the connector
     * @param connectorIndex (int): index of the connector
     * @param destination (String): destination of the corrector
     */
    public void setConnectorDestination(int connectorIndex, String destination){
        connectors.get(connectorIndex).setDestination(destination);
    }

    public DiscreteCoordinates getSpawnPosition(ConnectorInRoom connector) {
        return connector.getDestination();
    }


    public void setConnectorsCracked(int connectorIndex){
        connectors.get(connectorIndex).setState(Connector.ConnectorType.CRACKED);
    }

    public void setConnectorClosed(int connectorIndex){
        connectors.get(connectorIndex).setState(Connector.ConnectorType.CLOSED);
    }

    /**
     * Open all connectors that are closed
     */
    public void openConnectorsClosed(){
        for (Connector connector : connectors){
            if (connector.getState().equals(Connector.ConnectorType.CLOSED)){
                setConnectorOpen(connector);
            }
        }
    }

    /**
     * Check if the challenge of the room is completed
     */
    public void tryToFinishRoom(){
        if (challengeCompleted()){
            openConnectorsClosed();
            challengeSucceeded = true;
        }
    }

    public boolean challengeCompleted() {
        return true;
    }

    public void setConnectorOpen(int connectorIndex) {
        connectors.get(connectorIndex).setState(Connector.ConnectorType.OPEN);
    }
    public void setConnectorLocked(int connectorIndex, int key_id){
        connectors.get(connectorIndex).setState(Connector.ConnectorType.LOCKED);
        connectors.get(connectorIndex).setKEY_ID(key_id);
    }
    public void setConnectorOpen(Connector connector){
        connector.setState(Connector.ConnectorType.OPEN);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Keyboard keyboard = getKeyboard();
        // open connectors when "O" is pressed
        if (keyboard.get(Keyboard.O).isPressed()){
            for (Connector connector: connectors) {
                connector.setState(Connector.ConnectorType.OPEN);
            }
        // close and lock first connector
        }else if (keyboard.get(Keyboard.L).isPressed()){
            connectors.get(0).setState(Connector.ConnectorType.LOCKED);
            connectors.get(0).setKEY_ID(1);
        // open closed connectors and close opened connectors
        }else if (keyboard.get(Keyboard.T).isPressed()){
            for (Connector connector: connectors) {
                if (connector.getState().equals(Connector.ConnectorType.OPEN)){
                    connector.setState(Connector.ConnectorType.CLOSED);
                }else if (connector.getState().equals(Connector.ConnectorType.CLOSED)){
                    connector.setState(Connector.ConnectorType.OPEN);
                }
            }
        }
    }

    @Override
    public final float getCameraScaleFactor() {
        return ICRogue.CAMERA_SCALE_FACTOR;
    }

    public abstract DiscreteCoordinates getPlayerSpawnPosition();

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            // Set the behavior map
            behavior = new ICRogueBehavior(window, behaviorName);
            setBehavior(behavior);
            // register the connectors
            for (Connector connector: connectors) {
                registerActor(connector);
            }
            createArea();
            return true;
        }
        return false;
    }
}
