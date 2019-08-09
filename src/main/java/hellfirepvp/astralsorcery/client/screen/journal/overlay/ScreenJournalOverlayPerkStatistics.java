/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.overlay;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournal;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;

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
        super(new TranslationTextComponent("gui.journal.perks.stats"), origin);
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
            if (type.hasTypeApplied(player, Dist.CLIENT)) {
                PerkStatistic strPerkStat = interpreter.getValue(type);
                if (strPerkStat != null) {
                    statistics.add(strPerkStat);
                }
            }
        }

        statistics.sort(Comparator.comparing(perkStatistic -> I18n.format(perkStatistic.getUnlocPerkTypeName())));
    }

    @Override
    public void render(int mouseX, int mouseY, float pTicks) {
        super.render(mouseX, mouseY, pTicks);

        Blending.DEFAULT.applyStateManager();

        int width = 275;
        int height = 344;

        GlStateManager.disableDepthTest();
        TexturesAS.TEX_GUI_KNOWLEDGE_FRAGMENT_BLANK.bindTexture();
        drawTexturedRect(guiLeft + guiWidth / 2 - width / 2, guiTop + guiHeight / 2 - height / 2,
                width, height, TexturesAS.TEX_GUI_KNOWLEDGE_FRAGMENT_BLANK);
        GlStateManager.enableDepthTest();

        drawHeader();
        drawPageText(mouseX, mouseY);
    }

    private void drawHeader() {
        String locTitle = I18n.format("perk.reader.gui");
        List<String> split = font.listFormattedStringToWidth(locTitle, MathHelper.floor(HEADER_WIDTH / 1.4));
        int step = 14;

        int offsetTop = guiTop + 15 - (split.size() * step) / 2;

        GlStateManager.pushMatrix();
        GlStateManager.translated(0, offsetTop, 0);
        for (int i = 0; i < split.size(); i++) {
            String s = split.get(i);

            double offsetLeft = width / 2 - (font.getStringWidth(s) * 1.4) / 2;
            GlStateManager.pushMatrix();
            GlStateManager.translated(offsetLeft, i * step, 0);
            GlStateManager.scaled(1.4, 1.4, 1.4);
            GlStateManager.disableDepthTest();
            RenderingDrawUtils.renderStringAtCurrentPos(font, s, 0xEE333333);
            GlStateManager.enableDepthTest();
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }

    private void drawPageText(int mouseX, int mouseY) {
        if(nameStrWidth == -1 || valueStrWidth == -1 || suffixStrWidth == -1) {
            buildDisplayWidth();
        }

        Map<Rectangle, PerkStatistic> valueStrMap = Maps.newHashMap();
        int offsetY = guiTop + 40;
        int offsetX = guiLeft + guiWidth / 2 - DEFAULT_WIDTH / 2;
        int line = 0;
        GlStateManager.disableDepthTest();
        for (PerkStatistic stat : statistics) {

            RenderingDrawUtils.renderStringAtPos(offsetX, offsetY + (line * 10),
                    this.font, I18n.format(stat.getUnlocPerkTypeName()),
                    0xEE333333, false);
            RenderingDrawUtils.renderStringAtPos(offsetX + nameStrWidth, offsetY + (line * 10),
                    this.font, stat.getPerkValue(),
                    0xEE333333, false);

            int strLength = font.getStringWidth(stat.getPerkValue());
            Rectangle rctValue = new Rectangle(offsetX + nameStrWidth, offsetY + (line * 10),
                    strLength, 8);
            valueStrMap.put(rctValue, stat);

            if (!stat.getSuffix().isEmpty()) {
                line++;
                RenderingDrawUtils.renderStringAtPos(offsetX + 25, offsetY + (line * 10),
                        this.font, stat.getSuffix(),
                        0xEE333333, false);
            }
            line++;
        }
        GlStateManager.enableDepthTest();

        for (Rectangle rct : valueStrMap.keySet()) {
            if (rct.contains(mouseX, mouseY)) {
                PerkStatistic stat = valueStrMap.get(rct);
                drawCalculationDescription(rct.x + rct.width + 2, rct.y + 15, stat);
            }
        }
    }

    private void drawCalculationDescription(int x, int y, PerkStatistic stat) {
        PerkAttributeType type = stat.getType();
        PerkAttributeReader reader = type.getReader();
        if (reader == null) {
            return;
        }

        PlayerEntity player = Minecraft.getInstance().player;
        PerkAttributeMap attrMap = PerkAttributeHelper.getOrCreateMap(player, Dist.CLIENT);

        List<String> information = Lists.newArrayList();
        information.add(I18n.format("perk.reader.description.head",
                PerkAttributeReader.formatDecimal(reader.getDefaultValue(attrMap, player, Dist.CLIENT))));
        information.add(I18n.format("perk.reader.description.addition",
                PerkAttributeReader.formatDecimal(reader.getModifierValueForMode(attrMap, player, Dist.CLIENT,
                        ModifierType.ADDITION) - 1)));
        information.add(I18n.format("perk.reader.description.increase",
                PerkAttributeReader.formatDecimal(reader.getModifierValueForMode(attrMap, player, Dist.CLIENT,
                        ModifierType.ADDED_MULTIPLY))));
        information.add(I18n.format("perk.reader.description.moreless",
                PerkAttributeReader.formatDecimal(reader.getModifierValueForMode(attrMap, player, Dist.CLIENT,
                        ModifierType.STACKING_MULTIPLY))));

        if (!stat.getSuffix().isEmpty() || !stat.getPostProcessInfo().isEmpty()) {
            information.add("");
        }
        if (!stat.getSuffix().isEmpty()) {
            information.add(stat.getSuffix());
        }
        if (!stat.getPostProcessInfo().isEmpty()) {
            information.add(stat.getPostProcessInfo());
        }

        RenderingDrawUtils.renderBlueTooltip(x, y, information, this.font, false);
    }

    private void buildDisplayWidth() {
        nameStrWidth = -1;
        valueStrWidth = -1;
        suffixStrWidth = -1;

        for (PerkStatistic stat : this.statistics) {
            int nameWidth = font.getStringWidth(I18n.format(stat.getUnlocPerkTypeName()));
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
