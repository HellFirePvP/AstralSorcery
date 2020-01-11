/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.perk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkRenderGroup
 * Created by HellFirePvP
 * Date: 08.08.2019 / 17:45
 */
@OnlyIn(Dist.CLIENT)
public class PerkRenderGroup {

    private static int counter = 0;
    private final int id;

    private List<BatchPerkContext.TextureObjectGroup> addedGroups = Lists.newArrayList();
    private Map<AbstractRenderableTexture, Integer> underlyingTextures = Maps.newHashMap();

    public PerkRenderGroup() {
        this.id = counter++;
    }

    public void add(AbstractRenderableTexture texture, Integer priority) {
        this.underlyingTextures.put(texture, priority);
    }

    public void batchRegister(BatchPerkContext ctx) {
        for (AbstractRenderableTexture tex : underlyingTextures.keySet()) {
            addedGroups.add(ctx.addContext(tex, underlyingTextures.get(tex)));
        }
    }

    @Nullable
    public BatchPerkContext.TextureObjectGroup getGroup(AbstractRenderableTexture texture) {
        return MiscUtils.iterativeSearch(addedGroups, grp -> grp.getResource().equals(texture));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerkRenderGroup that = (PerkRenderGroup) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
