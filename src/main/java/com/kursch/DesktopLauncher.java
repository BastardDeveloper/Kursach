package com.kursch;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.addIcon("SpriteSheet2_Enemies.png", Files.FileType.Internal);
        config.title = "My Game";
        config.width = 1300;
        config.height = 800;

        new LwjglApplication(new Main(), config);
    }
}
