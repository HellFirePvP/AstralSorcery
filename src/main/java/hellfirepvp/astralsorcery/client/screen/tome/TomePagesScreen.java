/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.screen.element.TomeNavArrowElement;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPage;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPageConstellation;
import hellfirepvp.astralsorcery.client.screen.tome.page.RenderPageLumenDescription;
import hellfirepvp.astralsorcery.client.sound.PlayableSoundInstance;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderQuadUtil;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.client.util.tooltip.DeferredTooltipUtil;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBindingType;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.tome.*;
import hellfirepvp.astralsorcery.common.util.RecipeFinder;
import hellfirepvp.astralsorcery.common.util.RecipeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.extensions.IHolderExtension;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomePagesScreen
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TomePagesScreen extends TomeScreen {

    private static boolean saveSite = true;

    private final Component title;
    @Nullable private final TomeResearchScreen origin;
    @Nullable private final Screen previous;
    private final List<RenderPage> renderPages;

    private boolean informClose = true;
    private int currentDoublePageOffset = 0;
    private TomeNavArrowElement pageLeft, pageRight, pageBack;

    private boolean capturePageWidgets = false;
    private final List<GuiEventListener> widgets = new ArrayList<>();

    private TomePagesScreen(@Nullable Screen screen,
                            @Nullable TomeResearchScreen origin,
                            Component title,
                            Supplier<List<TomePage>> pageProvider,
                            BiFunction<TomePage, Integer, RenderPage> renderPageProvider,
                            int currentPage) {
        super(TomeScreen.NO_BOOKMARK);
        this.title = title;
        this.origin = origin;
        this.previous = screen;
        this.currentDoublePageOffset = currentPage / 2;
        List<TomePage> pageList = pageProvider.get();
        this.renderPages = new ArrayList<>(pageList.size());
        for (int i = 0; i < pageList.size(); i++) {
            TomePage page = pageList.get(i);
            this.renderPages.add(renderPageProvider.apply(page, i));
        }
    }

    public static TomePagesScreen fromProgressNode(TomeResearchScreen origin, ResearchNode node) {
        return new TomePagesScreen(null, origin, node.getName(), node::getPages, pageFactory(node), 0);
    }

    public static TomePagesScreen fromResearchNode(ResearchNode node, int page) {
        return new TomePagesScreen(Minecraft.getInstance().screen, null, node.getName(), node::getPages, pageFactory(node), page);
    }

    public static TomePagesScreen fromLumen(TomeLumenScreen screen, Lumen lumen) {
        String descriptionKey = String.format("tome.research.lumen.astralsorcery.%s.description", lumen.getRegistryKey().map(key -> key.location().getPath()).orElse("unknown"));
        List<TomePage> pages = new ArrayList<>();
        pages.add(new TomePageText(descriptionKey));

        ResourceKey<RecipeType<?>> type = RecipeTypesAS.LUMEN_GENERATION_TYPE.getKey();
        pages.add(Optional.ofNullable(Minecraft.getInstance().getConnection())
                .map(ClientPacketListener::getRecipeManager)
                .flatMap(mgr -> RecipeFinder.of(mgr).findLumenGenerationRecipeByOutput(lumen))
                .map(recipe -> (TomePage) new TomePageRecipe(type, recipe.id()))
                .orElse(TomePageEmpty.getInstance()));

        pages.add(new TomePageLumenDescription(lumen, List.of(
                LumenBindingType.SlotType.HELMET,
                LumenBindingType.SlotType.CHESTPLATE,
                LumenBindingType.SlotType.LEGGINGS,
                LumenBindingType.SlotType.BOOTS)));
        pages.add(new TomePageLumenDescription(lumen, List.of(
                LumenBindingType.SlotType.MELEE_WEAPON,
                LumenBindingType.SlotType.RANGED_WEAPON,
                LumenBindingType.SlotType.TOOL)));
        return new TomePagesScreen(screen, null, lumen.getHoverName(), () -> pages, pageFactory(null), 0);
    }

    public static TomePagesScreen fromConstellation(TomeConstellationScreen screen, BaseConstellation cst) {
        List<TomePage> pages = new ArrayList<>();
        pages.add(new TomePageConstellation(cst));
        pages.add(new TomePageConstellation(cst));

        return new TomePagesScreen(screen, null, cst.getName(), () -> pages, pageFactory(null), 0);
    }

    private static BiFunction<TomePage, Integer, RenderPage> pageFactory(@Nullable ResearchNode node) {
        return (page, pageNum) -> page.createPage(node, pageNum);
    }

    @Override
    protected void init() {
        super.init();

        this.clearWidgets();

        if (this.origin != null) {
            this.origin.preventViewRefresh();
            this.origin.width = this.width;
            this.origin.height = this.height;
            this.origin.init();
        }

        this.initBookmarks();
        this.initNavArrows();
        this.initCurrentPages();
    }

    private void initNavArrows() {
        this.pageLeft = new TomeNavArrowElement(this.screenLeft + (this.screenWidth / 2) - 170, this.screenTop + 230, false, () -> {
            this.currentDoublePageOffset = Math.max(0, this.currentDoublePageOffset - 1);
            PlayableSoundInstance.of(SoundsAS.SCREEN_TOME_PAGE).forUI().play();
            this.initCurrentPages();
        });
        this.pageRight = new TomeNavArrowElement(this.screenLeft + (this.screenWidth / 2) + 170, this.screenTop + 230, true, () -> {
            this.currentDoublePageOffset = Math.min((this.renderPages.size() - 1) / 2, this.currentDoublePageOffset + 1);
            PlayableSoundInstance.of(SoundsAS.SCREEN_TOME_PAGE).forUI().play();
            this.initCurrentPages();
        });
        this.pageBack = new TomeNavArrowElement(this.screenLeft + (this.screenWidth / 2), this.screenTop + 240, false, () -> {
            if (this.origin != null) {
                this.origin.expectNewInit();
                saveSite = false;
            } else {
                this.informClose = false;
            }
            this.handleClose();
        });
        this.pageLeft.visible = false;
        this.pageRight.visible = false;
        this.pageBack.visible = true;
        this.addRenderableWidget(this.pageLeft);
        this.addRenderableWidget(this.pageRight);
        this.addRenderableWidget(this.pageBack);
    }

    private void initCurrentPages() {
        this.widgets.forEach(this::removeWidget);
        this.widgets.clear();
        this.capturePageWidgets = true;
        for (int i = this.currentDoublePageOffset * 2; i < this.renderPages.size() && i < (this.currentDoublePageOffset + 1) * 2; i++) {
            RenderPage page = this.renderPages.get(i);
            page.init(this, this.screenLeft + (i % 2 == 0 ? 30 : 215), this.screenTop + 20);
        }
        this.capturePageWidgets = false;
    }

    @Override
    public <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget) {
        if (this.capturePageWidgets) {
            this.widgets.add(widget);
        }
        return super.addRenderableWidget(widget);
    }

    @Override
    public void tick() {
        super.tick();
        this.renderPages.forEach(RenderPage::tick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
        this.pageLeft.visible = this.currentDoublePageOffset > 0;
        this.pageRight.visible = (this.currentDoublePageOffset + 1) * 2 < this.renderPages.size();
        int pageIndex = this.currentDoublePageOffset * 2;
        RenderPage leftPage = this.renderPages.size() > pageIndex ? this.renderPages.get(pageIndex) : null;

        DeferredTooltipUtil.captureTooltips();
        int offsetY = 22;

        if (this.renderPages.size() > pageIndex) {
            RenderPage left = this.renderPages.get(pageIndex);
            left.preRender(guiGraphics, this.getScreenLeft() + 30, this.getScreenTop() + 22, partialTick, mouseX, mouseY);
        }
        pageIndex += 1;
        if (this.renderPages.size() > pageIndex) {
            RenderPage right = this.renderPages.get(pageIndex);
            right.preRender(guiGraphics, this.getScreenLeft() + 215, this.getScreenTop() + 22, partialTick, mouseX, mouseY);
        }

        AbstractRenderTexture tex = TexturesAS.SCREEN_TOME_FRAME_FULL;
        if (leftPage instanceof RenderPageConstellation) { //Special case for constellation detail pages
            tex = TexturesAS.SCREEN_TOME_FRAME_LEFT;
        }
        RenderingDrawUtil.drawTexturedRect(guiGraphics.pose(), tex, this.getScreenRectangle());
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (this.currentDoublePageOffset == 0 && !(leftPage instanceof RenderPageConstellation)) {
            Font font = Minecraft.getInstance().font;
            float offsetCenter = 20 + TomePage.DEFAULT_WIDTH / 2F;
            int width = font.width(this.title);
            float offset = this.getScreenLeft() + offsetCenter - width / 2F;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(offset, this.getScreenTop() + offsetY, 0);
            guiGraphics.pose().scale(1.3F, 1.3F, 1F);
            guiGraphics.drawString(font, this.title, 0, 0, ColorsAS.TOME_TEXT_COLOR.getColor(), true);
            guiGraphics.pose().popPose();

            RenderSystem.enableBlend();
            Blending.DEFAULT.apply();
            TexturesAS.SCREEN_TOME_UNDERLINE.bindTexture();
            RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
                RenderQuadUtil.rect(buf, guiGraphics.pose(), this.getScreenLeft() + 30, this.getScreenTop() + 35, TomePage.DEFAULT_WIDTH, 13)
                        .draw();
            });
            RenderSystem.disableBlend();

            offsetY += 28;
        }

        RenderSystem.enableDepthTest();

        pageIndex = this.currentDoublePageOffset * 2;
        if (this.renderPages.size() > pageIndex) {
            RenderPage left = this.renderPages.get(pageIndex);
            left.render(guiGraphics, this.getScreenLeft() + 30, this.getScreenTop() + offsetY, partialTick, mouseX, mouseY);
        }
        pageIndex += 1;
        if (this.renderPages.size() > pageIndex) {
            RenderPage right = this.renderPages.get(pageIndex);
            right.render(guiGraphics, this.getScreenLeft() + 215, this.getScreenTop() + 22, partialTick, mouseX, mouseY);
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 100);

        pageIndex = this.currentDoublePageOffset * 2;
        if (this.renderPages.size() > pageIndex) {
            RenderPage left = this.renderPages.get(pageIndex);
            left.postRender(guiGraphics, this.getScreenLeft() + 30, this.getScreenTop() + offsetY, partialTick, mouseX, mouseY);
        }
        pageIndex += 1;
        if (this.renderPages.size() > pageIndex) {
            RenderPage right = this.renderPages.get(pageIndex);
            right.postRender(guiGraphics, this.getScreenLeft() + 215, this.getScreenTop() + 22, partialTick, mouseX, mouseY);
        }
        guiGraphics.pose().popPose();

        DeferredTooltipUtil.stopCapturingTooltips().forEach(Runnable::run);
    }

    @Override
    public void onClose() {
        this.handleClose();
    }

    private void handleClose() {
        if (this.origin != null) {
            if (saveSite) {
                TomeResearchScreen.OPEN_INSTANCE = this;
                this.origin.preventViewRefresh();
                PlayableSoundInstance.of(SoundsAS.SCREEN_TOME_CLOSE).forUI().play();
                Minecraft.getInstance().setScreen(null);
            } else {
                saveSite = true;
                TomeResearchScreen.OPEN_INSTANCE = this.origin;
                PlayableSoundInstance.of(SoundsAS.SCREEN_TOME_PAGE).forUI().play();
                Minecraft.getInstance().setScreen(this.origin);
            }
        } else {
            if (this.previous != null && this.informClose) {
                PlayableSoundInstance.of(SoundsAS.SCREEN_TOME_PAGE).forUI().play();
                this.previous.onClose();
            } else {
                PlayableSoundInstance.of(SoundsAS.SCREEN_TOME_CLOSE).forUI().play();
            }
            Minecraft.getInstance().setScreen(this.previous);
        }
    }

    @Override
    protected boolean shouldRightClickCloseScreen(double mouseX, double mouseY) {
        if (this.origin != null) {
            this.origin.expectNewInit();
            saveSite = false;
        } else {
            this.informClose = false;
        }
        return true;
    }

    @Override
    protected void mouseDragTick(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY, double mouseOffsetX, double mouseOffsetY) {
        super.mouseDragTick(mouseX, mouseY, mouseDiffX, mouseDiffY, mouseOffsetX, mouseOffsetY);

        this.propagateToPages(page -> {
            if (page.propagateMouseDrag(mouseX, mouseY, mouseOffsetX, mouseOffsetY)) {
                return true;
            }
            return null;
        });
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }

        return this.propagateToPages(page -> {
            if (page.propagateMouseScroll(mouseX, mouseY, scrollX, scrollY)) {
                return true;
            }
            return null;
        }).orElse(false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if (button == 1) return true; //Rightlick still passed, just ignore the usage
        if (button != 0) return false; //Anything but left click doesn't have further handling

        return this.propagateToPages(page -> {
            if (page.propagateMouseClick(mouseX, mouseY)) {
                return true;
            }
            return null;
        }).orElse(false);
    }

    private <T> Optional<T> propagateToPages(Function<RenderPage, T> pageFn) {
        int pageIndex = this.currentDoublePageOffset * 2;
        if (this.renderPages.size() > pageIndex) {
            RenderPage left = this.renderPages.get(pageIndex);
            if (left != null) {
                T result = pageFn.apply(left);
                if (result != null) {
                    return Optional.of(result);
                }
            }
        }

        pageIndex += 1;
        if (this.renderPages.size() > pageIndex) {
            RenderPage right = this.renderPages.get(pageIndex);
            if (right != null) {
                T result = pageFn.apply(right);
                if (result != null) {
                    return Optional.of(result);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void doBookmarkClick(BookmarkProvider provider) {
        super.doBookmarkClick(provider);
        saveSite = false;
    }
}
