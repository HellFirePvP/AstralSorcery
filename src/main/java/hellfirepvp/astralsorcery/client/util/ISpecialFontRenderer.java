package hellfirepvp.astralsorcery.client.util;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ISpecialFontRenderer
 * Created by HellFirePvP
 * Date: 07.10.2016 / 11:03
 */
public interface ISpecialFontRenderer {

    public double getStringWidth(String str);

    public double drawString(String str, double offsetX, double offsetY, float zLevel, Color c, float alpha);

}
