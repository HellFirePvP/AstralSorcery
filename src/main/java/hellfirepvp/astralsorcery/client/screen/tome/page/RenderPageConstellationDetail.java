/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.page;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.EffectUtil;
import hellfirepvp.astralsorcery.client.util.RenderQuadUtil;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.level.LevelSkyHandler;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.common.extensions.IHolderExtension;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderPageConstellationDetail
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderPageConstellationDetail extends RenderPage {

    private final BaseConstellation constellation;

    public RenderPageConstellationDetail(@Nullable ResearchNode node, int nodePage, BaseConstellation constellation) {
        super(node, nodePage);
        this.constellation = constellation;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float pTicks, float mouseX, float mouseY) {
        Font font = Minecraft.getInstance().font;
        MutableComponent title = this.constellation.getName();
        PlayerProgress progress = ResearchManager.getClientProgress();
        boolean discovered = progress.hasDiscoveredConstellation(this.constellation);

        ResourceLocation id = this.constellation.getHolder()
                .map(IHolderExtension::getKey)
                .map(ResourceKey::location)
                .orElse(ResourceLocation.withDefaultNamespace("unknown"));
        String subtitleKey = String.format("tome.research.constellation.%s.%s.subtitle", id.getNamespace(), id.getPath());
        String descriptionKey = String.format("tome.research.constellation.%s.%s.description", id.getNamespace(), id.getPath());
        MutableComponent subtitle = !discovered ? Component.literal("? ? ?") : Component.translatable(subtitleKey);
        MutableComponent description = Component.translatable(descriptionKey);

        int titleWidth = font.width(title);
        float titleOffset = x + TomePage.DEFAULT_WIDTH / 2F - (titleWidth * 1.5F) / 2F;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(titleOffset, y, 0);
        guiGraphics.pose().scale(1.5F, 1.5F, 1F);
        guiGraphics.drawString(font, title, 0, 0, ColorsAS.TOME_TEXT_COLOR.getColor(), true);
        guiGraphics.pose().popPose();

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        TexturesAS.SCREEN_TOME_UNDERLINE.bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
            RenderQuadUtil.rect(buf, guiGraphics.pose(), x, y + 13, TomePage.DEFAULT_WIDTH, 13)
                    .draw();
        });
        RenderSystem.disableBlend();

        int subtitleWidth = font.width(subtitle);
        int subtitleOffset = x + TomePage.DEFAULT_WIDTH / 2 - subtitleWidth / 2;
        guiGraphics.drawString(font, subtitle, subtitleOffset, y + 26, ColorsAS.TOME_TEXT_COLOR.getColor(), true);

        if (discovered) {
            List<FormattedCharSequence> descriptionLines = font.split(description, TomePage.DEFAULT_WIDTH - 10);
            int offsetY = y + 46;
            for (FormattedCharSequence line : descriptionLines) {
                guiGraphics.drawString(font, line, x + 5, offsetY, ColorsAS.TOME_TEXT_COLOR.getColor(), true);
                offsetY += 11;
            }
        }

        LevelSkyHandler.getContext(Minecraft.getInstance().level).ifPresent(ctx -> {
            List<MoonPhase> activePhases = ctx.getConstellationHandler().getActiveIndexedPhases(this.constellation);

            if (activePhases.isEmpty()) {
                Component display = Component.literal("? ? ?");
                int unknownWidth = font.width(display);
                float unknownOffset = x + TomePage.DEFAULT_WIDTH / 2F - (unknownWidth * 1.8F) / 2F;

                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(unknownOffset, y + 175, 0);
                guiGraphics.pose().scale(1.8F, 1.8F, 1F);
                guiGraphics.drawString(font, display, 0, 0, ColorsAS.TOME_TEXT_COLOR.getColor(), true);
                guiGraphics.pose().popPose();
            } else {
                int totalWidth = MoonPhase.values().length * 20 + (MoonPhase.values().length - 1) * 2;
                int phaseOffsetX = x + TomePage.DEFAULT_WIDTH / 2 - totalWidth / 2;
                int phaseOffsetY = y + 175;

                MoonPhase currentPhase = MoonPhase.fromWorld(Minecraft.getInstance().level);

                RenderSystem.enableBlend();
                MoonPhase[] values = MoonPhase.values();
                for (int i = 0; i < values.length; i++) {
                    MoonPhase phase = values[i];
                    float brightness;
                    if (activePhases.contains(phase)) {
                        brightness = 1F;
                        Blending.PREALPHA.apply();
                    } else {
                        brightness = 0.5F;
                        Blending.DEFAULT.apply();
                    }
                    phase.getAssetQuery().resolve().bindTexture();
                    float sizeBob = phase == currentPhase ? EffectUtil.flicker(0.1F, pTicks, 2F, 0F) : 0;

                    int phaseIndex = i;
                    RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
                        RenderQuadUtil.rect(buf, guiGraphics.pose(),
                                        phaseOffsetX + phaseIndex * 22 - sizeBob / 2F, phaseOffsetY - sizeBob / 2F,
                                        20 + sizeBob, 20 + sizeBob)
                                .color(brightness, brightness, brightness, 1F)
                                .draw();
                    });
                }
                RenderSystem.disableBlend();
            }
        });

        MutableComponent tierTitle = !discovered ? Component.literal("? ? ?") : this.constellation.getTier().getTierName();
        int tierWidth = font.width(tierTitle);
        float tierOffset = x + TomePage.DEFAULT_WIDTH / 2F - (tierWidth * 1F) / 2F;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(tierOffset, y + 195, 0);
        guiGraphics.drawString(font, tierTitle, 0, 0, ColorsAS.TOME_TEXT_COLOR.getColor(), true);
        guiGraphics.pose().popPose();
    }
}
