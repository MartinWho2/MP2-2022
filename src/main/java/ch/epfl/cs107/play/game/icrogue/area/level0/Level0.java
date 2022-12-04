package ch.epfl.cs107.play.game.icrogue.area.level0;

import ch.epfl.cs107.play.game.icrogue.ICRogue;
import ch.epfl.cs107.play.game.icrogue.area.Level;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0KeyRoom;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0Room;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0StaffRoom;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0TurretRoom;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level0 extends Level {
    private final int PART_1_KEY_ID = 1;
    private final int BOSS_KEY_ID = 2;
    public Level0(ICRogue game){
        super(new DiscreteCoordinates(2,2),new DiscreteCoordinates(4,2));
        setFirstRoomName(new DiscreteCoordinates(1,0));
        //generateFixedMap(2);
        generateFinalMap();
        registerAreas(game);
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



}
