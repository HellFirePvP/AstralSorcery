/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.effect;

import hellfirepvp.astralsorcery.client.effect.EntityFX;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ScreenEffectContainer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ScreenEffectContainer<C extends ScreenEffectContainer<C, T>, T extends ScreenEffectTicket<C, T>> {

    private final Map<ScreenEffectTemplate<?>, List<EntityVisualScreenFX>> effects = new HashMap<>();
    private final T ticket;

    public ScreenEffectContainer(T ticket) {
        this.ticket = ticket;
    }

    public final T getTicket() {
        return this.ticket;
    }

    public boolean canAddEffects() {
        return ScreenEffectTicketManager.getInstance().canAddEffects(this.getTicket());
    }

    public void tick() {
        this.effects.values().forEach(effects -> {
            effects.removeIf(fx -> {
                fx.tick();
                if (fx.canRemove() || fx.isRemovalRequested()) {
                    fx.setRemoved();
                    return true;
                }
                return false;
            });
        });
    }

    public void clear() {
        this.effects.values().forEach(effects -> effects.forEach(EntityFX::setRemoved));
        this.effects.clear();
    }

    public void renderAll(GuiGraphics graphics, float pTicks) {
        if (AssetLibrary.isReloading()) {
            return;
        }

        for (ScreenEffectTemplate template : this.effects.keySet()) {
            template.renderAll(this.effects.get(template), graphics, pTicks);
        }
        graphics.flush();
    }

    public <E extends EntityVisualScreenFX> E createParticle(ScreenEffectTemplate<E> ctx, double x, double y) {
        E fx = ctx.createParticle(this.ticket, x, y);
        this.effects.computeIfAbsent(ctx, t -> new ArrayList<>()).add(fx);
        fx.setActive();
        return fx;
    }
}
