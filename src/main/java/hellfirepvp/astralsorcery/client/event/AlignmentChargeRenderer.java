/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.draw.TextureHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.item.base.AlignmentChargeRevealer;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import org.lwjgl.opengl.GL11;

import java.util.EnumSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AlignmentChargeRenderer
 * Created by HellFirePvP
 * Date: 02.03.2020 / 21:44
 */
public class AlignmentChargeRenderer implements ITickHandler {

    public static final AlignmentChargeRenderer INSTANCE = new AlignmentChargeRenderer();

    private static final int fadeTicks = 15;
    private static final float visibilityChange = 1F / ((float) fadeTicks);

    private int revealTicks = 0;
    private float alphaReveal = 0F;

    private AlignmentChargeRenderer() {}

    public void attachEventListeners(IEventBus bus) {
        bus.addListener(EventPriority.HIGH, this::onRenderOverlay);
    }

    private void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        if (this.alphaReveal <= 0) {
            return;
        }

        MainWindow window = Minecraft.getInstance().mainWindow;
        int screenWidth = window.getScaledWidth();
        int screenHeight = window.getScaledHeight();
        int barWidth = 194;
        int offsetLeft = screenWidth / 2 - barWidth / 2;
        int offsetTop = screenHeight + 3 - 81; //*sigh* vanilla

        SpritesAS.SPR_OVERLAY_CHARGE.bindTexture();

        float percFilled = 1F; //TODO Add charge handling
        float uLength = SpritesAS.SPR_OVERLAY_CHARGE.getULength() * percFilled;
        Tuple<Float, Float> uv = SpritesAS.SPR_OVERLAY_CHARGE.getUVOffset();
        float width = barWidth * percFilled;

        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();
        GlStateManager.disableAlphaTest();

        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR, buf -> {
            RenderingGuiUtils.rect(buf, offsetLeft, offsetTop, 10, width, 54)
                    .tex(uv.getA(), uv.getB() + 0.002F, uLength, SpritesAS.SPR_OVERLAY_CHARGE.getVWidth() - 0.002F)
                    .color(1F, 1F, 1F, this.alphaReveal)
                    .draw();
        });

        GlStateManager.enableAlphaTest();
        GlStateManager.disableBlend();

        TextureHelper.bindBlockAtlas();
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack held = player.getHeldItem(Hand.MAIN_HAND);
            if (!held.isEmpty() &&
                    held.getItem() instanceof AlignmentChargeRevealer &&
                    ((AlignmentChargeRevealer) held.getItem()).shouldReveal(held)) {
                revealCharge(20);
            }

            held = player.getHeldItem(Hand.OFF_HAND);
            if (!held.isEmpty() &&
                    held.getItem() instanceof AlignmentChargeRevealer &&
                    ((AlignmentChargeRevealer) held.getItem()).shouldReveal(held)) {
                revealCharge(20);
            }
        }

        revealTicks--;

        if ((revealTicks - fadeTicks) < 0) {
            if (alphaReveal > 0) {
                alphaReveal = Math.max(0, alphaReveal - visibilityChange);
            }
        } else {
            if (alphaReveal < 1) {
                alphaReveal = Math.min(1, alphaReveal + visibilityChange);
            }
        }
    }

    public void revealCharge(int forTicks) {
        revealTicks = forTicks;
    }

    public void resetChargeReveal() {
        revealTicks = 0;
        alphaReveal = 0F;
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.CLIENT);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Alignment Charge Renderer";
    }
}
