
package Java.ErfolgsRechner.service;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FontService {

    public void loadNotoSansFont() {
        try {
            File regularFile = new File("assets/NotoSans-Regular.ttf");
            if (!regularFile.exists()) {
                regularFile = new File("../assets/NotoSans-Regular.ttf");
            }

            if (!regularFile.exists()) {
                System.out.println("Noto Sans nicht gefunden: " + regularFile.getPath());
                return;
            }

            Font regular = Font.createFont(Font.TRUETYPE_FONT, regularFile);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(regular);

            Font uiFont = regular.deriveFont(14f);

            UIManager.put("Label.font", uiFont);
            UIManager.put("Button.font", uiFont);
            UIManager.put("Menu.font", uiFont);
            UIManager.put("MenuItem.font", uiFont);
            UIManager.put("TextField.font", uiFont);
            UIManager.put("Slider.font", uiFont);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
