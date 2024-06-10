package org.teamvoided.iridium.helper

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.teamvoided.iridium.mod.ModConfiguration.Entrypoint

object EntrypointSerializer : KSerializer<Entrypoint> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Entrypoint) {
        if (value.adapter == "") encoder.encodeString(value.value)
        else Entrypoint.serializer().serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): Entrypoint = Entrypoint("", "")
}
