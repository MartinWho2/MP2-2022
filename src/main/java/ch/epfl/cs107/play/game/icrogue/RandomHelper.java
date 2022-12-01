package ch.epfl.cs107.play.game.icrogue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Exposes multiple random generators for various part of the game
 */
public class RandomHelper {
    private static final int ROOM_SEED = 42;
    public static Random roomGenerator = new Random(ROOM_SEED);

    private static final int ENEMY_SEED = 452;
    public static Random enemyGenerator = new Random(ENEMY_SEED);

    private static final int CHOOSE_SEED = 452;
    private static final Random chooseGenerator = new Random(CHOOSE_SEED);


    /**
     * Chooses k random elements from the given list
     * @param k the number of elements to pick
     * @param list the list of elements
     * @return a list with k randomly chosen elements
     */
    public static List<Integer> chooseKInList(int k, List<Integer> list) {
        if (k > list.size())
            throw new IllegalArgumentException();

        int remainingValues = k;
        List<Integer> values = new ArrayList<>(list), res = new ArrayList<>();

        while (remainingValues > 0) {
            int ix = chooseGenerator.nextInt(values.size());
            res.add(list.get(ix));
            values.remove(ix);
            remainingValues -= 1;
        }

        return res;
    }
}