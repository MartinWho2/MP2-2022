package ch.epfl.cs107.play.game.icrogue;

import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.area.Level;
import ch.epfl.cs107.play.game.icrogue.area.level0.Level0;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

public class ICRogue extends AreaGame{
    public final static float CAMERA_SCALE_FACTOR = 11.f;
    Level level0;
    private ICRoguePlayer player;
    private ICRogueRoom currentArea;

    /**
     * Add all the areas
     */

    private void initLevel(){
        level0 = new Level0(this);
        DiscreteCoordinates coords = currentArea.getPlayerSpawnPosition();
        player = new ICRoguePlayer(currentArea, Orientation.DOWN, coords);
        player.enterArea(currentArea,new DiscreteCoordinates(5,5));

    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            initLevel();
            return true;
        }
        return false;
    }
    public void setCurrentAreaOfLevel(ICRogueRoom currentArea){
        this.currentArea = currentArea;
        setCurrentArea(currentArea.getTitle(),!currentArea.getHasPlayerEntered());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Keyboard keyboard = currentArea.getKeyboard();
        if (keyboard.get(Keyboard.R).isPressed()){
            initLevel();
        }
        if (player.getIsChangingRoom()){
            switchRoom();
        }
        if (player.isWeak()) {
            System.out.println("Game over");
            initLevel();
        }
        if (level0.isOn()) {
            System.out.println("Win");
            end();
        }

    }

    @Override
    public void end() {
        getWindow().dispose();
        System.exit(0);
    }

    @Override
    public String getTitle() {
        return "ICRogue";
    }


    public void switchRoom() {
            String dest = player.getCurrentConnector().getDestinationRoom();

            int x = Integer.parseInt("" + dest.charAt(dest.length()-2));
            int y = Integer.parseInt("" + dest.charAt(dest.length()-1));
            DiscreteCoordinates newRoom = new DiscreteCoordinates(x,y);
            DiscreteCoordinates spawnPos  =  player.getCurrentConnector().getDestinationCoord();

            player.leaveArea();
            level0.setCurrentRoom(this, newRoom);

            player.enterArea(getCurrentArea(), spawnPos);
            player.setChangingRoom(false);
    }
    /*
    protected void switchArea() {
        player.leaveArea();
        //areaIndex = 0;
        // ICRogueRoom currentArea = (ICRogueRoom) setCurrentArea(areas[areaIndex], false);
        player.enterArea(currentArea, currentArea.getPlayerSpawnPosition());

        player.strengthen();
    }
    */

}

