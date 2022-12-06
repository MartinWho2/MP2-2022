package ch.epfl.cs107.play.game.icrogue.area;


import ch.epfl.cs107.play.game.icrogue.ICRogue;
import ch.epfl.cs107.play.game.icrogue.RandomHelper;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0Room;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0StaffRoom;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0TurretRoom;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.*;
import java.util.stream.IntStream;

public abstract class Level implements Logic {
    protected ICRogueRoom[][] wholeMap;
    private final int WIDTH;
    private final int HEIGHT;
    private DiscreteCoordinates spawnCoordinates;
    private DiscreteCoordinates bossCoordinates;
    private String firstRoomName;
    private HashMap<Integer,ICRogueRoom> indexRoomToRoom;


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
    protected MapState[][] generateRandomRoomPlacement(){
        MapState[][] map = new MapState[WIDTH][HEIGHT];
        int roomsToPlace = WIDTH;
        ArrayList<DiscreteCoordinates> placedRooms = new ArrayList<>();
        for (MapState[] mapStates : map) {
            Arrays.fill(mapStates, MapState.NULL);
        }

        map[WIDTH/2][HEIGHT/2] = MapState.PLACED;
        placedRooms.add(new DiscreteCoordinates(WIDTH/2,HEIGHT/2));
        DiscreteCoordinates currentRoom = new DiscreteCoordinates(0,0);
        while (roomsToPlace > 0) {
            currentRoom = placedRooms.get(0);
            List<Integer> freeSlots = new ArrayList<>();
            // FIRST IS UP AND THEN CLOCKWISE
            if ((currentRoom.y) > 0 && map[currentRoom.x][currentRoom.y - 1].equals(MapState.NULL)) {
                freeSlots.add(1);
            }
            if ((currentRoom.x + 1) < map.length && map[currentRoom.x + 1][currentRoom.y].equals(MapState.NULL)) {
                freeSlots.add(2);
            }
            if ((currentRoom.y + 1) < map[currentRoom.x].length && map[currentRoom.x][currentRoom.y + 1].equals(MapState.NULL)) {
                freeSlots.add(3);
            }
            if ((currentRoom.x) > 0 && map[currentRoom.x - 1][currentRoom.y].equals(MapState.NULL)) {
                freeSlots.add(4);
            }
            int maxRoomsToAdd = Math.min(freeSlots.size(), roomsToPlace);
            List<Integer> newRooms = RandomHelper.chooseKInList(RandomHelper.roomGenerator.nextInt(1, maxRoomsToAdd+1), freeSlots);
            for (Integer room : newRooms) {
                if (room.equals(1)) {
                    map[currentRoom.x][currentRoom.y - 1] = MapState.PLACED;
                    placedRooms.add(new DiscreteCoordinates(currentRoom.x, currentRoom.y - 1));
                }
                if (room.equals(2)) {
                    map[currentRoom.x + 1][currentRoom.y] = MapState.PLACED;
                    placedRooms.add(new DiscreteCoordinates(currentRoom.x + 1, currentRoom.y));
                }
                if (room.equals(3)) {
                    map[currentRoom.x][currentRoom.y + 1] = MapState.PLACED;
                    placedRooms.add(new DiscreteCoordinates(currentRoom.x, currentRoom.y + 1));
                }
                if (room.equals(4)) {
                    map[currentRoom.x - 1][currentRoom.y] = MapState.PLACED;
                    placedRooms.add(new DiscreteCoordinates(currentRoom.x - 1, currentRoom.y));
                }
                roomsToPlace--;
            }
            placedRooms.remove(0);
            map[currentRoom.x][currentRoom.y] = MapState.EXPLORED;  //TODO ça sert à quoi ça ?

        }
        List<DiscreteCoordinates> possibleRooms;
        do {
            possibleRooms = findNearbyUnoccupiedRooms(map,currentRoom);
            placedRooms.remove(0);
            if (placedRooms.size() == 0) return null;
            currentRoom = placedRooms.get(0);
        } while (possibleRooms.size() == 0);
        map[possibleRooms.get(0).x][possibleRooms.get(0).y] = MapState.BOSS_ROOM;
        printMap(map);
        return map;
    }

