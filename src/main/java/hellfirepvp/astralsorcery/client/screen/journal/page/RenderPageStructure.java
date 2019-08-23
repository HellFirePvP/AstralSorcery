/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.page;

import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import hellfirepvp.observerlib.api.util.BlockArray;
import hellfirepvp.observerlib.client.util.RenderWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.biome.Biomes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderPageStructure
 * Created by HellFirePvP
 * Date: 22.08.2019 / 21:18
 */
public class RenderPageStructure implements RenderablePage {

    private final RenderWorld render;
    private final BlockArray blocks;
    private final Vector3 shift;
    private final List<Tuple<ItemStack, ITextComponent>> contentStacks;
    private final ITextComponent name;
    private long totalRenderFrame = 0;

    private Optional<Integer> drawSlice = Optional.empty();
    private Rectangle switchView = null, sliceUp = null, sliceDown = null;

    public RenderPageStructure(BlockArray structure, @Nullable ITextComponent name, @Nonnull Vector3 shift) {
        this.render = new RenderWorld(structure, Biomes.PLAINS);
        this.blocks = structure;
        this.name = name;
        this.shift = shift;
        this.contentStacks = structure.getAsStacks(this.render).stream()
                .map(stack -> new Tuple<>(stack, new StringTextComponent(stack.getCount() + "x").appendSibling(stack.getDisplayName())))
                .collect(Collectors.toList());
    }

    @Override
    public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {

    }

    //@Override
    //public boolean propagateMouseClick(double mouseX, double mouseZ) {
    //    if (switchView != null && switchView.contains(mouseX, mouseZ)) {
    //        if (drawSlice.isPresent()) {
    //            drawSlice = Optional.empty();
    //        } else {
    //            drawSlice = Optional.of(structRender.getDefaultSlice());
    //        }
    //        SoundHelper.playSoundClient(SoundsAS.GUI_JOURNAL_PAGE, 1F, 1F);
    //        return true;
    //    }
    //    if (sliceUp != null && drawSlice.isPresent() && sliceUp.contains(mouseX, mouseZ)) {
    //        drawSlice = Optional.of(drawSlice.get() + 1);
    //        SoundHelper.playSoundClient(SoundsAS.GUI_JOURNAL_PAGE, 1F, 1F);
    //        return true;
    //    }
    //    if (sliceDown != null && drawSlice.isPresent() && sliceDown.contains(mouseX, mouseZ)) {
    //        drawSlice = Optional.of(drawSlice.get() - 1);
    //        SoundHelper.playSoundClient(SoundsAS.GUI_JOURNAL_PAGE, 1F, 1F);
    //        return true;
    //    }
    //    return false;
    //}
}
