/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.client.Minecraft;
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

    public void textboxKeyTyped(char typedChar, int keyCode) {
        if (Screen.isCopy(keyCode)) {
            Minecraft.getInstance().keyboardListener.setClipboardString(this.getText());
        } else if (Screen.isPaste(keyCode)) {
            this.setText(Minecraft.getInstance().keyboardListener.getClipboardString());
        } else if (Screen.isCut(keyCode)) {
            Minecraft.getInstance().keyboardListener.setClipboardString(this.getText());
            this.setText("");
        } else {
            String text = this.getText();
            //Keyboard.KEY_BACK: uh....
            if (keyCode == 259) {
                this.setText(text.length() > 1 ? text.substring(0, text.length() - 1) : "");
            } else {
                if (SharedConstants.isAllowedCharacter(typedChar)) {
                    this.setText(text + String.valueOf(typedChar));
                }
            }
        }
    }
}
