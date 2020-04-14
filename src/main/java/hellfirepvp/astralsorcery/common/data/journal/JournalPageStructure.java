/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.journal;

import hellfirepvp.astralsorcery.client.screen.journal.page.RenderPageStructure;
import hellfirepvp.astralsorcery.client.screen.journal.page.RenderablePage;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.api.util.BlockArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageStructure
 * Created by HellFirePvP
 * Date: 22.08.2019 / 21:17
 */
public class JournalPageStructure implements JournalPage {

    private BlockArray structure;
    private ITextComponent name;
    private Vector3 shift;

    public JournalPageStructure(BlockArray struct) {
        this(struct, null);
    }

    public JournalPageStructure(BlockArray struct, @Nullable ITextComponent name) {
        this(struct, name, new Vector3());
    }

    public JournalPageStructure(BlockArray struct, @Nullable ITextComponent name, @Nonnull Vector3 shift) {
        this.structure = struct;
        this.name = name;
        this.shift = shift;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderablePage buildRenderPage(ResearchNode node, int nodePage) {
        return new RenderPageStructure(node, nodePage, structure, name, shift.clone());
    }
}
