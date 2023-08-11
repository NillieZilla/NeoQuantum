package com.pixelmoncreate.neoquantum.network;

import com.pixelmoncreate.neoquantum.NeoQuantum;
import com.pixelmoncreate.neoquantum.util.MismatchedVersionsException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.mojang.text2speech.Narrator.LOGGER;

public final class Network {
    public static final String VERSION = "nquantum-net-14";
    private static final Pattern NET_VERSION_PATTERN = Pattern.compile("nquantum-net-\\d+$");
    private static final Pattern MOD_VERSION_PATTERN = Pattern.compile("^\\d+\\.\\d+\\.\\d+$");

    public static SimpleChannel channel;

    static {
        channel = NetworkRegistry.ChannelBuilder.named(NeoQuantum.getId("network"))
                .clientAcceptedVersions(s -> Objects.equals(s, VERSION))
                .serverAcceptedVersions(s -> Objects.equals(s, VERSION))
                .networkProtocolVersion(() -> VERSION)
                .simpleChannel();

        channel.messageBuilder(LoginPacket.Reply.class, 3)
                .loginIndex(LoginPacket::getLoginIndex, LoginPacket::setLoginIndex)
                .decoder(buffer -> new LoginPacket.Reply())
                .encoder((msg, buffer) -> {})
                .consumerMainThread(HandshakeHandler.indexFirst((hh, msg, ctx) -> msg.handle(ctx)))
                .add();
        // uwu
        channel.messageBuilder(PlayMessages.SpawnEntity.class, 7)
                .encoder(PlayMessages.SpawnEntity::encode)
                .decoder(PlayMessages.SpawnEntity::decode)
                .consumerMainThread(PlayMessages.SpawnEntity::handle)
                .add();
        channel.messageBuilder(KeyPressOnItemPacket.class, 9, NetworkDirection.PLAY_TO_SERVER)
                .decoder(KeyPressOnItemPacket::decode)
                .encoder(KeyPressOnItemPacket::encode)
                .consumerMainThread(KeyPressOnItemPacket::handle)
                .add();
    }

    private Network() {}

    public static void init() {}

    static void writeModVersionInfoToNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(Network.VERSION);
    }

    static void verifyNetworkVersion(FriendlyByteBuf buffer) {

        String serverNetVersion = readNetworkVersion(buffer);
        String serverModVersion = readModVersion(buffer);

        LOGGER.debug("Read NeoQuantum server version as {} ({})", serverModVersion, serverNetVersion);

        if (!Network.VERSION.equals(serverNetVersion)) {
            String msg = String.format("This server is running a different version of NeoQuantum. Try updating Silent Gear on the client and/or server. Client version is %s (%s) and server version is %s (%s).",
                    NeoQuantum.getVersion(),
                    Network.VERSION,
                    serverModVersion,
                    serverNetVersion);
            throw new MismatchedVersionsException(msg);
        }
    }

    private static String readNetworkVersion(FriendlyByteBuf buffer) {
        String str = buffer.readUtf(16);
        if (!NET_VERSION_PATTERN.matcher(str).matches()) {
            return "UNKNOWN (received: " + str + ")";
        }
        return str;
    }

    private static String readModVersion(FriendlyByteBuf buffer) {
        String str = buffer.readUtf(16);
        if (!"NONE".equals(str) && !MOD_VERSION_PATTERN.matcher(str).matches()) {
            return "UNKNOWN (received: " + str + ")";
        }
        return str;
    }
}
