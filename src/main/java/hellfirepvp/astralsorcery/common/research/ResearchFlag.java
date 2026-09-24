/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.asm.enumextension.ExtensionInfo;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;

import java.util.Locale;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchFlag
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public enum ResearchFlag implements StringRepresentable, IExtensibleEnum {

    HAS_OBTAINED_ARTIFACT;

    private final boolean isShareable;

    ResearchFlag() {
        this(true);
    }

    ResearchFlag(boolean isShareable) {
        this.isShareable = isShareable;
    }

    public boolean isShareable() {
        return this.isShareable;
    }

    public void setIfAbsent(ServerPlayer sPlayer) {
        PlayerProgress prog = ResearchManager.getProgress(sPlayer, LogicalSide.SERVER);
        if (prog.isValid() && !prog.isFlagSet(this)) {
            if (ResearchHelper.setKnowledgeFlag(sPlayer, this)) {
                ResearchMessageHelper.sendResearchFlagDiscovery(sPlayer, this);
            }
        }
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public static ExtensionInfo getExtensionInfo() {
        return ExtensionInfo.nonExtended(ResearchFlag.class);
    }
}
