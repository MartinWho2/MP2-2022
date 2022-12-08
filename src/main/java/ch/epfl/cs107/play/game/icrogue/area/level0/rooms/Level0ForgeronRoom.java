package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.items.Cherry;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level0ForgeronRoom extends Level0ItemRoom{
    public Level0ForgeronRoom(DiscreteCoordinates roomCoords){
        super(roomCoords);
    }

    @Override
    protected void createArea() {
        super.createArea();
        for (int i = 0; i < 4; i++) {
            items.add(new Cherry(this, Orientation.DOWN, new DiscreteCoordinates(1,i+1)));
        }
    }
}
