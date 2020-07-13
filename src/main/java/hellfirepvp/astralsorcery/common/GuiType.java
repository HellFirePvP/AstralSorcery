/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common;

import hellfirepvp.astralsorcery.client.screen.ScreenConstellationPaper;
import hellfirepvp.astralsorcery.client.screen.ScreenHandTelescope;
import hellfirepvp.astralsorcery.client.screen.ScreenRefractionTable;
import hellfirepvp.astralsorcery.client.screen.ScreenTelescope;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalProgression;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.tile.TileRefractionTable;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiType
 * Created by HellFirePvP
 * Date: 03.08.2019 / 11:26
 */
public enum GuiType {

    CONSTELLATION_PAPER,
    TOME,
    REFRACTION_TABLE,
    TELESCOPE,
    HAND_TELESCOPE;

    public CompoundNBT serializeArguments(Object[] data) {
        try {
            CompoundNBT nbt = new CompoundNBT();
            switch (this) {
                case CONSTELLATION_PAPER:
                    nbt.putString("cst", ((IConstellation) data[0]).getRegistryName().toString());
                    break;
                case REFRACTION_TABLE:
                case TELESCOPE:
                    NBTHelper.writeBlockPosToNBT((BlockPos) data[0], nbt);
                    break;
                default:
                    break;
            }
            return nbt;
        } catch (Exception exc) {
            throw new IllegalArgumentException("Invalid Arguments for GuiType: " + this.name(), exc);
        }
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public Screen deserialize(CompoundNBT data) {
        World clWorld = Minecraft.getInstance().world;
        PlayerEntity clPlayer = Minecraft.getInstance().player;
        if (clWorld == null || clPlayer == null) {
            return null;
        }

        BlockPos at;
        try {
            switch (this) {
                case CONSTELLATION_PAPER:
                    return new ScreenConstellationPaper(RegistriesAS.REGISTRY_CONSTELLATIONS.getValue(new ResourceLocation(data.getString("cst"))));
                case TOME:
                    return ScreenJournalProgression.getOpenJournalInstance();
                case REFRACTION_TABLE:
                    at = NBTHelper.readBlockPosFromNBT(data);
                    TileRefractionTable refractionTable = MiscUtils.getTileAt(clWorld, at, TileRefractionTable.class, true);
                    if (refractionTable != null) {
                        return new ScreenRefractionTable(refractionTable);
                    }
                    return null;
                case TELESCOPE:
                    at = NBTHelper.readBlockPosFromNBT(data);
                    TileTelescope telescope = MiscUtils.getTileAt(clWorld, at, TileTelescope.class, true);
                    if (telescope != null) {
                        return new ScreenTelescope(telescope);
                    }
                    return null;
                case HAND_TELESCOPE:
                    return new ScreenHandTelescope();
                default:
                    throw new IllegalArgumentException("Unknown GuiType: " + this.name());
            }
        } catch (Exception exc) {
            throw new IllegalArgumentException("Invalid Arguments for GuiType: " + this.name(), exc);
        }
    }
}
