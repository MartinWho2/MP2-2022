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
    private final static int BOSS_KEY_ID = 5;
    private int NB_ROOMS;
    private int levelNumber = 0;

    /**
     * Sets a value of the mpa to a given room
     * @param coordinates (DiscreteCoordinates): The coordinates of the room
     * @param room (ICRogueRoom): The room to put
     */
    protected void setRoom(DiscreteCoordinates coordinates, ICRogueRoom room){
        wholeMap[coordinates.x][coordinates.y] = room;

    }

    /**
     * Sets a room destination to a given connector
     * @param coords (DiscreteCoordinates): Coordinates of the room
     * @param destination (String): The name of the new room
     * @param connector (ConnectorInRoom): The right orientated connector
     */
    protected void setRoomConnectorDestination(DiscreteCoordinates coords, String destination,
                                            ConnectorInRoom connector){
        wholeMap[coords.x][coords.y].setConnectorDestination(connector.getIndex(),destination);

    }

    /**
     * Sets the destination room of a connector and closes it
     * @param coords (DiscreteCoordinates): Coordinates of the room
     * @param destination (String): The name of the new room
     * @param connector (ConnectorInRoom): The right orientated connector
     */
    protected void setRoomConnector(DiscreteCoordinates coords, String destination,
                                 ConnectorInRoom connector){
        setRoomConnectorDestination(coords, destination, connector);
        wholeMap[coords.x][coords.y].setConnectorClosed(connector.getIndex());
    }
    /**
     * Sets the destination room of a connector and opens it
     * @param coords (DiscreteCoordinates): Coordinates of the room
     * @param destination (String): The name of the new room
     * @param connector (ConnectorInRoom): The right orientated connector
     */
    protected void setRoomConnectorOpen(DiscreteCoordinates coords, String destination, ConnectorInRoom connector) {
        setRoomConnectorDestination(coords, destination, connector);
        wholeMap[coords.x][coords.y].setConnectorOpen(connector.getIndex());
    }

    /**
     * Locks a certain connector
     * @param coords (DiscreteCoordinates): Coordinates of the room
     * @param connector (ConnectorInRoom): The right orientated connector
     * @param keyId (int): The ID of the key used to unlock the door
     */
    protected void lockRoomConnector(DiscreteCoordinates coords, ConnectorInRoom connector, int keyId){
        wholeMap[coords.x][coords.y].setConnectorLocked(connector.getIndex(), keyId);
    }

    /**
     * Sets the value firstRoomName to level<nb><x><y>
     * @param coordinates (DiscreteCoordinates): coordinates of the room
     */
    protected void setFirstRoomName(DiscreteCoordinates coordinates){
        firstRoomName = "icrogue/level"+levelNumber+ "" + coordinates.x+""+coordinates.y;
    }

    /**
     * Sets the current area of the game to the one corresponding at the coordinates coords in the map
     * @param a (ICRogue): not null
     * @param coords (DiscreteCoordinates): Coordinates of the room
     */
    public void setCurrentRoom(ICRogue a, DiscreteCoordinates coords) {
        a.setCurrentAreaOfLevel(wholeMap[coords.x][coords.y]);
    }

    /**
     * Generates a random placement for the rooms
     * @return a 2-dim array of MapState
     */
    protected MapState[][] generateRandomRoomPlacement(){
        // Create the map and inits all rooms
        MapState[][] map = new MapState[WIDTH][HEIGHT];
        int roomsToPlace = NB_ROOMS;
        ArrayList<DiscreteCoordinates> placedRooms = new ArrayList<>();
        for (MapState[] mapStates : map) {
            Arrays.fill(mapStates, MapState.NULL);
        }
        // Places the room in the center
        map[WIDTH/2][HEIGHT/2] = MapState.PLACED;
        placedRooms.add(new DiscreteCoordinates(WIDTH/2,HEIGHT/2));
        DiscreteCoordinates currentRoom;
        roomsToPlace--;
        // Places rooms until there are no more rooms to be placed
        while (roomsToPlace > 0) {
            currentRoom = placedRooms.get(0);
            List<Integer> freeSlots = new ArrayList<>();
            // Checks every four rooms around
            // If they are NULL, adds their indices to the freeSlots list
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
            // Choose a random number >= 1 of them to place on the map
            int maxRoomsToAdd = Math.min(freeSlots.size(), roomsToPlace);
            List<Integer> newRooms = RandomHelper.chooseKInList(RandomHelper.roomGenerator.nextInt(1, maxRoomsToAdd+1), freeSlots);
            roomsToPlace = initNewRooms(map,currentRoom,newRooms, placedRooms, roomsToPlace);
        }
        // Find the places for the special rooms
        List<DiscreteCoordinates> possibleRooms = findPlaceForSpecialRooms(map);
        findPlaceForBoss(map, possibleRooms);
        findPLaceForForgeron(map, possibleRooms);
        //printMap(map);
        return map;
    }

    /**
     * This method is made to check if a DiscreteCoordinates with the same  and y is already in the list
     * @param coords (List<DiscreteCoordinates>): The list of coordinates
     * @param currentCoord (DiscreteCoordinates): The current coordinates
     * @return true if it is not already in the list
     */
    private boolean checkIfDuplicate(List<DiscreteCoordinates> coords, DiscreteCoordinates currentCoord){
        for (DiscreteCoordinates coord: coords) {
            if ((currentCoord.x == coord.x) && (currentCoord.y == coord.y)){
                return false;
            }
        }
        return true;
    }

    /**
     * Loops through the map and find all NULL rooms that are nearby a PLACED or EXPLORED room ad
     * adds them to a list
     * @param map (MapState[][]): not null
     * @return A list containing all possible places
     */
    public List<DiscreteCoordinates> findPlaceForSpecialRooms(MapState[][] map) {
        List<DiscreteCoordinates> possibleRooms = new ArrayList<>();
        for (int x = 0; x < map.length; x++) {
            for(int y = 0; y < map[x].length; y++){
                if (map[x][y].equals(MapState.PLACED)||map[x][y].equals(MapState.EXPLORED)){
                    // Looks for NULL rooms nearby a PLACED or EXPLORED room
                    List<DiscreteCoordinates> nearbyRooms = findNearbyRooms(map,new DiscreteCoordinates(x, y),MapState.NULL);
                    for (DiscreteCoordinates roomCoord: nearbyRooms) {
                        // For each room found, check if it not already in the list
                        if (checkIfDuplicate(possibleRooms, roomCoord)){
                            possibleRooms.add(roomCoord);
                        }
                    }
                }
            }
        }
        return possibleRooms;
    }
    /**
     * Chooses a room for the boss in the possibilities
     * @param map (MapState[][]): Map of rooms
     * @param possibleRooms (List<DiscreteCoordinates>): possible rooms for the boss
     */
    private void findPlaceForBoss(MapState[][] map, List<DiscreteCoordinates> possibleRooms){
        int index = RandomHelper.roomGenerator.nextInt(0,possibleRooms.size());
        map[possibleRooms.get(index).x][possibleRooms.get(index).y] = MapState.BOSS_ROOM;
        bossCoordinates = possibleRooms.get(index);
        possibleRooms.remove(bossCoordinates);
    }
    /**
     * Chooses a room for the forgeron in the possibilities
     * @param map (MapState[][]):
     * @param possibleRooms (List<DiscreteCoordinates>): possible rooms for the forgeron
     */
    private void findPLaceForForgeron(MapState[][] map, List<DiscreteCoordinates> possibleRooms) {
        int index = RandomHelper.roomGenerator.nextInt(0,possibleRooms.size());
        map[possibleRooms.get(index).x][possibleRooms.get(index).y] = MapState.FORGERON_ROOM;
        forgeronCoordinates = possibleRooms.get(index);
        possibleRooms.remove(index);
    }

    /**
     * Inits the rooms around a given current room
     * @param map (MapState[][]): Map of rooms
     * @param currentRoom (DiscreteCoordinates): Coordinates of the current room
     * @param newRooms (List<Integer>): The indices corresponding to the positions of the rooms to add
     * @param placedRooms (List<DiscreteCoordinates>):
     * @param roomsToPlace (int): number of rooms that still need to be placed
     * @return The numbers of room that still need to be placed after the end of the method
     */
    private int initNewRooms(MapState[][] map, DiscreteCoordinates currentRoom,List<Integer> newRooms,
                              List<DiscreteCoordinates> placedRooms, int roomsToPlace){
        for (Integer room : newRooms) {
            // Iterate through every room and places it
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
            // Decrease the counter by 1
            roomsToPlace--;
        }
        // Remove the current room from the placed rooms
        placedRooms.remove(0);
        map[currentRoom.x][currentRoom.y] = MapState.EXPLORED;
        return roomsToPlace;
    }

    /**
     * Prints the map (given in the pdf)
     * @param map (MapState[][]): the map to print
     */
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

    /**
     * Register every room of the level into the ICRogue game
     * @param a (ICRogue): not null
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

    /**
     *
     * @param map (MapState[][]): not null
     * @param currentRoom (DiscreteCoordinates): the room to look around
     * @param state (MapState): The state to look for
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

    /**
     * Basic Constructor of Level
     * @param randomMap (boolean): true if the map needs to be random
     * @param startPosition (DiscreteCoordinates): The start position room of the player
     * @param roomsDistribution (int[]): The distribution of the rooms
     * @param width (int): width of the map
     * @param height (int): height of the map
     */
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
            bossCoordinates = new DiscreteCoordinates(0,0);
            NB_ROOMS = 5;
            generateFinalMap();
        }
    }
    public abstract void generateFinalMap();

    /**
     * Main method to generate the map
     * @param roomsDistribution (int[]): Distribution of the rooms
     */
    protected void generateRandomMap(int[] roomsDistribution){
        WIDTH = NB_ROOMS;
        HEIGHT = NB_ROOMS;
        wholeMap = new ICRogueRoom[WIDTH][HEIGHT];
        //Generates first the random placement of any room
        List<Integer> indexesOfRoomsCoordinates = new ArrayList<>();
        List<Integer> chosenRooms;
        MapState[][] mapRooms = generateRandomRoomPlacement();
        List<DiscreteCoordinates> roomsCoordinates  = getCoordinatesOfRooms(mapRooms);
        for (int i=0; i< roomsCoordinates.size();i++){
            indexesOfRoomsCoordinates.add(i);
        }
        // Iterate through every type of room
        for (int indexOfRoom=0; indexOfRoom<roomsDistribution.length;indexOfRoom++){
            int nbOfRoomOfType = roomsDistribution[indexOfRoom];
            System.out.println("there are "+nbOfRoomOfType+ " rooms of type "+indexOfRoom);
            // Choose random available rooms and assigns them to the current room type
            chosenRooms = RandomHelper.chooseKInList(nbOfRoomOfType,indexesOfRoomsCoordinates);
            for (Integer chosenRoom : chosenRooms) {
                DiscreteCoordinates roomCoord = roomsCoordinates.get(chosenRoom);
                createRoomOfType(indexOfRoom, roomCoord);
                indexesOfRoomsCoordinates.remove(chosenRoom);
                mapRooms[roomCoord.x][roomCoord.y] = MapState.CREATED;
            }
        }
        // Generate the special rooms
        generateBossRoom();
        generateForgeronRoom();
        generateConnectors(mapRooms);
    }

    /**
     * generateBossRoom must be overridden in child classes
     */
    abstract protected void generateBossRoom();

    /**
     * generateForgeronRoom must be overridden in child classes
     */
    abstract protected void generateForgeronRoom();

    /**
     * CreateRoomOfType must be overridden in child classes
     * @param nbOfRoomType (int): number that define a certain room type
     * @param roomCoord (DiscreteCoordinates): coordinate of the room
     */
    abstract protected void createRoomOfType(int nbOfRoomType, DiscreteCoordinates roomCoord);

    /**
     * Generate the connectors in each room
     * @param map (MapState): the MapState
     */
    private void generateConnectors(MapState[][] map){
        printMap(map);
        // loop through all element of map
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                // if the room is of type "CREATED" generate de connecotrs
                if (wholeMap[i][j] != null && map[i][j].equals(MapState.CREATED)) {
                    setUpLevelConnector(map, wholeMap[i][j]);
                }
                // if the map is a boss room, generate de connectors of the room
                if (map[i][j].equals(MapState.BOSS_ROOM)) {
                   List<DiscreteCoordinates> adjacentBossRooms = findNearbyRooms(map,new DiscreteCoordinates(i,j),MapState.CREATED);
                   setUpBossConnector(adjacentBossRooms);
                }
                // if map is a forgeron room, set the forgeron room's connector
                if (map[i][j].equals(MapState.FORGERON_ROOM)) {
                    List<DiscreteCoordinates> adjacentForgeronRooms = findNearbyRooms(map,new DiscreteCoordinates(i,j),MapState.CREATED);
                    setUpForgeronConnector(adjacentForgeronRooms);
                }
            }
        }
    }

    abstract protected void setUpBossConnector(List<DiscreteCoordinates> coords);

    abstract protected void setUpLevelConnector(MapState[][] roomsPlacement, ICRogueRoom room);

    abstract protected void setUpForgeronConnector(List<DiscreteCoordinates> coords);

    /**
     * Return the connector that connects a baseRoom to anotherRoom
     * @param baseRoom (DiscreteCoordinates): coordinates of the room on the roomMap
     * @param otherRoom (DiscreteCoordinates): coordinates of the room on the roomMap
     * @return (Level0Room.Level0Connectors): the connector that needs to be placed in the baseRoom
     */
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


    /**
     * Get the coordinates of all room that are PLACED or EXPLORED
     * @param mapRooms (MapState): map state
     * @return (List<DiscreteCoordinates>): list of all rooms on the map
     */
    private List<DiscreteCoordinates> getCoordinatesOfRooms(MapState[][] mapRooms) {
        List<DiscreteCoordinates> roomsCoordinate = new ArrayList<>();
        for (int i = 0; i < mapRooms.length; i++) {
            for (int j = 0; j < mapRooms[i].length; j++) {
                // get all rooms on the map if they are placed or explored
                if (mapRooms[i][j].equals(MapState.PLACED) || mapRooms[i][j].equals(MapState.EXPLORED)){
                    roomsCoordinate.add(new DiscreteCoordinates(i,j));
                }
            }
        }
        return roomsCoordinate;
    }

    @Override
    public boolean isOn() {
        if (wholeMap[bossCoordinates.x][bossCoordinates.y] != null) return wholeMap[bossCoordinates.x][bossCoordinates.y].getChallengeSucceeded();
        return false;
    }

    @Override
    public boolean isOff() {
        if (wholeMap[bossCoordinates.x][bossCoordinates.y] != null) return !wholeMap[bossCoordinates.x][bossCoordinates.y].getChallengeSucceeded();
        return true;
    }

    @Override
    public float getIntensity() {
        if (wholeMap[bossCoordinates.x][bossCoordinates.y] != null) return wholeMap[bossCoordinates.x][bossCoordinates.y].getChallengeSucceeded()? 1.f : 0.f;
        return 0.f;
    }

    /**
     * Enumerate all possible MapState
     */
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
