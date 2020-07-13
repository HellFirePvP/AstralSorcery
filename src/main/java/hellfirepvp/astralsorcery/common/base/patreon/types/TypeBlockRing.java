/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.types;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.LightmapUtil;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.base.patreon.FlareColor;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TypeBlockRing
 * Created by HellFirePvP
 * Date: 31.08.2019 / 09:44
 */
public class TypeBlockRing extends PatreonEffect {

    private final UUID playerUUID;

    private final float distance;
    private final float rotationAngle;
    private final int repetition;
    private final int rotationSpeed;
    private final float rotationPart;
    private final Map<BlockPos, BlockState> pattern;

    /*
    Based on X = 2
    variable in Z and Y direction, X towards and from player
     */
    public TypeBlockRing(UUID sessionEffectId,
                             FlareColor chosenColor,
                             UUID playerUUID,
                             float distance,
                             float rotationAngle,
                             int repeats,
                             int tickRotationSpeed,
                             Map<BlockPos, BlockState> pattern) {
        super(sessionEffectId, chosenColor);

        this.playerUUID = playerUUID;
        this.distance = distance;
        this.rotationAngle = rotationAngle;
        this.repetition = repeats;
        this.rotationSpeed = tickRotationSpeed;
        this.rotationPart = 360F / rotationSpeed;
        this.pattern = pattern;
    }

    @Override
    public void attachEventListeners(IEventBus bus) {
        super.attachEventListeners(bus);

        bus.register(this);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderLast(RenderWorldLastEvent event) {
        PlayerEntity pl = Minecraft.getInstance().player;
        if (Minecraft.getInstance().gameSettings.thirdPersonView == 0 && //First person
                pl != null && pl.getUniqueID().equals(playerUUID)) {
            MatrixStack renderStack = event.getMatrixStack();

            int alpha = 88;
            if (pl.rotationPitch >= 35F) {
                alpha *= Math.max(0, (55F - pl.rotationPitch) / 20F);
            }
            renderStack.push();
            renderStack.translate(0, -0.5, 0);
            renderStack.scale(0.5F, 0.5F, 0.5F);
            renderRingAt(renderStack, pl, alpha, event.getPartialTicks());
            renderStack.pop();
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderPost(RenderPlayerEvent.Post ev) {
        PlayerEntity player = ev.getPlayer();
        if (!player.getUniqueID().equals(playerUUID)) {
            return;
        }

        renderRingAt(ev.getMatrixStack(), player, 88, ev.getPartialRenderTick());
    }

    @OnlyIn(Dist.CLIENT)
    private void renderRingAt(MatrixStack renderStack, PlayerEntity player, int alphaMultiplier, float pTicks) {
        float addedRotationAngle = 0;

        if (rotationSpeed > 1) {
            float rot = ClientScheduler.getSystemClientTick() % rotationSpeed;
            addedRotationAngle = (rot / ((float) (rotationSpeed))) * 360F + this.rotationPart * pTicks;
        }

        RenderSystem.enableTexture();
        BlockAtlasTexture.getInstance().bindTexture();

        RenderSystem.disableAlphaTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        Blending.ADDITIVE_ALPHA.apply();

        for (int rotation = 0; rotation < 360; rotation += (360 / repetition)) {
            for (BlockPos offset : pattern.keySet()) {
                BlockState state = pattern.get(offset);

                TextureAtlasSprite tas = RenderingUtils.getParticleTexture(state, offset);
                if (tas == null) {
                    continue;
                }

                float angle = offset.getZ() * rotationAngle + rotation + addedRotationAngle;

                Vector3 dir = new Vector3(offset.getX() - distance, offset.getY(), 0);
                dir.rotate(Math.toRadians(angle), Vector3.RotAxis.Y_AXIS);
                dir.multiply(new Vector3(0.2F, 0.1F, 0.2F));

                renderStack.push();
                renderStack.translate(dir.getX(), dir.getY(), dir.getZ());
                renderStack.scale(0.09F, 0.09F, 0.09F);

                RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, buf -> {
                    RenderingDrawUtils.renderTexturedCubeCentralColorLighted(buf, renderStack,
                            tas.getMinU(), tas.getMinV(),
                            tas.getMaxU() - tas.getMinU(), tas.getMaxV() - tas.getMinV(),
                            255, 255, 255, alphaMultiplier, LightmapUtil.getPackedLightCoords(player.getEntityWorld(), player.getPosition()));
                });
                renderStack.pop();
            }
        }

        Blending.DEFAULT.apply();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();

        RenderSystem.disableTexture();
    }
}
