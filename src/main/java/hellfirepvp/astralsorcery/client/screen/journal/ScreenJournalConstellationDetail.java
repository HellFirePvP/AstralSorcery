/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal;

import hellfirepvp.astralsorcery.common.base.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.data.journal.JournalPage;
import hellfirepvp.astralsorcery.common.data.research.GatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenJournalConstellationDetail
 * Created by HellFirePvP
 * Date: 04.08.2019 / 10:54
 */
public class ScreenJournalConstellationDetail extends ScreenJournal {

    private final ScreenJournal origin;
    private final IConstellation constellation;
    private final boolean detailed;

    private int doublePageID = 0;
    private int doublePages = 0;
    private List<MoonPhase> activePhases = null;

    private List<String> locTextMain = new LinkedList<>();
    private List<String> locTextRitualEnch = new LinkedList<>();
    private List<String> locTextCapeEffect = new LinkedList<>();

    public ScreenJournalConstellationDetail(ScreenJournal origin, IConstellation cst) {
        super(new TranslationTextComponent(cst.getUnlocalizedName()), NO_BOOKMARK);
        this.origin = origin;
        this.constellation = cst;

        this.detailed = ResearchHelper.getClientProgress().hasConstellationDiscovered(cst);

        PlayerProgress playerProgress = ResearchHelper.getClientProgress();
        if (this.detailed && (GatedKnowledge.CONSTELLATION_RITUAL.canSee(playerProgress) || GatedKnowledge.CONSTELLATION_ENCH_POTION.canSee(playerProgress))) {
            this.doublePages++;

            if (GatedKnowledge.CONSTELLATION_CAPE.canSee(playerProgress) && !(constellation instanceof IMinorConstellation)) {
                this.doublePages++;
            }
        }

        testActivePhases();
        buildMainText();
        buildEnchText();
        buildRitualText();
        buildCapeText();
    }

    private void buildCapeText() {
        if (GatedKnowledge.CONSTELLATION_CAPE.canSee(ResearchHelper.getClientProgress())) {
            String unlocEnch = constellation.getUnlocalizedName() + ".capeeffect";
            String textEnch = I18n.format(unlocEnch);
            if (!unlocEnch.equals(textEnch)) {
                String head = I18n.format("gui.journal.cst.capeeffect");
                locTextCapeEffect.add(head);
                locTextCapeEffect.add("");

                List<String> lines = new LinkedList<>();
                for (String segment : textEnch.split("<NL>")) {
                    lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
                    lines.add("");
                }
                locTextCapeEffect.addAll(lines);
                locTextCapeEffect.add("");
            }
        }
    }

    private void buildEnchText() {
        if (GatedKnowledge.CONSTELLATION_ENCH_POTION.canSee(ResearchHelper.getClientProgress())) {
            String unlocEnch = constellation.getUnlocalizedName() + ".enchantments";
            String textEnch = I18n.format(unlocEnch);
            if (!unlocEnch.equals(textEnch)) {
                String head = I18n.format("gui.journal.cst.enchantments");
                locTextRitualEnch.add(head);
                locTextRitualEnch.add("");

                List<String> lines = new LinkedList<>();
                for (String segment : textEnch.split("<NL>")) {
                    lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
                    lines.add("");
                }
                locTextRitualEnch.addAll(lines);
                locTextRitualEnch.add("");
            }
        }
    }

    private void buildRitualText() {
        if (GatedKnowledge.CONSTELLATION_RITUAL.canSee(ResearchHelper.getClientProgress())) {
            if (constellation instanceof IMinorConstellation) {
                String unlocRitual = constellation.getUnlocalizedName() + ".trait";
                String textRitual = I18n.format(unlocRitual);
                if (!unlocRitual.equals(textRitual)) {
                    String head = I18n.format("gui.journal.cst.ritual.trait");
                    locTextRitualEnch.add(head);
                    locTextRitualEnch.add("");

                    List<String> lines = new LinkedList<>();
                    for (String segment : textRitual.split("<NL>")) {
                        lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
                        lines.add("");
                    }
                    locTextRitualEnch.addAll(lines);
                }
            } else {
                String unlocRitual = constellation.getUnlocalizedName() + ".ritual";
                String textRitual = I18n.format(unlocRitual);
                if (!unlocRitual.equals(textRitual)) {
                    String head = I18n.format("gui.journal.cst.ritual");
                    locTextRitualEnch.add(head);
                    locTextRitualEnch.add("");

                    List<String> lines = new LinkedList<>();
                    for (String segment : textRitual.split("<NL>")) {
                        lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
                        lines.add("");
                    }
                    locTextRitualEnch.addAll(lines);
                }
            }
        }
    }

    private void buildMainText() {
        String unloc = constellation.getUnlocalizedName() + ".effect";
        String text = I18n.format(unloc);
        if (unloc.equals(text)) {
            return;
        }

        List<String> lines = new LinkedList<>();
        for (String segment : text.split("<NL>")) {
            lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
            lines.add("");
        }
        locTextMain.addAll(lines);
    }

    private void testActivePhases() {
        WorldContext ctx = SkyHandler.getContext(Minecraft.getInstance().world, Dist.CLIENT);
        if (ctx == null) {
            return;
        }
        this.activePhases = new LinkedList<>();
        for (MoonPhase phase : MoonPhase.values()) {
            if (ctx.getConstellationHandler().isActive(this.constellation, phase)) {
                this.activePhases.add(phase);
            }
        }
    }

    @Override
    protected boolean handleRightClickClose(double mouseX, double mouseY) {
        Minecraft.getInstance().displayGuiScreen(origin);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }

        if(mouseButton != 0) {
            return false;
        }
        if (handleBookmarkClick(mouseX, mouseY)) {
            return true;
        }

        if(rectBack != null && rectBack.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(origin);
            return;
        }
        if(rectPrev != null && rectPrev.contains(p)) {
            if(doublePageID >= 1) {
                this.doublePageID--;
            }
            SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
            return;
        }
        if(rectNext != null && rectNext.contains(p)) {
            if(doublePageID <= doublePages - 1) {
                this.doublePageID++;
            }
            SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
            return;
        }
        if(doublePageID != 0 && lastFramePage != null) {
            lastFramePage.propagateMouseClick(mouseX, mouseY);
        }
    }

}
