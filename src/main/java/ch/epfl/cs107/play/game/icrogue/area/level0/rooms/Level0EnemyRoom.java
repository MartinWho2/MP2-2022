package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.icrogue.actor.enemies.Enemy;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.List;

public class Level0EnemyRoom extends Level0Room {
    protected List<Enemy> enemies;
    public Level0EnemyRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);
        enemies = new ArrayList<>();
    }

    @Override
    public boolean challengeCompleted() {
        for (Enemy enemy : enemies) {
            if (enemy.getIsAlive()) return false;
        }
        return true;
    }

    @Override
    public void playerEnters() {
        this.hasPlayerEntered = true;
    }
}
