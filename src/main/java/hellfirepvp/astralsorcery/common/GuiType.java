/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.screen.ScreenConstellationPaper;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalPages;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalProgression;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.journal.JournalPageAltarRecipe;
import hellfirepvp.astralsorcery.common.data.journal.JournalPageRecipe;
import hellfirepvp.astralsorcery.common.data.journal.JournalPageText;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiType
 * Created by HellFirePvP
 * Date: 03.08.2019 / 11:26
 */
public enum GuiType {

    CONSTELLATION_PAPER,
    TOME;

    public CompoundNBT serializeArguments(Object[] data) {
        try {
            CompoundNBT nbt = new CompoundNBT();
            switch (this) {
                case CONSTELLATION_PAPER:
                    nbt.putString("cst", ((IConstellation) data[0]).getRegistryName().toString());
                    break;
                default:
                    break;
            }
            return nbt;
        } catch (Exception exc) {
            throw new IllegalArgumentException("Invalid Arguments for GuiType: " + this.name(), exc);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public Screen deserialize(CompoundNBT data) {
        try {
            switch (this) {
                case CONSTELLATION_PAPER:
                    return new ScreenConstellationPaper(RegistriesAS.REGISTRY_CONSTELLATIONS.getValue(new ResourceLocation(data.getString("cst"))));
                case TOME:
                    return new ScreenJournalPages(null,
                            new ResearchNode(new ItemStack(Items.APPLE), "", 0, 0)
                                .addPage(new JournalPageText("astralsorcery.journal.SPEC_RELAY.1.text"))
                                .addPage(new JournalPageRecipe(new ResourceLocation("dark_oak_planks")))
                                .addPage(new JournalPageAltarRecipe(AstralSorcery.key("altar/marble/pillar")))
                                .addPage(new JournalPageAltarRecipe(AstralSorcery.key("altar/constellation_paper/aevitas"))),
                            0);
                    //return ScreenJournalProgression.getOpenJournalInstance();
                default:
                    throw new IllegalArgumentException("Unknown GuiType: " + this.name());
            }
        } catch (Exception exc) {
            throw new IllegalArgumentException("Invalid Arguments for GuiType: " + this.name(), exc);
        }
    }
}
