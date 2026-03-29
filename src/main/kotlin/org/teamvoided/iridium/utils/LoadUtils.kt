package org.teamvoided.iridium.utils

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.tree.nodes.TomlKeyValueArray
import com.akuleshov7.ktoml.tree.nodes.TomlKeyValuePrimitive
import com.akuleshov7.ktoml.tree.nodes.TomlNode
import com.akuleshov7.ktoml.tree.nodes.pairs.values.TomlArray
import com.akuleshov7.ktoml.tree.nodes.pairs.values.TomlNull
import com.akuleshov7.ktoml.tree.nodes.pairs.values.TomlValue
import kotlinx.serialization.json.Json


var TOML = Toml.Default
var JSON = Json { prettyPrint = true; prettyPrintIndent = "  " }

fun processNode(map: MutableMap<String, String>, node: TomlNode, rawName: Boolean = false) {
    when (node) {
        is TomlKeyValuePrimitive -> processValue(map, node.value, node, rawName)
        is TomlKeyValueArray -> processValue(map, node.value, node, rawName)

        else -> node.children.forEach { processNode(map, it, rawName) }
    }
}

fun processValue(map: MutableMap<String, String>, value: TomlValue, node: TomlNode, rawName: Boolean) {
    val name = if (rawName) node.name else resolveName(node)

    map[name] = valueToString(value)
}


fun resolveName(node: TomlNode, suffix: String = ""): String {
    return if (node.name == "rootNode") suffix else {
        var name = node.name
        if (suffix != "") name += ".$suffix"
        resolveName(node.parent as TomlNode, name)
    }
}

@Suppress("UNCHECKED_CAST")
fun valueToString(value: TomlValue): String {
    return when (value) {
        is TomlNull -> ""
        is TomlArray ->
            (value.content as List<Any>).filterIsInstance<TomlValue>()
                .joinToString(", ", transform = ::valueToString)

        else -> value.content.toString()
    }
}

