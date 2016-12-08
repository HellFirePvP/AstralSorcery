package hellfirepvp.astralsorcery.client;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.gui.GuiConstellationPaper;
import hellfirepvp.astralsorcery.client.gui.GuiJournalProgression;
import hellfirepvp.astralsorcery.client.gui.GuiTelescope;
import hellfirepvp.astralsorcery.client.gui.container.GuiAltarAttunement;
import hellfirepvp.astralsorcery.client.gui.container.GuiAltarConstellation;
import hellfirepvp.astralsorcery.client.gui.container.GuiAltarDiscovery;
import hellfirepvp.astralsorcery.client.gui.container.GuiJournalContainer;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.item.ItemJournal;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientGuiHandler
 * Created by HellFirePvP
 * Date: 02.08.2016 / 13:26
 */
public class ClientGuiHandler {

    @SideOnly(Side.CLIENT)
    public static Object openGui(CommonProxy.EnumGuiId guiType, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity t = null;
        if(guiType.getTileClass() != null) {
            t = MiscUtils.getTileAt(world, new BlockPos(x, y, z), guiType.getTileClass(), true);
            if(t == null) {
                return null;
            }
        }
        switch (guiType) {
            case TELESCOPE:
                return new GuiTelescope(player, (TileTelescope) t);
            case CONSTELLATION_PAPER:
                IConstellation c = ConstellationRegistry.getConstellationById(x); //Suggested Constellation id;
                if(c == null) {
                    AstralSorcery.log.info("Tried opening ConstellationPaper GUI with out-of-range constellation id!");
                    return null;
                } else {
                    return new GuiConstellationPaper(c);
                }
            case ALTAR_DISCOVERY:
                return new GuiAltarDiscovery(player.inventory, (TileAltar) t);
            case ALTAR_ATTUNEMENT:
                return new GuiAltarAttunement(player.inventory, (TileAltar) t);
            case ALTAR_CONSTELLATION:
                return new GuiAltarConstellation(player.inventory, (TileAltar) t);
            case JOURNAL:
                return GuiJournalProgression.getOpenJournalInstance();
            case JOURNAL_STORAGE:
                ItemStack held = player.getHeldItem(EnumHand.MAIN_HAND);
                if(held != null) {
                    if(held.getItem() != null && held.getItem() instanceof ItemJournal) {
                        return new GuiJournalContainer(player.inventory, held, player.inventory.currentItem);
                    }
                }
            default:
                return null;
        }
    }

}
