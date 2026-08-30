package dev.dmod4all.client;

import dev.dmod4all.DMod4All;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class RenameScreen extends Screen {
    private static final ResourceLocation RENAME_SPRITE = ResourceLocation.fromNamespaceAndPath(DMod4All.MODID, "textures/gui/container/rename.png");
    private EditBox textField;
    private final LocalPlayer player;
    private final ItemStack itemStack;
    public RenameScreen(LocalPlayer localPlayer, ItemStack itemStack) {
        super(Component.translatable("container.dmod4all.rename"));
        this.player = localPlayer;
        this.itemStack = itemStack;
    }
    @Override
    protected void init() {
        this.textField = new EditBox(this.minecraft.font, this.width / 2 - 97, this.height / 2 + 8, 194, 20, Component.empty());
        this.textField.setMaxLength(50);
        this.textField.setTextColor(0xFFFFFF);
        this.textField.setBordered(false);
        this.addRenderableWidget(this.textField);
        this.setInitialFocus(this.textField);
        String name = RenameManager.getName(itemStack);
        if(name != null) {
            this.textField.setValue(name);
        }
    }
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
        guiGraphics.blit(RENAME_SPRITE, this.width / 2 - 107, this.height / 2 - 15, 0, 0, 214, 44, 214, 44);
        guiGraphics.drawString(this.minecraft.font, Component.translatable("container.dmod4all.rename"), this.width / 2 - 99, this.height / 2 - 9, 0x404040,  false);
        this.textField.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == 257 || keyCode == 335) {
            RenameManager.setName(this.itemStack, this.textField.getValue().trim());
            player.swing(InteractionHand.MAIN_HAND);
            this.minecraft.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override
	public boolean isPauseScreen() {
		return false;
	}
}
