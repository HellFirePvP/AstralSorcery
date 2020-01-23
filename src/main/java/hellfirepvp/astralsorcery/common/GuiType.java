/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common;

import hellfirepvp.astralsorcery.client.screen.ScreenConstellationPaper;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalProgression;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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
    TOME,
    TELESCOPE;

    public CompoundNBT serializeArguments(Object[] data) {
        try {
            CompoundNBT nbt = new CompoundNBT();
            switch (this) {
                case CONSTELLATION_PAPER:
                    nbt.putString("cst", ((IConstellation) data[0]).getRegistryName().toString());
                    break;
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

    @OnlyIn(Dist.CLIENT)
    public Screen deserialize(CompoundNBT data) {
        try {
            switch (this) {
                case CONSTELLATION_PAPER:
                    return new ScreenConstellationPaper(RegistriesAS.REGISTRY_CONSTELLATIONS.getValue(new ResourceLocation(data.getString("cst"))));
                case TOME:
                    return ScreenJournalProgression.getOpenJournalInstance();
                case TELESCOPE:
                    BlockPos at = NBTHelper.readBlockPosFromNBT(data);

                    //TODO
                    throw new IllegalArgumentException("Unknown GuiType: " + this.name());
                default:
                    throw new IllegalArgumentException("Unknown GuiType: " + this.name());
            }
        } catch (Exception exc) {
            throw new IllegalArgumentException("Invalid Arguments for GuiType: " + this.name(), exc);
        }
    }
}
