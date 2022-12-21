package ch.epfl.cs107.play.game.icrogue.area.level0;

import ch.epfl.cs107.play.game.icrogue.RandomHelper;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.area.Level;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.*;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.List;

public class Level0 extends Level {
    private final static int PART_1_KEY_ID = 1;
    public final static int BOSS_KEY_ID = 2;
    public Level0(boolean randomMap){
        super(randomMap,new DiscreteCoordinates(1,0),RoomType.NORMAL.getArrayOfRooms(), 4,2);
    }
    public Level0(){
        this(true);
    }

    /**
     * Setups the connectors of a given room given the rooms around it
     * @param map (MapState[][]): A 2-dim array of MapState
     * @param room (ICRogueRoom): A given room
     */
    protected void setUpLevelConnector(MapState[][] map, ICRogueRoom room){
        List<DiscreteCoordinates> nearbyRooms = findNearbyRooms(map,new DiscreteCoordinates(room.getRoomCoordinates().x,room.getRoomCoordinates().y),MapState.CREATED);
        for (DiscreteCoordinates roomSetting : nearbyRooms){
            String destination = room.getTitle();
            destination = destination.substring(0, destination.length()-2) + roomSetting.x + roomSetting.y;
            setRoomConnector(new DiscreteCoordinates(room.getRoomCoordinates().x, room.getRoomCoordinates().y), destination, findRelativeConnectorPos(room.getRoomCoordinates(), roomSetting));
        }
    }

    /**
     * A basic switch to create a room with indexes corresponding to the ordinal of the enum class
     * @param nbOfRoomType (int): The number corresponding to the index of the room
     * @param roomCoord (DiscreteCoordinates): The coordinates of the given room
     */
    protected void createRoomOfType(int nbOfRoomType, DiscreteCoordinates roomCoord){
        switch (nbOfRoomType){
            case 0 -> setRoom(roomCoord,new Level0TurretRoom(roomCoord));
            case 1-> setRoom(roomCoord, new Level0StaffRoom(roomCoord));
            case 2-> setRoom(roomCoord, new Level0KeyRoom(roomCoord,BOSS_KEY_ID));
            case 3 ->{
                setRoom(roomCoord, new Level0Room(roomCoord));
                setFirstRoomName(roomCoord);
                spawnCoordinates = roomCoord;
            }
            case 4-> setRoom(roomCoord, new Level0Room(roomCoord));
            case 5 -> setRoom(roomCoord, new Level0BombRoom(roomCoord));
            case 6 -> setRoom(roomCoord, new Level0SwordRoom(roomCoord));
            case 7 -> setRoom(roomCoord, new Level0SkeletonRoom(roomCoord));
        }
    }

    /**
     * Sets up the connector of the boss room by choosing randomly one of the rooms
     * @param coords (List<DiscreteCoordinates>): The coordinates of every nearby room not NULL, not null
     */
    @Override
    protected void setUpBossConnector(List<DiscreteCoordinates> coords) {
        // Choose a random element in the list
        int i = RandomHelper.roomGenerator.nextInt(0, coords.size());
        DiscreteCoordinates coord = coords.get(i);
        String destination = wholeMap[bossCoordinates.x][bossCoordinates.y].getTitle();
        Level0Room.Level0Connectors connector = findRelativeConnectorPos(wholeMap[coord.x][coord.y].getRoomCoordinates(), bossCoordinates);
        // Calls a method to set up the boss room in function of the side of the entry
        setRoomConnector(new DiscreteCoordinates(coord.x, coord.y), destination, connector);
        // Locks the connector of the nearby room
        lockRoomConnector(coord, connector, BOSS_KEY_ID);
        setRoomConnector(bossCoordinates, wholeMap[coord.x][coord.y].getTitle(), findRelativeConnectorPos(bossCoordinates, wholeMap[coord.x][coord.y].getRoomCoordinates()));
        ((Level0BossRoom)wholeMap[bossCoordinates.x][bossCoordinates.y]).setRoomOrientation(connector.getOrientation());

    }

