/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.effect;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ScreenEffectTicket
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ScreenEffectTicket<C extends ScreenEffectContainer<C, T>, T extends ScreenEffectTicket<C, T>> {

    private boolean valid = true;

    void invalidate() {
        this.valid = false;
    }

    void validate() {
        this.valid = true;
    }

    public boolean isValid() {
        return this.valid;
    }

    public abstract C createEffectContainer();
}
