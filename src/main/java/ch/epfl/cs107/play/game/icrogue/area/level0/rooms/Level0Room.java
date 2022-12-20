package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.area.ConnectorInRoom;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.signal.logic.Or;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.List;

public class Level0Room extends ICRogueRoom {
    private final static String behaviorName = "icrogue/Level0Room";

    /**
     * Init super
     * @param roomCoordinates (DiscreteCoordinates): room coordinates on the roomMap
     */
    public Level0Room(DiscreteCoordinates roomCoordinates){
        super(Level0Connectors.getAllConnectorsPosition(),Level0Connectors.getAllConnectorsOrientation(),
                behaviorName,roomCoordinates);
    }

    @Override
    public boolean challengeCompleted() {
        return super.challengeCompleted();
    }

    @Override
    public String getTitle() {
        return "icrogue/level0"+getRoomCoordinates().x + ""+getRoomCoordinates().y;
    }

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(5,5);
    }

    protected void createArea() {
        registerActor(new Background(this, behaviorName));
    }

    @Override
    public boolean isOn() {
        return getChallengeSucceeded();
    }

    @Override
    public boolean isOff() {
        return !getChallengeSucceeded();
    }

    @Override
    public float getIntensity() {
        return getChallengeSucceeded() ? 1.f : 0.f;
    }

    public enum Level0Connectors implements ConnectorInRoom {
        // ordre des attributs: position , destination , orientation
        W(new DiscreteCoordinates(0, 4),
                new DiscreteCoordinates(8, 5), Orientation.RIGHT),
        S(new DiscreteCoordinates(4, 0),
                new DiscreteCoordinates(5, 8), Orientation.UP),
        E(new DiscreteCoordinates(9, 4),
                new DiscreteCoordinates(1, 5), Orientation.LEFT),
        N(new DiscreteCoordinates(4, 9),
                new DiscreteCoordinates(5, 1), Orientation.DOWN);
        private DiscreteCoordinates position;
        private DiscreteCoordinates destination;
        private Orientation orientation;
            Level0Connectors(DiscreteCoordinates position, DiscreteCoordinates destination, Orientation orientation){
                this.position = position;
                this.destination = destination;
                this.orientation = orientation;
            }

        @Override
        public int getIndex() {
            return this.ordinal();
        }

        public Orientation getOrientation() {
            return orientation;
        }

        @Override
        public DiscreteCoordinates getDestination() {
            return this.destination;
        }

        public static List<Orientation> getAllConnectorsOrientation(){
            List<Orientation> connectorOrientations = new ArrayList<>();
            for (Level0Connectors connector: Level0Connectors.values()) {
                connectorOrientations.add(connector.orientation);
            }
            return connectorOrientations;
        }

        public static List <DiscreteCoordinates > getAllConnectorsPosition(){
            List<DiscreteCoordinates> coordinates = new ArrayList<>();
            for (Level0Connectors connector: Level0Connectors.values()) {
                coordinates.add(connector.position);
            }
            return coordinates;
        }

    }
}
