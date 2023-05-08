package minescript;

import io.wispforest.owo.ui.parsing.UIParsing;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.text.Text;

public class MineScriptClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		UIParsing.registerFactory("EditBoxWidget", parent -> {
			assert MinecraftClient.getInstance().currentScreen != null;
			int h = (int)((double)MinecraftClient.getInstance().currentScreen.height * 0.6); h = Math.max(h, 150);
			int w = (int)((double)h*1.5);
			return new EditBoxWidget(MinecraftClient.getInstance().textRenderer, 0, 0, w, h, Text.of("Write MineScript code here..."), Text.of(""));
		});
	}
}