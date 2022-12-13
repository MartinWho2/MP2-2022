package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.DarkLord;
import ch.epfl.cs107.play.game.icrogue.actor.items.Cherry;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level0BossRoom extends Level0ItemRoom{
    public Level0BossRoom(DiscreteCoordinates roomCoords){
        super(roomCoords);
    }

    @Override
    protected void createArea() {
        super.createArea();
        Cherry cherry = new Cherry(this, Orientation.DOWN,new DiscreteCoordinates(5,5));
        items.add(cherry);
        DarkLord boss = new DarkLord(this,Orientation.DOWN,new DiscreteCoordinates(3,3));
    }
}
