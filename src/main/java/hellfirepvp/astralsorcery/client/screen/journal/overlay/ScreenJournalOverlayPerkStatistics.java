/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.overlay;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournal;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeMap;
import hellfirepvp.astralsorcery.common.perk.reader.PerkAttributeInterpreter;
import hellfirepvp.astralsorcery.common.perk.reader.PerkAttributeReader;
import hellfirepvp.astralsorcery.common.perk.reader.PerkStatistic;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import hellfirepvp.astralsorcery.common.perk.type.vanilla.VanillaPerkAttributeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.LogicalSide;

import java.awt.*;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenJournalOverlayPerkStatistics
 * Created by HellFirePvP
 * Date: 09.08.2019 / 07:52
 */
public class ScreenJournalOverlayPerkStatistics extends ScreenJournalOverlay {

    private static final int HEADER_WIDTH = 190;
    private static final int DEFAULT_WIDTH = 175;

    private final List<PerkStatistic> statistics = new LinkedList<>();

    private int nameStrWidth = -1;
    private int valueStrWidth = -1;
    private int suffixStrWidth = -1;

    public ScreenJournalOverlayPerkStatistics(ScreenJournal origin) {
        super(new TranslationTextComponent("screen.astralsorcery.tome.perks.stats"), origin);
    }

    @Override
    protected void init() {
        super.init();

        statistics.clear();

        PlayerEntity player = Minecraft.getInstance().player;
        PerkAttributeInterpreter interpreter = PerkAttributeInterpreter.defaultInterpreter(player);

        RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES.getValues()
                .stream()
                .filter(t -> t instanceof VanillaPerkAttributeType)
                .forEach(t -> ((VanillaPerkAttributeType) t).refreshAttribute(player));

        for (PerkAttributeType type : RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES.getValues()) {
            if (type.hasTypeApplied(player, LogicalSide.CLIENT)) {
                PerkStatistic strPerkStat = interpreter.getValue(type);
                if (strPerkStat != null) {
                    statistics.add(strPerkStat);
                }
            }
        }

        statistics.sort(Comparator.comparing(perkStatistic -> I18n.format(perkStatistic.getUnlocPerkTypeName())));
    }

    @Override
    public void render(MatrixStack renderStack, int mouseX, int mouseY, float pTicks) {
        super.render(renderStack, mouseX, mouseY, pTicks);

        float width = 275;
        float height = 344;

        this.setBlitOffset(150);
        TexturesAS.TEX_GUI_PARCHMENT_BLANK.bindTexture();
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        RenderingGuiUtils.drawRect(renderStack, guiLeft + guiWidth / 2F - width / 2F, guiTop + guiHeight / 2F - height / 2F, this.getGuiZLevel(),
                width, height);
        RenderSystem.disableBlend();
        this.setBlitOffset(0);

        drawHeader(renderStack);
        drawPageText(renderStack, mouseX, mouseY);
    }

    private void drawHeader(MatrixStack renderStack) {
        ITextProperties title = new TranslationTextComponent("perk.reader.astralsorcery.gui");
        List<IReorderingProcessor> lines = font.trimStringToWidth(title, MathHelper.floor(HEADER_WIDTH / 1.4F));
        int step = 14;
        float offsetTop = guiTop + 15 - (lines.size() * step) / 2F;

        renderStack.push();
        renderStack.translate(0, offsetTop, 0);

        for (int i = 0; i < lines.size(); i++) {
            IReorderingProcessor line = lines.get(i);
            float offsetLeft = width / 2F - (font.func_243245_a(line) * 1.4F) / 2F;

            renderStack.push();
            renderStack.translate(offsetLeft, i * step, 0);
            renderStack.scale(1.4F, 1.4F, 1F);
            RenderingDrawUtils.renderStringAt(line, renderStack, font, 0xEE333333, false);
            renderStack.pop();
        }
        renderStack.pop();
    }

