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
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SectionWorldData
 * Created by HellFirePvP
 * Date: 29.05.2019 / 21:55
 */
public abstract class SectionWorldData<T extends WorldSection> extends CachedWorldData {

    public static final int PRECISION_SECTION = 5;
    public static final int PRECISION_CHUNK = 4;

    private Map<SectionKey, T> sections = new HashMap<>();
    private final int precision;

    private Set<SectionKey> dirtySections = new HashSet<>();
    private Set<SectionKey> removedSections = new HashSet<>();

    protected SectionWorldData(WorldCacheManager.SaveKey key, int sectionPrecision) {
        super(key);
        this.precision = sectionPrecision;
    }

    public void markDirty(Vec3i absolute) {
        SectionKey key = SectionKey.resolve(absolute, this.precision);
        T section = getSection(key);
        if (section != null) {
            this.dirtySections.add(key);
        }
    }

    public void markDirty(T section) {
        this.dirtySections.add(SectionKey.from(section));
    }

    protected abstract T createNewSection(int sectionX, int sectionZ);

    @Nonnull
    public T getOrCreateSection(Vec3i absolute) {
        SectionKey key = SectionKey.resolve(absolute, this.precision);
        return this.sections.computeIfAbsent(key, sectionKey -> createNewSection(sectionKey.x, sectionKey.z));
    }

    @Nullable
    public T getSection(Vec3i absolute) {
        return this.getSection(SectionKey.resolve(absolute, this.precision));
    }

    @Nullable
    private T getSection(SectionKey key) {
        return this.sections.get(key);
    }

    public boolean removeSection(T section) {
        SectionKey key = SectionKey.from(section);
        return this.sections.remove(key) == section && this.removedSections.add(key);
    }

    public boolean removeSection(Vec3i absolute) {
        SectionKey key = SectionKey.resolve(absolute, this.precision);
        return this.sections.remove(key) != null && this.removedSections.add(key);
    }

    @Nonnull
    public Collection<T> getSections() {
        return this.sections.values();
    }

    @Override
    public boolean needsSaving() {
        return !this.dirtySections.isEmpty();
    }

    @Override
    public void markSaved() {
        this.dirtySections.clear();
    }

    public abstract void writeToNBT(NBTTagCompound nbt);

    public abstract void readFromNBT(NBTTagCompound nbt);

    private File getSaveFile(File directory, T section) {
        String name = String.format("%s_%s_%s.dat",
                this.getSaveKey().getIdentifier(),
                section.getSectionX(),
                section.getSectionZ());
        return directory.toPath().resolve(name).toFile();
    }

    @Override
    public final void writeData(File baseDirectory, File backupDirectory) throws IOException {
        File generalSaveFile = new File(baseDirectory, "general.dat");
        if (generalSaveFile.exists()) {
            try {
                Files.copy(generalSaveFile, new File(backupDirectory, "general.dat"));
            } catch (Exception exc) {
                AstralSorcery.log.info("Copying '" + getSaveKey().getIdentifier() + "' general actual file to its backup file failed!");
                exc.printStackTrace();
            }
        } else {
            generalSaveFile.createNewFile();
        }
        NBTTagCompound generalData = new NBTTagCompound();
        this.writeToNBT(generalData);
        CompressedStreamTools.write(generalData, generalSaveFile);

        for (SectionKey key : this.dirtySections) {
            T section = getSection(key);
            if (section != null) {

                File saveFile = this.getSaveFile(baseDirectory, section);
                if (saveFile.exists()) {
                    try {
                        Files.copy(saveFile, this.getSaveFile(backupDirectory, section));
                    } catch (Exception exc) {
                        AstralSorcery.log.info("Copying '" + getSaveKey().getIdentifier() + "' actual file to its backup file failed!");
                        exc.printStackTrace();
                    }
                } else {
                    saveFile.createNewFile();
                }

                NBTTagCompound data = new NBTTagCompound();
                section.writeToNBT(data);
                CompressedStreamTools.write(data, saveFile);
            }
        }
    }

    @Override
    public final void readData(File baseDirectory) throws IOException {
        String identifier = getSaveKey().getIdentifier();

        File generalSaveFile = new File(baseDirectory, "general.dat");
        if (generalSaveFile.exists()) {
            NBTTagCompound tag = CompressedStreamTools.read(generalSaveFile);
            this.readFromNBT(tag);
        } else {
            this.readFromNBT(new NBTTagCompound());
        }

        for (File subFile : baseDirectory.listFiles()) {
            String fileName = subFile.getName();
            if (!fileName.endsWith(".dat")) {
                continue;
            }
            fileName = fileName.substring(fileName.length() - 4);
            String[] ptrn = fileName.split("_");
            if (ptrn.length != 3 || ptrn[0].equalsIgnoreCase(identifier)) {
                continue;
            }
            int sX, sZ;
            try {
                sX = Integer.parseInt(ptrn[1]);
                sZ = Integer.parseInt(ptrn[2]);
            } catch (NumberFormatException exc) {
                continue;
            }

            T section = createNewSection(sX, sZ);
            section.readFromNBT(CompressedStreamTools.read(subFile));
            this.sections.put(new SectionKey(sX, sZ), section);
        }
    }

    private static class SectionKey {

        private final int x, z;

        private SectionKey(int x, int z) {
            this.x = x;
            this.z = z;
        }

        private static SectionKey from(WorldSection section) {
            return new SectionKey(section.getSectionX(), section.getSectionZ());
        }

        private static SectionKey resolve(Vec3i absolute, int shift) {
            return new SectionKey(absolute.getX() >> shift, absolute.getZ() >> shift);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SectionKey that = (SectionKey) o;
            return x == that.x && z == that.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, z);
        }
    }

}
