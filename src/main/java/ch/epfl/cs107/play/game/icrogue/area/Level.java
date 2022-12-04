package ch.epfl.cs107.play.game.icrogue.area;


import ch.epfl.cs107.play.game.icrogue.ICRogue;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public abstract class Level implements Logic {
    protected ICRogueRoom[][] wholeMap;
    private final int WIDTH;
    private final int HEIGHT;
    private DiscreteCoordinates spawnCoordinates;
    private DiscreteCoordinates bossCoordinates;
    private String firstRoomName;


    protected void setRoom(DiscreteCoordinates coordinates, ICRogueRoom room){
        wholeMap[coordinates.x][coordinates.y] = room;

    }
    protected void setRoomConnectorDestination(DiscreteCoordinates coords, String destination,
                                            ConnectorInRoom connector){
        wholeMap[coords.x][coords.y].setConnectorDestination(connector.getIndex(),destination);

    }
    protected void setRoomConnector(DiscreteCoordinates coords, String destination,
                                 ConnectorInRoom connector){
        wholeMap[coords.x][coords.y].setConnectorDestination(connector.getIndex(),destination);
        wholeMap[coords.x][coords.y].setConnectorClosed(connector.getIndex(), Connector.ConnectorType.CLOSED);

    }

    protected void setRoomConnectorOpen(DiscreteCoordinates coords, String destination, ConnectorInRoom connector) {
        wholeMap[coords.x][coords.y].setConnectorDestination(connector.getIndex(),destination);
        wholeMap[coords.x][coords.y].setConnectorOpen(connector.getIndex());
    }
    protected void lockRoomConnector(DiscreteCoordinates coords, ConnectorInRoom connector, int keyId){
        wholeMap[coords.x][coords.y].setConnectorClosed(connector.getIndex(), Connector.ConnectorType.LOCKED);
        wholeMap[coords.x][coords.y].setConnectorLocked(connector.getIndex(), keyId);
    }
    protected void setFirstRoomName(DiscreteCoordinates coordinates){
        firstRoomName = "icrogue/level0" + coordinates.x+""+coordinates.y;
    }

    public void setCurrentRoom(ICRogue a, DiscreteCoordinates coords) {
        a.setCurrentAreaOfLevel(wholeMap[coords.x][coords.y]);
    }
    /*
    public DiscreteCoordinates getSpawnCoordinates(DiscreteCoordinates coord, ConnectorInRoom connector) {
        return wholeMap[coord.x][coord.y].getSpawnPosition(connector);
    }
    */

    public void registerAreas(ICRogue a) {
        for (ICRogueRoom[] rooms: wholeMap) {
            for (ICRogueRoom room : rooms) {
                if (room != null) {
                    a.addArea(room);
                    if (room.getTitle().equals(firstRoomName)){
                        a.setCurrentAreaOfLevel(room);
                    }
                }

            }
        }
    }
    protected Level(DiscreteCoordinates coordinates, DiscreteCoordinates mapSize){
        WIDTH = mapSize.x;
        HEIGHT = mapSize.y;
        wholeMap = new ICRogueRoom[WIDTH][HEIGHT];
        spawnCoordinates = coordinates;
        bossCoordinates = new DiscreteCoordinates(0,0);
    }


    @Override
    public boolean isOn() {
        if (wholeMap[bossCoordinates.x][bossCoordinates.y] != null) return wholeMap[bossCoordinates.x][bossCoordinates.y].challengeSucceeded;
        return false;
    }

    @Override
    public boolean isOff() {
        if (wholeMap[bossCoordinates.x][bossCoordinates.y] != null) return !wholeMap[bossCoordinates.x][bossCoordinates.y].challengeSucceeded;
        return true;
    }

    @Override
    public float getIntensity() {
        if (wholeMap[bossCoordinates.x][bossCoordinates.y] != null) return wholeMap[bossCoordinates.x][bossCoordinates.y].challengeSucceeded? 1.f : 0.f;
        return 0.f;
    }

}
