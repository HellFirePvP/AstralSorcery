/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal.overlay;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournalOverlay;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.PlayerAttributeMap;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.type.VanillaAttributeType;
import hellfirepvp.astralsorcery.common.constellation.perk.reader.AttributeReader;
import hellfirepvp.astralsorcery.common.constellation.perk.reader.AttributeReaderRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.reader.PerkStatistic;
import hellfirepvp.astralsorcery.common.constellation.perk.reader.PlayerAttributeInterpreter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalOverlayPerkStats
 * Created by HellFirePvP
 * Date: 17.01.2019 / 22:01
 */
public class GuiJournalOverlayPerkStats extends GuiScreenJournalOverlay {

    public static final BindableResource texturePerkStatOverlay =
            AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guicontippaper_blank");
    private static final int HEADER_WIDTH = 190;
    private static final int DEFAULT_WIDTH = 175;

    private final List<PerkStatistic> statistics = new LinkedList<>();

    private int nameStrWidth = -1;
    private int valueStrWidth = -1;
    private int suffixStrWidth = -1;

    public GuiJournalOverlayPerkStats(GuiScreenJournal origin) {
        super(origin);
    }

    @Override
    public void initGui() {
        super.initGui();

        statistics.clear();

        EntityPlayer player = Minecraft.getMinecraft().player;
        PlayerAttributeInterpreter interpreter = PlayerAttributeInterpreter.defaultInterpreter(player);

        AttributeTypeRegistry.getTypes()
                .stream()
                .filter(t -> t instanceof VanillaAttributeType)
                .forEach(t -> ((VanillaAttributeType) t).refreshAttribute(player));

        for (PerkAttributeType type : AttributeTypeRegistry.getTypes()) {
            if (type.hasTypeApplied(player, Side.CLIENT)) {
                PerkStatistic strPerkStat = interpreter.getValue(type);
                if (strPerkStat != null) {
                    statistics.add(strPerkStat);
                }
            }
        }

        statistics.sort(Comparator.comparing(perkStatistic -> I18n.format(perkStatistic.getUnlocPerkTypeName())));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();

        int width = 275;
        int height = 344;

        GlStateManager.disableDepth();
        texturePerkStatOverlay.bindTexture();
        drawTexturedRect(guiLeft + guiWidth / 2 - width / 2, guiTop + guiHeight / 2 - height / 2,
                width, height, texturePerkStatOverlay);
        GlStateManager.enableDepth();

        drawHeader();
        drawPageText(mouseX, mouseY);

        TextureHelper.refreshTextureBindState();
    }

    private void drawHeader() {
        String locTitle = I18n.format("perk.reader.gui");
        TextureHelper.refreshTextureBindState();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        List<String> split = fr.listFormattedStringToWidth(locTitle, MathHelper.floor(HEADER_WIDTH / 1.4));
        int step = 14;

        int offsetTop = guiTop + 15 - (split.size() * step) / 2;

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, offsetTop, 0);
        for (int i = 0; i < split.size(); i++) {
            String s = split.get(i);

            double offsetLeft = width / 2 - (fr.getStringWidth(s) * 1.4) / 2;
            GlStateManager.pushMatrix();
            GlStateManager.translate(offsetLeft, i * step, 0);
            GlStateManager.scale(1.4, 1.4, 1.4);
            GlStateManager.disableDepth();
            fr.drawString(s, 0, 0, 0xEE333333, false);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1, 1);
        GL11.glColor4f(1, 1, 1, 1);
    }

    private void drawPageText(int mouseX, int mouseY) {
        if(nameStrWidth == -1 || valueStrWidth == -1 || suffixStrWidth == -1) {
            buildDisplayWidth();
        }

        Map<Rectangle, PerkStatistic> valueStrMap = Maps.newHashMap();
        GlStateManager.color(1, 1, 1, 1);
        int offsetY = guiTop + 40;
        int offsetX = guiLeft + guiWidth / 2 - DEFAULT_WIDTH / 2;
        int line = 0;
        for (PerkStatistic stat : statistics) {
            GlStateManager.disableDepth();
            fontRenderer.drawString(I18n.format(stat.getUnlocPerkTypeName()),
                    offsetX, offsetY + (line * 10),
                    0xEE333333, false);
            fontRenderer.drawString(stat.getPerkValue(),
                    offsetX + nameStrWidth, offsetY + (line * 10),
                    0xEE333333, false);

            GlStateManager.enableDepth();
            int strLength = fontRenderer.getStringWidth(stat.getPerkValue());
            Rectangle rctValue = new Rectangle(offsetX + nameStrWidth, offsetY + (line * 10),
                    strLength, 8);
            valueStrMap.put(rctValue, stat);

            if (!stat.getSuffix().isEmpty()) {
                line++;
                GlStateManager.disableDepth();
                fontRenderer.drawString(stat.getSuffix(),
                        offsetX + 25, offsetY + (line * 10),
                        0xEE333333, false);
                GlStateManager.enableDepth();
            }
            line++;
        }
        for (Rectangle rct : valueStrMap.keySet()) {
            if (rct.contains(mouseX, mouseY)) {
                PerkStatistic stat = valueStrMap.get(rct);
                drawCalculationDescription(rct.x + rct.width + 2, rct.y + 15, stat);
            }
        }

        GlStateManager.color(1, 1, 1, 1);
    }

    private void drawCalculationDescription(int x, int y, PerkStatistic stat) {
        PerkAttributeType type = stat.getType();
        AttributeReader reader;
        if ((reader = AttributeReaderRegistry.getReader(type.getTypeString())) == null) {
            return;
        }

        EntityPlayer player = Minecraft.getMinecraft().player;
        PlayerAttributeMap attrMap = PerkAttributeHelper.getOrCreateMap(player, Side.CLIENT);

        List<String> information = Lists.newArrayList();
        information.add(I18n.format("perk.reader.description.head",
                AttributeReader.formatDecimal(reader.getDefaultValue(attrMap, player, Side.CLIENT))));
        information.add(I18n.format("perk.reader.description.addition",
                AttributeReader.formatDecimal(reader.getModifierValueForMode(attrMap, player, Side.CLIENT,
                        PerkAttributeModifier.Mode.ADDITION) - 1)));
        information.add(I18n.format("perk.reader.description.increase",
                AttributeReader.formatDecimal(reader.getModifierValueForMode(attrMap, player, Side.CLIENT,
                        PerkAttributeModifier.Mode.ADDED_MULTIPLY))));
        information.add(I18n.format("perk.reader.description.moreless",
                AttributeReader.formatDecimal(reader.getModifierValueForMode(attrMap, player, Side.CLIENT,
                        PerkAttributeModifier.Mode.STACKING_MULTIPLY))));

        if (!stat.getSuffix().isEmpty() || !stat.getPostProcessInfo().isEmpty()) {
            information.add("");
        }
        if (!stat.getSuffix().isEmpty()) {
            information.add(stat.getSuffix());
        }
        if (!stat.getPostProcessInfo().isEmpty()) {
            information.add(stat.getPostProcessInfo());
        }

        RenderingUtils.renderBlueTooltip(x, y, information, this.fontRenderer);
    }

    private void buildDisplayWidth() {
        nameStrWidth = -1;
        valueStrWidth = -1;
        suffixStrWidth = -1;

        for (PerkStatistic stat : this.statistics) {
            int nameWidth = fontRenderer.getStringWidth(I18n.format(stat.getUnlocPerkTypeName()));
            int valueWidth = fontRenderer.getStringWidth(stat.getPerkValue());
            int suffixWidth = fontRenderer.getStringWidth(stat.getSuffix());

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
