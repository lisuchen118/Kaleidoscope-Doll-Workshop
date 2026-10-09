package lsc.kaleidoscopeDollWorkshop.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.menu.ComputerMenu;
import lsc.kaleidoscopeDollWorkshop.network.CraftDollPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class ComputerScreen extends AbstractContainerScreen<ComputerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(KaleidoscopeDollWorkshop.MOD_ID, "textures/gui/computer.png");
    private static final ResourceLocation OUTPUT_SLOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(KaleidoscopeDollWorkshop.MOD_ID, "textures/slot/computer_output_slot.png");

    private EditBox nameField; // 玩家名输入框
    private Button craftButton; // 制作按钮

    public ComputerScreen(ComputerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 128;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
    }

    @Override
    protected void init() {
        super.init();

        // 初始化输入框
        this.nameField = new EditBox(this.font, this.leftPos + 10, this.topPos + 22, 68, 12, Component.literal("Player Name"));
        this.nameField.setMaxLength(16);
        this.nameField.setBordered(false);
        this.nameField.setCanLoseFocus(true);
        this.nameField.setTextColor(0xFFFFFF);
        this.addRenderableWidget(this.nameField);

        // 初始化制作按钮
        this.craftButton = Button.builder(Component.literal("✔"), button -> {
            sendCraftPacket(hasShiftDown()); // 发送制作网络包
        }).bounds(this.leftPos + 80, this.topPos + 18, 16, 16).build();
        this.addRenderableWidget(this.craftButton);
    }

    // 隐藏原版默认渲染的“Inventory”文字
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled = super.mouseClicked(mouseX, mouseY, button);
        // 处理输入框失焦逻辑
        if (!this.nameField.isMouseOver(mouseX, mouseY)) {
            this.nameField.setFocused(false);
        }
        if (!this.nameField.isMouseOver(mouseX, mouseY) && !this.craftButton.isMouseOver(mouseX, mouseY)) {
            this.setFocused(null);
        }
        return handled;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 1. 按下 ESC 直接关闭界面
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }

        // 2. 输入框聚焦时，拦截按键防止触发 E 键关闭背包
        if (this.nameField.isFocused()) {
            if (this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
                return true;
            }
            return this.nameField.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // 发送制作请求到服务端
    private void sendCraftPacket(boolean craftAll) {
        String name = nameField.getValue();
        if (name != null && !name.isEmpty()) {
            PacketDistributor.sendToServer(new CraftDollPayload(name, craftAll));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 根据焦点状态更新输入框文字颜色
        if (this.nameField.isFocused()) {
            this.nameField.setTextColor(0x39ff56); // 绿色
        } else {
            this.nameField.setTextColor(0xFFFFFF); // 白色
        }

        // 渲染背景和组件
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // 渲染占位符提示文字
        if (!this.nameField.isFocused() && this.nameField.getValue().isEmpty()) {
            guiGraphics.drawString(this.font, Component.translatable("gui.kaleidoscope_doll_workshop.computer.search"),
                    this.nameField.getX(), this.nameField.getY(), 0xAAAAAA, false);
        }

        // 渲染输入槽悬浮提示
        renderInputSlotTooltip(guiGraphics, mouseX, mouseY);

        // 渲染标准 Tooltip
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderInputSlotTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int slotX = this.leftPos + 108;
        int slotY = this.topPos + 18;
        if (mouseX >= slotX && mouseX < slotX + 16 && mouseY >= slotY && mouseY < slotY + 16) {
            if (!this.menu.getSlot(0).hasItem()) {
                guiGraphics.renderTooltip(this.font, Component.translatable("tooltip.kaleidoscope_doll_workshop.computer.input"), mouseX, mouseY);
            }
        }
    }

    // 渲染 GUI 背景图
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // 如果输出槽为空，绘制槽位背景提示
        if (!this.menu.getSlot(1).hasItem()) {
            guiGraphics.blit(OUTPUT_SLOT_TEXTURE, x + 151, y + 18, 0, 0, 16, 16, 16, 16);
        }
    }
}