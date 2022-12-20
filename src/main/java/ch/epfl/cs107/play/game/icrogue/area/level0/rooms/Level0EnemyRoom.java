package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.icrogue.actor.enemies.Enemy;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.List;

public class Level0EnemyRoom extends Level0Room {
    protected List<Enemy> enemies;
    /**
     * Init useful attributes
     * @param roomCoordinates (DiscreteCoordinates): room coordinates on the roomMap
     */
    public Level0EnemyRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);
        enemies = new ArrayList<>();
    }

    @Override
    public boolean challengeCompleted() {
        // the challenge is completed
        for (Enemy enemy : enemies) {
            if (enemy.getIsAlive()) return false;
        }
        return true;
    }
}
