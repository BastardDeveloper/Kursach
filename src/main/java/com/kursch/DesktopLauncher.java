package com.kursch;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        config.setTitle("My Game");
        config.setWindowedMode(1300, 800);
        config.setWindowIcon(Files.FileType.Internal, "SpriteSheet2_Enemies.png");

        new Lwjgl3Application(new Main(), config);
    }
}
