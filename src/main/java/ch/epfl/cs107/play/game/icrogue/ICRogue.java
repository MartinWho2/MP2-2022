package ch.epfl.cs107.play.game.icrogue;

import ch.epfl.cs107.play.game.PauseMenu;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.area.Level;
import ch.epfl.cs107.play.game.icrogue.area.level0.Level0;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.io.XMLTexts;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

import java.awt.*;

public class ICRogue extends AreaGame{
    public final static float CAMERA_SCALE_FACTOR = 11.f;
    Level level0;
    private ICRoguePlayer player;
    private ICRogueRoom currentArea;

    private TextGraphics winMessage = new TextGraphics("GAGNÉ!",2, Color.GREEN);
    private TextGraphics loseMessage = new TextGraphics("PERDU!",2, Color.RED);


    /**
     * Creates the level and the player and puts it into the level
     */
    private void initLevel(){
        requestResume();
        level0 = new Level0(true);
        level0.registerAreas(this);
        DiscreteCoordinates coords = currentArea.getPlayerSpawnPosition();
        player = new ICRoguePlayer(currentArea, Orientation.DOWN, coords);
        player.enterArea(currentArea,new DiscreteCoordinates(5,5));
        player.speak(XMLTexts.getText("text-player-start"), false);
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            // inits the game
            XMLTexts.initialize(fileSystem, "strings/GameTexts.xml");
            initLevel();
            return true;
        }
        return false;
    }

    /**
     * Sets the currentArea
     * @param currentArea (ICRogueRoom): The new current area
     */
    public void setCurrentAreaOfLevel(ICRogueRoom currentArea){
        this.currentArea = currentArea;
        setCurrentArea(currentArea.getTitle(),!currentArea.getHasPlayerEntered());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        setPauseMenu(new PauseMenu() {
            @Override
            protected void drawMenu(Canvas c) {

            }
        });
        Keyboard keyboard = currentArea.getKeyboard();
        // If R is pressed, restarts a new level
        if (keyboard.get(Keyboard.R).isPressed()){
            initLevel();
        }
        // Switches room if the player is changing of room
        if (player.getIsChangingRoom()){
            switchRoom();

        }
        // Restarts a new level if the player is dead
        if (player.isWeak()) {
            loseMessage.setAnchor(new Vector(getWindow().getScaledWidth()/3.25f,getWindow().getScaledHeight()/2));
            loseMessage.draw(getWindow());
            requestPause();
        }
        // Wins the game
        if (level0.isOn()) {
            System.out.println("Win");
            // winMessage.setAnchor(new Vector(getWindow().getScaledWidth()/3,getWindow().getScaledHeight()/2));
            // winMessage.draw(getWindow());
            // requestPause();
        }

    }

    /**
     * Closes the window and the program
     */
    @Override
    public void end() {
        getWindow().dispose();
        System.exit(0);
    }

    @Override
    public String getTitle() {
        return "ICRogue";
    }

    /**
     * Switches the room to the new one and makes all important changes
     */
    public void switchRoom() {
        // Gets the new room name and spawn position
        String dest = player.getNewRoomName();
        DiscreteCoordinates spawnPos  =  player.getNewSpawnPosition();
        // Parse the x and y values from the new room title
        int x = Integer.parseInt("" + dest.charAt(dest.length()-2));
        int y = Integer.parseInt("" + dest.charAt(dest.length()-1));
        DiscreteCoordinates newRoom = new DiscreteCoordinates(x,y);
        player.leaveArea();
        level0.setCurrentRoom(this, newRoom);

        player.enterArea(getCurrentArea(), spawnPos);
        player.setChangingRoom(false);
    }
}

