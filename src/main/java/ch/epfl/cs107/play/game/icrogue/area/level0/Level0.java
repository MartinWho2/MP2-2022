package ch.epfl.cs107.play.game.icrogue.area.level0;

import ch.epfl.cs107.play.game.icrogue.RandomHelper;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.area.Level;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.*;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.List;

public class Level0 extends Level {
    private final int PART_1_KEY_ID = 1;
    private final int BOSS_KEY_ID = 2;
    public Level0(){
        super(new DiscreteCoordinates(2,2),new DiscreteCoordinates(4,2));
        setFirstRoomName(new DiscreteCoordinates(1,0));
        //generateFixedMap(2);
        generateFinalMap();
    }
    public Level0(boolean randomMap){
        super(randomMap,new DiscreteCoordinates(1,0),RoomType.NORMAL.getArrayOfRooms(), 4,2);
    }
    public Level0(int TO_TEST_AND_TO_REMOVE){
        this(true);
    }

    protected void setUpLevelConnector(MapState[][] map, ICRogueRoom room){
        List<DiscreteCoordinates> nearbyRooms = findNearbyRooms(map,new DiscreteCoordinates(room.getRoomCoordinates().x,room.getRoomCoordinates().y),MapState.CREATED);
        for (DiscreteCoordinates roomSetting : nearbyRooms){
            String destination = room.getTitle();

            destination = destination.substring(0, destination.length()-2) + roomSetting.x + roomSetting.y;

            if (room.getRoomCoordinates().x == 4 && room.getRoomCoordinates().y == 4 ) {
                System.out.println(destination);
                System.out.println(findRelativeConnectorPos(room.getRoomCoordinates(), roomSetting));
            }
            // System.out.println(destination);
            setRoomConnector(new DiscreteCoordinates(room.getRoomCoordinates().x, room.getRoomCoordinates().y), destination, findRelativeConnectorPos(room.getRoomCoordinates(), roomSetting));
        }
    }
    protected void createRoomOfType(int nbOfRoomType, DiscreteCoordinates roomCoord){
        switch (nbOfRoomType){
            case 0 -> setRoom(roomCoord,new Level0TurretRoom(roomCoord));
            case 1-> setRoom(roomCoord, new Level0StaffRoom(roomCoord));
            case 2-> setRoom(roomCoord, new Level0KeyRoom(roomCoord,BOSS_KEY_ID));
            case 3 ->{
                setRoom(roomCoord, new Level0Room(roomCoord));
                setFirstRoomName(roomCoord);
                spawnCoordinates = roomCoord;
                System.out.println("this is the Spawn "+spawnCoordinates);
            }
            case 4-> setRoom(roomCoord, new Level0Room(roomCoord));
            case 5 -> setRoom(roomCoord, new Level0BombRoom(roomCoord));
        }
    }

    @Override
    protected void setUpBossConnector(List<DiscreteCoordinates> coords) {
        for (DiscreteCoordinates coord : coords) {
            String destination = wholeMap[bossCoordinates.x][bossCoordinates.y].getTitle();
            Level0Room.Level0Connectors connector = findRelativeConnectorPos(wholeMap[coord.x][coord.y].getRoomCoordinates(), bossCoordinates);
            setRoomConnector(new DiscreteCoordinates(coord.x, coord.y), destination, connector);
            System.out.println("boos connector : " + findRelativeConnectorPos(wholeMap[coord.x][coord.y].getRoomCoordinates(), bossCoordinates));
            lockRoomConnector(coord, connector, BOSS_KEY_ID);
            setRoomConnector(bossCoordinates, wholeMap[coord.x][coord.y].getTitle(), findRelativeConnectorPos(bossCoordinates, wholeMap[coord.x][coord.y].getRoomCoordinates()));
        }
    }

    @Override
    protected void setUpForgeronConnector(List<DiscreteCoordinates> coords) {
        DiscreteCoordinates coord = coords.get(RandomHelper.roomGenerator.nextInt(0, coords.size()));
        String destination = wholeMap[forgeronCoordinates.x][forgeronCoordinates.y].getTitle();
        Level0Room.Level0Connectors connector = findRelativeConnectorPos(wholeMap[coord.x][coord.y].getRoomCoordinates(), forgeronCoordinates);
        setRoomConnector(new DiscreteCoordinates(coord.x, coord.y), destination, connector);
        System.out.println("connector forgeron " + coord);
        wholeMap[coord.x][coord.y].setConnectorsCracked(connector.getIndex());
        setRoomConnector(forgeronCoordinates, wholeMap[coord.x][coord.y].getTitle(), findRelativeConnectorPos(forgeronCoordinates, wholeMap[coord.x][coord.y].getRoomCoordinates()));

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
        generateMap2();
    }


    public void generateMap1(){
        DiscreteCoordinates room00 = new DiscreteCoordinates(0, 0);
        setRoom(room00, new Level0KeyRoom(room00, PART_1_KEY_ID));
        setRoomConnector(room00, "icrogue/level000", Level0Room.Level0Connectors.E);
        lockRoomConnector(room00, Level0Room.Level0Connectors.E,  PART_1_KEY_ID);

        DiscreteCoordinates room10 = new DiscreteCoordinates(1, 0);
        setRoom(room10, new Level0Room(room10));
        setRoomConnector(room10, "icrogue/level010", Level0Room.Level0Connectors.W);
    }
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
    protected enum RoomType{
        TURRET (0),
        STAFF (1),
        BOSS_KEY (1),
        SPAWN (1),
        NORMAL(1),
        BOMB(1);
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
