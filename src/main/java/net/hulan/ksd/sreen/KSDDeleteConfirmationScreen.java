package net.hulan.ksd.sreen;

import com.mojang.blaze3d.vertex.PoseStack;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.mappings.ScreenMapper;
import mtr.mappings.Text;
import mtr.mappings.UtilitiesClient;
import net.minecraft.client.gui.components.Button;

public class KSDDeleteConfirmationScreen extends ScreenMapper {

    private final Runnable deleteCallback;
    private final String name;
    private final KSDDashboardScreen dashboardScreen;
    private final Button buttonYes;
    private final Button buttonNo;

    public KSDDeleteConfirmationScreen(Runnable deleteCallback, String name, KSDDashboardScreen dashboardScreen) {
        super(Text.literal(""));
        this.deleteCallback = deleteCallback;
        this.name = name;
        this.dashboardScreen = dashboardScreen;
        this.buttonYes = UtilitiesClient.newButton(Text.translatable("gui.yes"), (button) -> this.onYes());
        this.buttonNo = UtilitiesClient.newButton(Text.translatable("gui.no"), (button) -> this.onNo());
    }

    protected void init() {
        super.init();
        IDrawing.setPositionAndWidth(this.buttonYes, this.width / 2 - 100 - 10, this.height / 2, 100);
        IDrawing.setPositionAndWidth(this.buttonNo, this.width / 2 + 10, this.height / 2, 100);
        this.addDrawableChild(this.buttonYes);
        this.addDrawableChild(this.buttonNo);
    }

    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        try {
            this.renderBackground(matrices);
            super.render(matrices, mouseX, mouseY, delta);
            drawCenteredString(matrices, this.font, Text.translatable("gui.mtr.delete_confirmation", IGui.formatStationName(this.name)), this.width / 2, this.height / 2 - 40 + 6, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClose() {
        super.onClose();
        if (this.minecraft != null) {
            UtilitiesClient.setScreen(this.minecraft, this.dashboardScreen);
        }
    }

    private void onYes() {
        this.deleteCallback.run();
        this.onClose();
    }

    private void onNo() {
        this.onClose();
    }
}
