/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.world.base;

import com.google.common.io.Files;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.world.CachedWorldData;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.io.IOException;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GlobalWorldData
 * Created by HellFirePvP
 * Date: 29.05.2019 / 21:56
 */
public abstract class GlobalWorldData extends CachedWorldData {

    private boolean dirty = false;
    private final String saveFileName;

    protected GlobalWorldData(WorldCacheManager.SaveKey key) {
        super(key);
        this.saveFileName = key.getIdentifier() + ".dat";
    }

    public final void markDirty() {
        this.dirty = true;
    }

    private File getSaveFile(File directory) {
        return directory.toPath().resolve(this.saveFileName).toFile();
    }

    @Override
    public final void writeData(File baseDirectory, File backupDirectory) throws IOException {
        File saveFile = this.getSaveFile(baseDirectory);
        if (saveFile.exists()) {
            try {
                Files.copy(saveFile, this.getSaveFile(backupDirectory));
            } catch (Exception exc) {
                AstralSorcery.log.info("Copying '" + getSaveKey().getIdentifier() + "' 's actual file to its backup file failed!");
                exc.printStackTrace();
            }
        } else {
            saveFile.createNewFile();
        }

        NBTTagCompound data = new NBTTagCompound();
        this.writeToNBT(data);
        CompressedStreamTools.write(data, saveFile);
    }

    @Override
    public final void readData(File baseDirectory) throws IOException {
        this.readFromNBT(CompressedStreamTools.read(this.getSaveFile(baseDirectory)));
    }

    public abstract void writeToNBT(NBTTagCompound tag);

    public abstract void readFromNBT(NBTTagCompound tag);

    public final boolean needsSaving() {
        return this.dirty;
    }

    public final void markSaved() {
        this.dirty = false;
    }

}
