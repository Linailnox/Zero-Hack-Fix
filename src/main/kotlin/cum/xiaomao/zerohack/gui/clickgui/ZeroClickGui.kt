package cum.xiaomao.zerohack.gui.clickgui

import cum.xiaomao.zerohack.gui.AbstractZeroGui
import cum.xiaomao.zerohack.gui.clickgui.component.ModuleButton
import cum.xiaomao.zerohack.gui.clickgui.window.ModuleSettingWindow
import cum.xiaomao.zerohack.gui.rgui.Component
import cum.xiaomao.zerohack.gui.rgui.windows.ListWindow
import cum.xiaomao.zerohack.module.AbstractModule
import cum.xiaomao.zerohack.module.ModuleManager
import cum.xiaomao.zerohack.module.modules.client.ClickGUI
import cum.xiaomao.zerohack.module.modules.client.GuiSetting
import cum.xiaomao.zerohack.util.extension.remove
import cum.xiaomao.zerohack.util.math.vector.Vec2f
import org.lwjgl.input.Keyboard

object ZeroClickGui : AbstractZeroGui<ModuleSettingWindow, AbstractModule>() {

    private val moduleWindows = ArrayList<ListWindow>()

    init {
        val allButtons = ModuleManager.modules
            .groupBy { it.category.displayName }
            .mapValues { (_, modules) -> modules.map { ModuleButton(it) } }

        var posX = 0.0f
        var posY = 0.0f
        val screenWidth = mc.displayWidth / GuiSetting.scaleFactorFloat

        for ((category, buttons) in allButtons) {
            val window = ListWindow(category, posX, posY, 90.0f, 300.0f, Component.SettingGroup.CLICK_GUI)

            window.children.addAll(buttons)
            moduleWindows.add(window)
            posX += 90.0f

            if (posX > screenWidth) {
                posX = 0.0f
                posY += 100.0f
            }
        }

        windowList.addAll(moduleWindows)
    }

    override fun onGuiClosed() {
        super.onGuiClosed()
        setModuleButtonVisibility { true }
    }

    override fun newSettingWindow(element: AbstractModule, mousePos: Vec2f): ModuleSettingWindow {
        return ModuleSettingWindow(element, mousePos.x, mousePos.y)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == Keyboard.KEY_ESCAPE || keyCode == ClickGUI.bind.value.key && !searching && settingWindow?.listeningChild == null) {
            ClickGUI.disable()
        } else {
            super.keyTyped(typedChar, keyCode)

            val string = typedString.remove(' ')

            if (string.isNotEmpty()) {
                setModuleButtonVisibility { moduleButton ->
                    moduleButton.module.name.contains(string, true)
                        || moduleButton.module.alias.any { it.contains(string, true) }
                }
            } else {
                setModuleButtonVisibility { true }
            }
        }
    }

    private fun setModuleButtonVisibility(function: (ModuleButton) -> Boolean) {
        windowList.filterIsInstance<ListWindow>().forEach {
            for (child in it.children) {
                if (child !is ModuleButton) continue
                child.visible = function(child)
            }
        }
    }
}