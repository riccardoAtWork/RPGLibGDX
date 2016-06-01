package com.fire.rpg;


import com.badlogic.gdx.Game;
import com.fire.rpg.domain.screens.MainGameScreen;
//import com.fire.rpg.screens.MainGameScreen;

public class BludBourne extends Game{
    public static final MainGameScreen mainScreen = new MainGameScreen();

    @Override
    public void create() {
       setScreen(mainScreen);
    }

    @Override
    public void dispose() {
        mainScreen.dispose();
    }
}