    /**
     * Sets up the forgeron room by choosing randomly a room in a given list and creating the connections
     * @param coords (List<DiscreteCoordinates>): All non-NULL nearby rooms, not null
     */
    @Override
    protected void setUpForgeronConnector(List<DiscreteCoordinates> coords) {
        // Choose a random element in the list
        DiscreteCoordinates coord = coords.get(RandomHelper.roomGenerator.nextInt(0, coords.size()));
        String destination = wholeMap[forgeronCoordinates.x][forgeronCoordinates.y].getTitle();
        Level0Room.Level0Connectors connector = findRelativeConnectorPos(wholeMap[coord.x][coord.y].getRoomCoordinates(), forgeronCoordinates);
        setRoomConnector(new DiscreteCoordinates(coord.x, coord.y), destination, connector);wholeMap[coord.x][coord.y].setConnectorsCracked(connector.getIndex());
        setRoomConnector(forgeronCoordinates, wholeMap[coord.x][coord.y].getTitle(), findRelativeConnectorPos(forgeronCoordinates, wholeMap[coord.x][coord.y].getRoomCoordinates()));

    }

    @Override
    protected void generateBossRoom() {
        setRoom(bossCoordinates, new Level0BossRoom(bossCoordinates,new DiscreteCoordinates(3, 5)));
    }

    @Override
    protected void generateForgeronRoom() {
        setRoom(forgeronCoordinates, new Level0ForgeronRoom(forgeronCoordinates));
    }


    public void generateFixedMap(int methodToUse){
        if (methodToUse== 1){
            generateMap1();
        }
        if (methodToUse == 2){
            generateMap2();
        }
    }

    public void generateFinalMap(){
        generateMap1();
    }

    /**
     * generate a map with 2 rooms
     */
    public void generateMap1(){
        DiscreteCoordinates room00 = new DiscreteCoordinates(0, 0);
        setRoom(room00, new Level0KeyRoom(room00, PART_1_KEY_ID));
        setRoomConnector(room00, "icrogue/level010", Level0Room.Level0Connectors.E);
        lockRoomConnector(room00, Level0Room.Level0Connectors.E,  PART_1_KEY_ID);

        DiscreteCoordinates room10 = new DiscreteCoordinates(1, 0);
        setRoom(room10, new Level0Room(room10));
        setRoomConnector(room10, "icrogue/level000", Level0Room.Level0Connectors.W);
    }

    /**
     * generate a map with 4 rooms
     */
    public void generateMap2(){
        DiscreteCoordinates room00 = new DiscreteCoordinates(0, 0);
        setRoom(room00, new Level0TurretRoom(room00));
        setRoomConnector(room00, "icrogue/level010", Level0Room.Level0Connectors.E);

        DiscreteCoordinates room10 = new DiscreteCoordinates(1,0);
        setRoom(room10, new Level0Room(room10));
        setRoomConnectorOpen(room10, "icrogue/level011", Level0Room.Level0Connectors.S);
        setRoomConnector(room10, "icrogue/level020", Level0Room.Level0Connectors.E);

        lockRoomConnector(room10, Level0Room.Level0Connectors.W,  BOSS_KEY_ID);
        setRoomConnectorDestination(room10, "icrogue/level000", Level0Room.Level0Connectors.W);

        DiscreteCoordinates room20 = new DiscreteCoordinates(2,0);
        setRoom(room20,  new Level0StaffRoom(room20));
        setRoomConnector(room20, "icrogue/level010", Level0Room.Level0Connectors.W);
        setRoomConnector(room20, "icrogue/level030", Level0Room.Level0Connectors.E);

        DiscreteCoordinates room30 = new DiscreteCoordinates(3,0);
        setRoom(room30, new Level0KeyRoom(room30, BOSS_KEY_ID));
        setRoomConnector(room30, "icrogue/level020", Level0Room.Level0Connectors.W);

        DiscreteCoordinates room11 = new DiscreteCoordinates(1, 1);
        setRoom (room11, new Level0Room(room11));
        setRoomConnector(room11, "icrogue/level010", Level0Room.Level0Connectors.N);

    }

    /**
     * Enum with all different room types to place
     */
    protected enum RoomType{
        TURRET (2),
        STAFF (1),
        BOSS_KEY (0),
        SPAWN (1),
        NORMAL(1),
        BOMB(1),
        SWORD(1),
        SKELETON(1);
        final int roomType;
        RoomType(int value){
            this.roomType = value;
        }
        public int[] getArrayOfRooms(){
            List<Integer> array = new ArrayList<>();
            for (RoomType roomType : RoomType.values()){
                array.add(roomType.roomType);
            }
            return array.stream().mapToInt(i->i).toArray();
        }
    }
}
