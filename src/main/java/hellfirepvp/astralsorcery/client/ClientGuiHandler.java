/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.gui.*;
import hellfirepvp.astralsorcery.client.gui.container.*;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.item.ItemJournal;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileMapDrawingTable;
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
            case HAND_TELESCOPE:
                return new GuiHandTelescope();
            case CONSTELLATION_PAPER:
                IConstellation c = ConstellationRegistry.getConstellationById(x); //Suggested Constellation id;
                if(c == null) {
                    AstralSorcery.log.info("[AstralSorcery] Tried opening ConstellationPaper GUI with out-of-range constellation id!");
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
            case ALTAR_TRAIT:
                return new GuiAltarTrait(player.inventory, (TileAltar) t);
            case MAP_DRAWING:
                return new GuiMapDrawing((TileMapDrawingTable) t);
            case JOURNAL:
                return GuiJournalProgression.getOpenJournalInstance();
            case JOURNAL_STORAGE:
                ItemStack held = player.getHeldItem(EnumHand.MAIN_HAND);
                if(!held.isEmpty()) {
                    if(held.getItem() instanceof ItemJournal) {
                        return new GuiJournalContainer(player.inventory, held, player.inventory.currentItem);
                    }
                }
            default:
                return null;
        }
    }

}
