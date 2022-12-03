package ch.epfl.cs107.play.game.icrogue.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.ICRogue;
import ch.epfl.cs107.play.game.icrogue.ICRogueBehavior;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.game.icrogue.actor.items.Key;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.List;

public abstract class ICRogueRoom extends Area {
    private ICRogueBehavior behavior;
    private String behaviorName;
    private final DiscreteCoordinates roomCoordinates;
    private ArrayList<Connector> connectors = new ArrayList<>();
    private Keyboard keyboard;



    /**
     * Create the area by adding it all actors
     * called by begin method
     * Note it set the Behavior as needed !
     */
    protected abstract void createArea();

    /// EnigmeArea extends Area

    public ICRogueRoom(List<DiscreteCoordinates > connectorsCoordinates ,
                       List<Orientation> orientations ,
                       String behaviorName, DiscreteCoordinates roomCoordinates){
        super();
        this.behaviorName = behaviorName;
        this.roomCoordinates = roomCoordinates;
        for (int i = 0; i < connectorsCoordinates.size(); i++) {
            connectors.add(new Connector(this,orientations.get(i),connectorsCoordinates.get(i)));
        }
    }
    public DiscreteCoordinates getRoomCoordinates(){
        return roomCoordinates;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        keyboard = getKeyboard();
        if (keyboard.get(Keyboard.O).isPressed()){
            for (Connector connector: connectors) {
                System.out.println(connector.getState());
                connector.setState(Connector.ConnectorType.OPEN);

            }
        }else if (keyboard.get(Keyboard.L).isPressed()){
            connectors.get(0).setState(Connector.ConnectorType.LOCKED);
            connectors.get(0).setKEY_ID(1);
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

    /// Demo2Area implements Playable

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            // Set the behavior map
            behavior = new ICRogueBehavior(window, behaviorName);
            setBehavior(behavior);
            for (Connector connector: connectors) {
                registerActor(connector);
            }
            createArea();
            return true;
        }
        return false;
    }
}
