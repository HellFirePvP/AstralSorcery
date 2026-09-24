/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.effect;

import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.effect.EffectTemplate;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphics;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EntityVisualScreenFX
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class EntityVisualScreenFX extends EntityVisualFX {

    private final ScreenEffectTicket<?, ?> ticket;

    protected EntityVisualScreenFX(ScreenEffectTicket<?, ?> ticket, double x, double y) {
        super(new Vector3(x, y, 0));
        this.ticket = ticket;
    }

    @Override
    protected void updateBoundingBox() {}

    @Override
    public boolean canRemove() {
        return !this.ticket.isValid() || super.canRemove();
    }

    @Override
    public void render(EffectTemplate<?> ctx, Camera renderInfo, VertexConsumer vb, float pTicks) {}

    public abstract void render(EffectTemplate<?> ctx, VertexConsumer buf, GuiGraphics graphics, float pTicks);
}
