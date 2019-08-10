/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.fonts.TextInputUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SharedConstants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiTextEntry
 * Created by HellFirePvP
 * Date: 15.07.2018 / 14:49
 */
public class ScreenTextEntry {

    private String text = "";
    private Runnable changeCallback = null;

    private final TextInputUtil inputUtil;

    public ScreenTextEntry() {
        inputUtil = new TextInputUtil(Minecraft.getInstance(),
                this::getText,
                this::setText,
                256);
    }

    public void setChangeCallback(Runnable changeCallback) {
        this.changeCallback = changeCallback;
    }

    public void setText(@Nullable String newText) {
        if (newText == null) {
            newText = "";
        }
        String prevText = this.text;
        this.text = newText;
        if (!newText.equals(prevText) && changeCallback != null) {
            changeCallback.run();
        }
    }

    @Nonnull
    public String getText() {
        return text;
    }

    public boolean keyTyped(int key) {
        //Escape & Return & Numpad Enter
        if (key == 256 || key == 257 || key == 335) {
            return false;
        }
        //Arrow keys
        if (key >= 262 && key <= 265) {
            return false;
        }
        return this.inputUtil.func_216897_a(key);
    }

    public boolean charTyped(char charCode) {
        return this.inputUtil.func_216894_a(charCode);
    }
}
