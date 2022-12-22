package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Skeleton;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level0SkeletonRoom extends Level0EnemyRoom{

    /**
     * Init useful attributes
     * @param roomCoordinates (DiscreteCoordinates): room coordinates on the roomMap
     */
    public Level0SkeletonRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);
    }

    @Override
    protected void createArea() {
        super.createArea();
        // Add a skeleton
        enemies.add(new Skeleton(this, Orientation.DOWN, new DiscreteCoordinates(4, 5)));
    }
}