    private void drawPageText(MatrixStack renderStack, int mouseX, int mouseY) {
        if (nameStrWidth == -1 || valueStrWidth == -1 || suffixStrWidth == -1) {
            buildDisplayWidth();
        }

        Map<Rectangle, PerkStatistic> valueStrMap = Maps.newHashMap();
        int offsetY = guiTop + 40;
        int offsetX = guiLeft + guiWidth / 2 - DEFAULT_WIDTH / 2;
        int line = 0;
        for (PerkStatistic stat : statistics) {
            ITextProperties statName = new TranslationTextComponent(stat.getUnlocPerkTypeName());
            List<IReorderingProcessor> statistics = font.trimStringToWidth(statName, MathHelper.floor(HEADER_WIDTH / 1.5F));
            for (int i = 0; i < statistics.size(); i++) {
                IReorderingProcessor statistic = statistics.get(i);

                int drawX = offsetX;
                if (i > 0) {
                    drawX += 10;
                }
                renderStack.push();
                renderStack.translate(drawX, offsetY + ((line + i) * 10), this.getGuiZLevel());
                RenderingDrawUtils.renderStringAt(statistic, renderStack, font, 0xEE333333, false);
                renderStack.pop();
            }

            renderStack.push();
            renderStack.translate(offsetX + nameStrWidth, offsetY + (line * 10), this.getGuiZLevel());
            RenderingDrawUtils.renderStringAt(new StringTextComponent(stat.getPerkValue()), renderStack, font, 0xEE333333, false);
            renderStack.pop();

            int strLength = font.getStringWidth(stat.getPerkValue());
            Rectangle rctValue = new Rectangle(offsetX + nameStrWidth, offsetY + (line * 10), strLength, 8);
            valueStrMap.put(rctValue, stat);

            line += statistics.size();
            if (!stat.getSuffix().isEmpty()) {
                renderStack.push();
                renderStack.translate(offsetX + 25, offsetY + (line * 10), this.getGuiZLevel());
                RenderingDrawUtils.renderStringAt(new StringTextComponent(stat.getSuffix()), renderStack, font, 0xEE333333, false);
                renderStack.pop();

                line++;
            }
        }

        for (Rectangle rct : valueStrMap.keySet()) {
            if (rct.contains(mouseX, mouseY)) {
                PerkStatistic stat = valueStrMap.get(rct);
                drawCalculationDescription(renderStack, rct.x + rct.width + 2, rct.y + 15, stat);
            }
        }
    }

    private void drawCalculationDescription(MatrixStack renderStack, int x, int y, PerkStatistic stat) {
        PerkAttributeType type = stat.getType();
        PerkAttributeReader reader = type.getReader();
        if (reader == null) {
            return;
        }

        PlayerEntity player = Minecraft.getInstance().player;
        PerkAttributeMap attrMap = PerkAttributeHelper.getOrCreateMap(player, LogicalSide.CLIENT);

        List<ITextProperties> information = Lists.newArrayList();
        information.add(new TranslationTextComponent("perk.reader.astralsorcery.description.head",
                PerkAttributeReader.formatDecimal(reader.getDefaultValue(attrMap, player, LogicalSide.CLIENT))));
        information.add(new TranslationTextComponent("perk.reader.astralsorcery.description.addition",
                PerkAttributeReader.formatDecimal(reader.getModifierValueForMode(attrMap, player, LogicalSide.CLIENT,
                        ModifierType.ADDITION) - 1)));
        information.add(new TranslationTextComponent("perk.reader.astralsorcery.description.increase",
                PerkAttributeReader.formatDecimal(reader.getModifierValueForMode(attrMap, player, LogicalSide.CLIENT,
                        ModifierType.ADDED_MULTIPLY))));
        information.add(new TranslationTextComponent("perk.reader.astralsorcery.description.moreless",
                PerkAttributeReader.formatDecimal(reader.getModifierValueForMode(attrMap, player, LogicalSide.CLIENT,
                        ModifierType.STACKING_MULTIPLY))));

        if (!stat.getSuffix().isEmpty() || !stat.getPostProcessInfo().isEmpty()) {
            information.add(StringTextComponent.EMPTY);
        }
        if (!stat.getSuffix().isEmpty()) {
            information.add(new StringTextComponent(stat.getSuffix()));
        }
        if (!stat.getPostProcessInfo().isEmpty()) {
            information.add(new StringTextComponent(stat.getPostProcessInfo()));
        }

        RenderingDrawUtils.renderBlueTooltipComponents(renderStack, x, y, this.getGuiZLevel(), information, this.font, false);
    }

    private void buildDisplayWidth() {
        nameStrWidth = -1;
        valueStrWidth = -1;
        suffixStrWidth = -1;

        for (PerkStatistic stat : this.statistics) {
            ITextProperties typeName = new TranslationTextComponent(stat.getUnlocPerkTypeName());
            int nameWidth = Math.min(font.getStringPropertyWidth(typeName), ((int) (HEADER_WIDTH / 1.5F)));
            int valueWidth = font.getStringWidth(stat.getPerkValue());
            int suffixWidth = font.getStringWidth(stat.getSuffix());

            if (nameWidth > nameStrWidth) {
                nameStrWidth = nameWidth;
            }
            if (valueWidth > valueStrWidth) {
                valueStrWidth = valueWidth;
            }
            if (suffixWidth > suffixStrWidth) {
                suffixStrWidth = suffixWidth;
            }
        }

        nameStrWidth += 6;
        valueStrWidth += 6;
        suffixStrWidth += 6;
    }
}
