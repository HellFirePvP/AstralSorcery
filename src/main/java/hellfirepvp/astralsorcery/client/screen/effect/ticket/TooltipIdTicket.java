/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.effect.ticket;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.FXRenderOffsetFunction;
import hellfirepvp.astralsorcery.client.screen.effect.ScreenEffectContainer;
import hellfirepvp.astralsorcery.client.screen.effect.ScreenEffectTicket;
import hellfirepvp.astralsorcery.client.screen.effect.ScreenEffectTicketManager;

import java.util.Objects;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TooltipIdTicket
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TooltipIdTicket extends ScreenEffectTicket<TooltipIdTicket.Container, TooltipIdTicket> {

    private final UUID uuid;

    public TooltipIdTicket(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public Container createEffectContainer() {
        return new Container(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TooltipIdTicket that = (TooltipIdTicket) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }

    public static class Container extends ScreenEffectContainer<Container, TooltipIdTicket> {

        private int tooltipX, tooltipY;

        public Container(TooltipIdTicket ticket) {
            super(ticket);
        }

        public void setTooltipPosition(int x, int y) {
            this.tooltipX = x;
            this.tooltipY = y;
        }

        public int getTooltipX() {
            return this.tooltipX;
        }

        public int getTooltipY() {
            return this.tooltipY;
        }

        public FXRenderOffsetFunction<?> createRenderOffset() {
            return (FXRenderOffsetFunction<EntityVisualFX>) (fx, renderPosition, pTicks) ->
                    renderPosition.add(this.getTooltipX(), this.getTooltipY(), 0);
        }
    }
}
