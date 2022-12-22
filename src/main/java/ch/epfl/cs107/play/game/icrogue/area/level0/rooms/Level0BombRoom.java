package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.RandomHelper;
import ch.epfl.cs107.play.game.icrogue.actor.items.Bomb;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level0BombRoom extends Level0ItemRoom {

    /**
     * Init super
     * @param roomCoordinates (DiscreteCoordinates): room coordinates on the roomMap
     */
    public Level0BombRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);
    }

    @Override
    public boolean getHasPlayerEntered() {
        // Permet de reload la salle si jamais la bombe a été utilisée bêtement
        return false;
    }

    @Override
    protected void createArea() {
        super.createArea();
        // add a bomb to a random tile with 2<=x<=7 and 2<=y<=7
        DiscreteCoordinates bombSpawnPos = new DiscreteCoordinates(RandomHelper.enemyGenerator.nextInt(2, 8), RandomHelper.enemyGenerator.nextInt(2,8));
        items.add(new Bomb(this, Orientation.DOWN, bombSpawnPos));
    }
}
