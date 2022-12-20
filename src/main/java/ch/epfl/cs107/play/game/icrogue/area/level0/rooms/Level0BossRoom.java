package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.DarkLord;
import ch.epfl.cs107.play.game.icrogue.actor.items.Cherry;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level0BossRoom extends Level0Room{
    private DiscreteCoordinates bossSpawnCoordinates;
    private Orientation roomOrientation;
    public Level0BossRoom(DiscreteCoordinates roomCoords, DiscreteCoordinates bossSpawnCoordinates){
        super(roomCoords);
        this.bossSpawnCoordinates = bossSpawnCoordinates;
    }
    public void setRoomOrientation(Orientation roomOrientation){
        this.roomOrientation = roomOrientation;
        if (roomOrientation.equals(Orientation.UP)){
            setSpawnCoordinates(new DiscreteCoordinates(5,1));
        }else if (roomOrientation.equals(Orientation.DOWN)){
            setSpawnCoordinates(new DiscreteCoordinates(5,8));
        }else if (roomOrientation.equals(Orientation.LEFT)){
            setSpawnCoordinates(new DiscreteCoordinates(8,5));
        }else {
            setSpawnCoordinates(new DiscreteCoordinates(1,5));
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
