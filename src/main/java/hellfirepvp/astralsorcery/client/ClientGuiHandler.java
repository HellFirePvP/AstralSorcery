package hellfirepvp.astralsorcery.client;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.gui.GuiConstellationPaper;
import hellfirepvp.astralsorcery.client.gui.GuiJournalProgression;
import hellfirepvp.astralsorcery.client.gui.GuiTelescope;
import hellfirepvp.astralsorcery.client.gui.container.GuiAltarAttenuation;
import hellfirepvp.astralsorcery.client.gui.container.GuiAltarConstellation;
import hellfirepvp.astralsorcery.client.gui.container.GuiAltarDiscovery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.entities.EntityTelescope;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
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
                Entity e = world.getEntityByID(x); //Suggested entity id;
                if (e == null || !(e instanceof EntityTelescope)) {
                    AstralSorcery.log.info("Tried opening Telescope GUI without valid telescope entity?");
                    return null;
                } else {
                    return new GuiTelescope(player, (EntityTelescope) e);
                }
            case CONSTELLATION_PAPER:
                Constellation c = ConstellationRegistry.getConstellationById(x); //Suggested Constellation id;
                if(c == null) {
                    AstralSorcery.log.info("Tried opening ConstellationPaper GUI with out-of-range constellation id!");
                    return null;
                } else {
                    return new GuiConstellationPaper(c);
                }
            case ALTAR_DISCOVERY:
                return new GuiAltarDiscovery(player.inventory, (TileAltar) t);
            case ALTAR_ATTENUATION:
                return new GuiAltarAttenuation(player.inventory, (TileAltar) t);
            case ALTAR_CONSTELLATION:
                return new GuiAltarConstellation(player.inventory, (TileAltar) t);
            case JOURNAL:
                return GuiJournalProgression.getOpenJournalInstance();
            default:
                return null;
        }
    }

}
