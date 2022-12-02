package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level0Room extends ICRogueRoom {
    private final static String behaviorName = "icrogue/Level0Room";

    @Override
    public String getTitle() {
        return "icrogue/level0"+roomCoordinates.x + ""+roomCoordinates.y;
    }

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(5,5);
    }

    public Level0Room(DiscreteCoordinates roomCoordinates){
        super(behaviorName,roomCoordinates);
    }
    protected void createArea() {
        // Base
        registerActor(new Background(this));
        registerActor(new Foreground(this));
    }
}
