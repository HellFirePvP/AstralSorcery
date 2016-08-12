package hellfirepvp.astralsorcery.client;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.gui.GuiConstellationPaper;
import hellfirepvp.astralsorcery.client.gui.journal.GuiJournalProgression;
import hellfirepvp.astralsorcery.client.gui.GuiTelescope;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.entities.EntityTelescope;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
    public static Object openGui(EnumClientGui guiType, EntityPlayer player, World world, int x, int y, int z) {
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
            case JOURNAL:
                return new GuiJournalProgression();
            default:
                return null;
        }
    }

    public static enum EnumClientGui {

        TELESCOPE,
        CONSTELLATION_PAPER,
        JOURNAL

    }

}
