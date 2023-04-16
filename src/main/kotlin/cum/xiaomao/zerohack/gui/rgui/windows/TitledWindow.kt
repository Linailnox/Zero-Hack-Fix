package cum.xiaomao.zerohack.gui.rgui.windows

import cum.xiaomao.zerohack.module.modules.client.GuiSetting
import cum.xiaomao.zerohack.util.graphics.font.renderer.MainFontRenderer
import cum.xiaomao.zerohack.util.math.vector.Vec2f

/**
 * Window with rectangle and title rendering
 */
open class TitledWindow(
    name: CharSequence,
    posX: Float,
    posY: Float,
    width: Float,
    height: Float,
    settingGroup: SettingGroup
) : BasicWindow(name, posX, posY, width, height, settingGroup) {
    override val draggableHeight: Float get() = MainFontRenderer.getHeight() + 5.0f

    override val minimizable get() = true

    override fun onRender(absolutePos: Vec2f) {
        super.onRender(absolutePos)
        MainFontRenderer.drawString(name, 3.0f, 1.5f, GuiSetting.text)
    }
}