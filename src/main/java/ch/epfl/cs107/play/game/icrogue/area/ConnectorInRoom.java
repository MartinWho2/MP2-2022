package ch.epfl.cs107.play.game.icrogue.area;

import ch.epfl.cs107.play.math.DiscreteCoordinates;

public interface ConnectorInRoom {
    int getIndex();
    DiscreteCoordinates getDestination();
}