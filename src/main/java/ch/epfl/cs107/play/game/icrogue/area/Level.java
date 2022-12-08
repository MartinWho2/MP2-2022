package ch.epfl.cs107.play.game.icrogue.area;


import ch.epfl.cs107.play.game.icrogue.ICRogue;
import ch.epfl.cs107.play.game.icrogue.RandomHelper;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.*;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

public abstract class Level implements Logic {
    protected ICRogueRoom[][] wholeMap;
    private int WIDTH;
    private int HEIGHT;
    protected DiscreteCoordinates spawnCoordinates;
    protected DiscreteCoordinates bossCoordinates;
    protected DiscreteCoordinates forgeronCoordinates;
    private String firstRoomName;
    private HashMap<Integer,ICRogueRoom> indexRoomToRoom;
    private final int BOSS_KEY_ID = 5;
    private int NB_ROOMS;


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
        wholeMap[coords.x][coords.y].setConnectorClosed(connector.getIndex());
    }
    protected void setRoomConnectorOpen(DiscreteCoordinates coords, String destination, ConnectorInRoom connector) {
        wholeMap[coords.x][coords.y].setConnectorDestination(connector.getIndex(),destination);
        wholeMap[coords.x][coords.y].setConnectorOpen(connector.getIndex());
    }
    protected void lockRoomConnector(DiscreteCoordinates coords, ConnectorInRoom connector, int keyId){
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
        int roomsToPlace = NB_ROOMS;
        ArrayList<DiscreteCoordinates> placedRooms = new ArrayList<>();
        for (MapState[] mapStates : map) {
            Arrays.fill(mapStates, MapState.NULL);
        }
        map[WIDTH/2][HEIGHT/2] = MapState.PLACED;
        placedRooms.add(new DiscreteCoordinates(WIDTH/2,HEIGHT/2));
        DiscreteCoordinates currentRoom;
        roomsToPlace--;
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
            roomsToPlace = initNewRooms(map,currentRoom,newRooms, placedRooms, roomsToPlace);
        }
        List<DiscreteCoordinates> possibleRooms = findPlaceForSpecialRooms(map);
        findPlaceForBoss(map, possibleRooms);
        findPLaceForForgeron(map, possibleRooms);
        printMap(map);
        return map;
    }
    private void findPlaceForBoss(MapState[][] map, List<DiscreteCoordinates> possibleRooms){
        int index = RandomHelper.roomGenerator.nextInt(0,possibleRooms.size());
        map[possibleRooms.get(index).x][possibleRooms.get(index).y] = MapState.BOSS_ROOM;
        bossCoordinates = possibleRooms.get(index);
        possibleRooms.remove(bossCoordinates);
    }
    public List<DiscreteCoordinates> findPlaceForSpecialRooms(MapState[][] map) {
        List<DiscreteCoordinates> possibleRooms = new ArrayList<>();
        for (int x = 0; x < map.length; x++) {
            for(int y = 0; y < map[x].length; y++){
                if (map[x][y].equals(MapState.PLACED)||map[x][y].equals(MapState.EXPLORED)){
                    List<DiscreteCoordinates> nearbyRooms = findNearbyRooms(map,new DiscreteCoordinates(x, y),MapState.NULL);
                    if (nearbyRooms.size() > 0){
                        possibleRooms.addAll(nearbyRooms);
                    }
                }
            }
        }
        return possibleRooms;
    }
    private void findPLaceForForgeron(MapState[][] map, List<DiscreteCoordinates> possibleRooms) {
        int index = RandomHelper.roomGenerator.nextInt(0,possibleRooms.size());
        map[possibleRooms.get(index).x][possibleRooms.get(index).y] = MapState.FORGERON_ROOM;
        forgeronCoordinates = possibleRooms.get(index);
        possibleRooms.remove(index);
    }
    private int initNewRooms(MapState[][] map, DiscreteCoordinates currentRoom,List<Integer> newRooms,
                              List<DiscreteCoordinates> placedRooms, int roomsToPlace){
        for (Integer room : newRooms) {
            switch (room) {
                case 1 -> {
                    map[currentRoom.x][currentRoom.y - 1] = MapState.PLACED;
                    placedRooms.add(new DiscreteCoordinates(currentRoom.x, currentRoom.y - 1));
                }
                case 2 -> {
                    map[currentRoom.x + 1][currentRoom.y] = MapState.PLACED;
                    placedRooms.add(new DiscreteCoordinates(currentRoom.x + 1, currentRoom.y));
                }
                case 3 -> {
                    map[currentRoom.x][currentRoom.y + 1] = MapState.PLACED;
                    placedRooms.add(new DiscreteCoordinates(currentRoom.x, currentRoom.y + 1));
                }
                case 4 -> {
                    map[currentRoom.x - 1][currentRoom.y] = MapState.PLACED;
                    placedRooms.add(new DiscreteCoordinates(currentRoom.x - 1, currentRoom.y));

                }
            }
            roomsToPlace--;
        }
        placedRooms.remove(0);
        map[currentRoom.x][currentRoom.y] = MapState.EXPLORED;
        return roomsToPlace;
    }
    private void printMap ( MapState [][] map ) {
        System . out . println ("Generated map:");
        System . out . print ("  | ");
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
                System . out . print ( map [j][i] + " ");
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
    /**
     * @return the coordinates of the rooms around the given room that have the state given (ex: MapState.CREATED)
     */
    protected List<DiscreteCoordinates> findNearbyRooms(MapState[][] map, DiscreteCoordinates currentRoom, MapState state){
        List<DiscreteCoordinates> rooms = new ArrayList<>();
        if ((currentRoom.y) > 0 && map[currentRoom.x][currentRoom.y - 1].equals(state)) {
            rooms.add(new DiscreteCoordinates(currentRoom.x, currentRoom.y-1));
        }
        if((currentRoom.x + 1) < map.length && map[currentRoom.x + 1][currentRoom.y].equals(state)){
            rooms.add(new DiscreteCoordinates(currentRoom.x+1, currentRoom.y));
        }
        if ((currentRoom.y + 1) < map[currentRoom.x].length && map[currentRoom.x][currentRoom.y + 1].equals(state)){
            rooms.add(new DiscreteCoordinates(currentRoom.x, currentRoom.y+1));
        }
        if ((currentRoom.x) > 0 && map[currentRoom.x - 1][currentRoom.y].equals(state)) {
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
        NB_ROOMS = 5;
    }
    protected Level(boolean randomMap , DiscreteCoordinates startPosition ,
                    int[] roomsDistribution , int width , int height){
        NB_ROOMS = IntStream.of(roomsDistribution).sum();
        if (randomMap){
            generateRandomMap(roomsDistribution);
        }else {
            WIDTH = width;
            HEIGHT = height;
            wholeMap = new ICRogueRoom[WIDTH][HEIGHT];
            spawnCoordinates = startPosition;
            setFirstRoomName(spawnCoordinates);
        }

    }

    protected void generateRandomMap(int[] roomsDistribution){
        WIDTH = NB_ROOMS;
        HEIGHT = NB_ROOMS;
        wholeMap = new ICRogueRoom[WIDTH][HEIGHT];
        List<Integer> indexesOfRoomsCoordinates = new ArrayList<>();
        List<Integer> chosenRooms;
        MapState[][] mapRooms = generateRandomRoomPlacement();
        List<DiscreteCoordinates> roomsCoordinates  = getCoordinatesOfRooms(mapRooms);
        for (int i=0; i< roomsCoordinates.size();i++){
            indexesOfRoomsCoordinates.add(i);
        }
        for (int indexOfRoom=0; indexOfRoom<roomsDistribution.length;indexOfRoom++){
            int nbOfRoomOfType = roomsDistribution[indexOfRoom];
            System.out.println("there are "+nbOfRoomOfType+ " rooms of type "+indexOfRoom);
            chosenRooms = RandomHelper.chooseKInList(nbOfRoomOfType,indexesOfRoomsCoordinates);
            for (Integer chosenRoom : chosenRooms) {
                DiscreteCoordinates roomCoord = roomsCoordinates.get(chosenRoom);
                createRoomOfType(indexOfRoom, roomCoord);
                indexesOfRoomsCoordinates.remove(chosenRoom);
                mapRooms[roomCoord.x][roomCoord.y] = MapState.CREATED;
            }
        }
        generateBossRoom();
        generateForgeronRoom();
        generateConnectors(mapRooms);
    }

    private void generateBossRoom() {
        setRoom(bossCoordinates, new Level0BossRoom(bossCoordinates));
        System.out.println("the boss is at "+bossCoordinates);
    }

    private void generateForgeronRoom() {
        setRoom(forgeronCoordinates, new Level0ForgeronRoom(forgeronCoordinates));
    }

    protected void createRoomOfType(int nbOfRoomType, DiscreteCoordinates roomCoord){
        System.out.println("Should really never print.\n\n\nyou die");
    }

    private void generateConnectors(MapState[][] map){
        printMap(map);
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (wholeMap[i][j] != null && map[i][j].equals(MapState.CREATED)) {
                    setUpLevelConnector(map, wholeMap[i][j]);
                }
                if (map[i][j].equals(MapState.BOSS_ROOM)) {
                   List<DiscreteCoordinates> adjacentBossRooms = findNearbyRooms(map,new DiscreteCoordinates(i,j),MapState.CREATED);
                   setUpBossConnector(adjacentBossRooms);
                }
                if (map[i][j].equals(MapState.FORGERON_ROOM)) {
                    System.out.println("setting");
                    List<DiscreteCoordinates> adjacentForgeronRooms = findNearbyRooms(map,new DiscreteCoordinates(i,j),MapState.CREATED);
                    setUpForgeronConnector(adjacentForgeronRooms);
                }
            }
        }
    }

    protected void setUpBossConnector(List<DiscreteCoordinates> coords) {
        //TODO compléter dans les méthodes des sous-classes
    }

    protected void setUpLevelConnector(MapState[][] roomsPlacement, ICRogueRoom room) {
        //TODO C'est vide
    }

    protected void setUpForgeronConnector(List<DiscreteCoordinates> coords) {
        //TODO compléter dans les méthodes des sous-classes
    }

    public static Level0Room.Level0Connectors findRelativeConnectorPos(DiscreteCoordinates baseRoom, DiscreteCoordinates otherRoom) {
        System.out.println(otherRoom.x + " " + otherRoom.y);
        if (baseRoom.x < otherRoom.x) {
            return Level0Room.Level0Connectors.E;
        } else if (baseRoom.x == otherRoom.x){
            if (baseRoom.y > otherRoom.y) { //start ing
                return Level0Room.Level0Connectors.N;
            } else {
                return Level0Room.Level0Connectors.S;
            }
        } else {
            return Level0Room.Level0Connectors.W;
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
        FORGERON_ROOM, //
        CREATED; // The room has been instantiated in the room map
        @Override
        public String toString() {
            return Integer.toString(ordinal());
        }
    }
}
