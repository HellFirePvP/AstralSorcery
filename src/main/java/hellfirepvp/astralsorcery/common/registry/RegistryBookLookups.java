/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.client.gui.journal.GuiJournalPages;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryBookLookups
 * Created by HellFirePvP
 * Date: 27.12.2016 / 18:39
 */
public class RegistryBookLookups {

    private static Map<ItemStack, LookupInfo> lookupMap = new HashMap<>();

    @Nullable
    public static LookupInfo tryGetPage(EntityPlayer querying, Side side, ItemStack search) {
        for (ItemStack compare : lookupMap.keySet()) {
            if(ItemUtils.stackEqualsNonNBT(search, compare)) {
                LookupInfo info = lookupMap.get(compare);
                PlayerProgress prog = ResearchManager.getProgress(querying, side);
                if(prog != null) {
                    if(prog.getResearchProgression().contains(info.neededKnowledge) && info.node.canSee(prog)) {
                        return info;
                    }
                }
            }
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public static void openLookupJournalPage(LookupInfo info) {
        if(info == null) return;

        GuiScreen current = Minecraft.getMinecraft().currentScreen;
        Minecraft.getMinecraft().displayGuiScreen(new GuiJournalPages(current, info.node, info.pageIndex));
    }

    public static void registerItemLookup(ItemStack stack, ResearchNode parentNode, int nodePage, ResearchProgression neededProgression) {
        lookupMap.put(stack, new LookupInfo(parentNode, nodePage, neededProgression));
    }

    public static class LookupInfo {

        public final ResearchNode node;
        public final int pageIndex;
        public final ResearchProgression neededKnowledge;

        public LookupInfo(ResearchNode node, int pageIndex, ResearchProgression neededKnowledge) {
            this.node = node;
            this.pageIndex = pageIndex;
            this.neededKnowledge = neededKnowledge;
        }
    }

}
