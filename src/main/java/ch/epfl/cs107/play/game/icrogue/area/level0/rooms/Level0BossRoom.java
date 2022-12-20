package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.DarkLord;
import ch.epfl.cs107.play.game.icrogue.actor.groundElement.Lava;
import ch.epfl.cs107.play.game.icrogue.actor.items.Cherry;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.List;

public class Level0BossRoom extends Level0Room{
    private DiscreteCoordinates bossSpawnCoordinates;
    private Orientation roomOrientation;
    private final List<DiscreteCoordinates> lavaBlocksCoords = new ArrayList<>();
    private DarkLord boss;

    /**
     * Init useful attributes
     * @param roomCoords (DiscreteCoordinates): room coordinates on the roomMap
     * @param bossSpawnCoordinates (DiscreteCoordinates): coordinates of the boss in the room
     */
    public Level0BossRoom(DiscreteCoordinates roomCoords, DiscreteCoordinates bossSpawnCoordinates){
        super(roomCoords);
        this.bossSpawnCoordinates = bossSpawnCoordinates;
    }

    /**
     * Determine in which, axis the boss can move and add lava blocks that obstruct way to the boss
     * @param roomOrientation (Orientation): orientation of the connector of the boss room
     */
    public void setRoomOrientation(Orientation roomOrientation){
        this.roomOrientation = roomOrientation;
        if (roomOrientation.equals(Orientation.UP)){
            setSpawnCoordinates(new DiscreteCoordinates(5,1));
            for (int i = 1;i < 9; ++i) {
                lavaBlocksCoords.add(new DiscreteCoordinates(i, 2));
            }
        }else if (roomOrientation.equals(Orientation.DOWN)){
            setSpawnCoordinates(new DiscreteCoordinates(5,8));
            for (int i = 1;i < 9; ++i) {
                lavaBlocksCoords.add(new DiscreteCoordinates(i, 7));
            }
        }else if (roomOrientation.equals(Orientation.LEFT)){
            setSpawnCoordinates(new DiscreteCoordinates(8,5));
            for (int i = 1;i < 9; ++i) {
                lavaBlocksCoords.add(new DiscreteCoordinates(i, 7));
            }
        }else {
            setSpawnCoordinates(new DiscreteCoordinates(1,5));
            for (int i = 1;i < 9; ++i) {
                lavaBlocksCoords.add(new DiscreteCoordinates(2, i));
            }
        }
    }

     private void setSpawnCoordinates(DiscreteCoordinates coords){
        this.bossSpawnCoordinates = coords;
     }

    @Override
    public boolean challengeCompleted() {
        return !boss.getIsAlive();
    }

    @Override
    protected void createArea() {
        super.createArea();
        // add lava blocks
        for (DiscreteCoordinates coord : lavaBlocksCoords) {
            new Lava(this, coord);
        }
        // add boss
        boss = new DarkLord(this,roomOrientation, bossSpawnCoordinates);
    }
}
