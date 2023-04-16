package cum.xiaomao.zerohack.manager.managers

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import cum.xiaomao.zerohack.ZeroHackMod
import cum.xiaomao.zerohack.command.CommandManager
import cum.xiaomao.zerohack.event.events.InputEvent
import cum.xiaomao.zerohack.event.listener
import cum.xiaomao.zerohack.manager.Manager
import cum.xiaomao.zerohack.util.ConfigUtils
import cum.xiaomao.zerohack.util.text.MessageSendUtils
import java.io.File

object MacroManager : Manager() {
    private var macroMap = List<MutableList<String>>(256, ::ArrayList)
    val isEmpty get() = macroMap.isEmpty()
    val macros: List<List<String>> get() = macroMap

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val parser = JsonParser()
    private val file get() = File("${ZeroHackMod.DIRECTORY}/macros.json")

    init {
        listener<InputEvent.Keyboard> {
            if (it.state) {
                sendMacro(it.key)
            }
        }
    }

    fun loadMacros(): Boolean {
        ConfigUtils.fixEmptyJson(file)

        return try {
            val jsonObject = parser.parse(file.readText())
            val newMap = List<ArrayList<String>>(256, ::ArrayList)
            for ((id, list) in jsonObject.asJsonObject.entrySet()) {
                list.asJsonArray.mapTo(newMap[id.toInt()]) { it.asString }
            }
            macroMap = newMap
            ZeroHackMod.logger.info("Macro loaded")
            true
        } catch (e: Exception) {
            ZeroHackMod.logger.warn("Failed loading macro", e)
            false
        }
    }

    fun saveMacros(): Boolean {
        return try {
            val jsonObject = JsonObject()
            for (i in macroMap.indices) {
                jsonObject.add(i.toString(), gson.toJsonTree(macroMap[i]))
            }
            file.bufferedWriter().use {
                gson.toJson(jsonObject, it)
            }
            ZeroHackMod.logger.info("Macro saved")
            true
        } catch (e: Exception) {
            ZeroHackMod.logger.warn("Failed saving macro", e)
            false
        }
    }

    /**
     * Sends the message or command, depending on which one it is
     * @param keyCode int keycode of the key the was pressed
     */
    private fun sendMacro(keyCode: Int) {
        val macros = getMacros(keyCode)
        for (macro in macros) {
            if (macro.startsWith(CommandManager.prefix)) { // this is done instead of just sending a chat packet so it doesn't add to the chat history
                MessageSendUtils.sendTrollCommand(macro) // ie, the false here
            } else {
                MessageManager.sendMessageDirect(macro)
            }
        }
    }

    fun getMacros(keycode: Int) = macroMap[keycode]

    fun setMacro(keycode: Int, macro: String) {
        val list = macroMap[keycode]
        list.clear()
        list.add(macro)
    }

    fun addMacro(keycode: Int, macro: String) {
        macroMap[keycode].add(macro)
    }

    fun removeMacro(keycode: Int) {
        macroMap[keycode].clear()
    }

}