package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Turret;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Level0TurretRoom extends Level0EnemyRoom{
    public Level0TurretRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);

    }

    @Override
    protected void createArea() {
        System.out.println("created turrets");
        super.createArea();
        Turret turret1 = new Turret(this, new ArrayList<Orientation>(Arrays.asList(Orientation.DOWN, Orientation.RIGHT)), new DiscreteCoordinates(1, 8));
        Turret turret2 = new Turret(this, new ArrayList<Orientation>(Arrays.asList(Orientation.UP, Orientation.LEFT)), new DiscreteCoordinates(8, 1));
        enemies.add(turret1);
        enemies.add(turret2);
    }
}