    private void printMap ( MapState [][] map ) {
        System . out . println ("Generated map:");
        System . out . print ("  |");
        for ( int j = 0; j < map [0]. length ; j ++) {
            System . out . print (j + " ");
        }
        System . out . println ();
        System . out . print ("--|-");
        for ( int j = 0; j < map [0]. length ; j ++) {
            System . out . print ("--");
        }
        System . out . println ();
        for ( int i = 0; i < map . length ; i ++) {
            System . out . print (i + " | ");
            for ( int j = 0; j < map [i]. length ; j ++) {
                System . out . print ( map [i][j] + " ");
            }
            System . out . println ();
        }
        System . out . println ();
    }

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
    private List<DiscreteCoordinates> findNearbyUnoccupiedRooms(MapState[][] map, DiscreteCoordinates currentRoom){
        List<DiscreteCoordinates> rooms = new ArrayList<>();
        if ((currentRoom.y) > 0 && map[currentRoom.x][currentRoom.y - 1].equals(MapState.NULL)) {
            rooms.add(new DiscreteCoordinates(currentRoom.x, currentRoom.y-1));
        }
        if((currentRoom.x + 1) < map.length && map[currentRoom.x + 1][currentRoom.y].equals(MapState.NULL)){
            rooms.add(new DiscreteCoordinates(currentRoom.x+1, currentRoom.y));
        }
        if ((currentRoom.y + 1) < map[currentRoom.x].length && map[currentRoom.x][currentRoom.y + 1].equals(MapState.NULL)){
            rooms.add(new DiscreteCoordinates(currentRoom.x, currentRoom.y+1));
        }
        if ((currentRoom.x) > 0 && map[currentRoom.x - 1][currentRoom.y].equals(MapState.NULL)) {
            rooms.add(new DiscreteCoordinates(currentRoom.x-1, currentRoom.y));
        }
        return rooms;
    }
    protected Level(DiscreteCoordinates coordinates, DiscreteCoordinates mapSize){
        WIDTH = mapSize.x;
        HEIGHT = mapSize.y;
        wholeMap = new ICRogueRoom[WIDTH][HEIGHT];
        spawnCoordinates = coordinates;
        bossCoordinates = new DiscreteCoordinates(0,0);
        generateRandomRoomPlacement();
    }
    protected Level(boolean randomMap , DiscreteCoordinates startPosition ,
                    int[] roomsDistribution , int width , int height){
        if (false){
            generateRandomMap(roomsDistribution);
        }else {
            WIDTH = width;
            HEIGHT = height;
            wholeMap = new ICRogueRoom[WIDTH][HEIGHT];
            spawnCoordinates = startPosition;
        }

    }

    protected void generateRandomMap(int[] roomsDistribution){
        int nbRooms = IntStream.of(roomsDistribution).sum();
        //WIDTH = nbRooms;
        //HEIGHT = nbRooms;
        MapState[][] mapRooms = generateRandomRoomPlacement();
        List<DiscreteCoordinates> roomsCoordinates  = getCoordinatesOfRooms(mapRooms);
        List<Integer> indexesOfRoomsCoordinates = new ArrayList<>();
        List<Integer> chosenRooms;


        for (int i=0; i< roomsCoordinates.size();i++){
            indexesOfRoomsCoordinates.add(i);
        }
        for (int i : roomsDistribution){
            chosenRooms = RandomHelper.chooseKInList(i,indexesOfRoomsCoordinates);
            for (Integer index: chosenRooms) {
                ////////

            }
        }
    }

    private List<DiscreteCoordinates> getCoordinatesOfRooms(MapState[][] mapRooms) {
        List<DiscreteCoordinates> roomsCoordinate = new ArrayList<>();
        for (int i = 0; i < mapRooms.length; i++) {
            for (int j = 0; j < mapRooms[i].length; j++) {
                if (mapRooms[i][j].equals(MapState.PLACED) || mapRooms[i][j].equals(MapState.EXPLORED)){
                    roomsCoordinate.add(new DiscreteCoordinates(i,j));
                }
            }
        }
        return roomsCoordinate;
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
    protected enum MapState {
        NULL , // Empty space
        PLACED , // The room has been placed but not yet explored by the room placement algorithm
        EXPLORED , // The room has been placed are explored by the algorithm
        BOSS_ROOM , // The room is a boss room
        CREATED; // The room has been instantiated in the room map
        @Override
        public String toString() {
            return Integer.toString(ordinal());
        }
    }
    protected enum RoomType{
        TURRET (0),
        STAFF (1),
        BOSS_KEY (2),
        SPAWN (3),
        NORMAL(4);
        final int roomType;
        RoomType(int value){
            this.roomType = value;
        }
    }


}
