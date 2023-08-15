package com.pixelmoncreate.neoquantum;

import com.pixelmoncreate.neoquantum.config.Configuration;
import com.pixelmoncreate.neoquantum.lib.registry.NeoTriggerRegistry;
import com.pixelmoncreate.neoquantum.proxy.IProxy;
import com.pixelmoncreate.neoquantum.proxy.Proxy;
import com.pixelmoncreate.neoquantum.util.ModResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Optional;

import static com.pixelmoncreate.neoquantum.util.Const.MOD_ID;
import static com.pixelmoncreate.neoquantum.util.Const.RESOURCE_PREFIX;

@Mod(MOD_ID)
public final class NeoQuantum {

    public static NeoQuantum INSTANCE;
    public static IProxy PROXY;

    public NeoQuantum() {

        INSTANCE = this;
        PROXY = DistExecutor.unsafeRunForDist(() -> Proxy.Client::new, () -> Proxy.Server::new);

    }

    public static String getVersion() {

        Optional<? extends ModContainer> o = ModList.get().getModContainerById(MOD_ID);

        if (o.isPresent()) {

            return o.get().getModInfo().getVersion().toString();

        }

        return "0.0.0";

    }

    @Deprecated
    public static String getVersion(boolean correctInDev) {
        return getVersion();
    }

    public static boolean isDevBuild() {
        return "NONE".equals(getVersion()) || !FMLLoader.isProduction();
    }

    public static ModResourceLocation getId(String path) {

        if (path.contains(":")) {

            throw new IllegalArgumentException("path contains namespace");

        }

        return new ModResourceLocation(path);

    }

    @Nullable
    public static ResourceLocation getIdWithDefaultNamespace(String name) {

        if (name.contains(":"))

            return ResourceLocation.tryParse(name);

        return ResourceLocation.tryParse(RESOURCE_PREFIX + name);

    }

    public static String shortenId(@Nullable ResourceLocation id) {

        if (id == null)

            return "null";

        if (MOD_ID.equals(id.getNamespace()))

            return id.getPath();

        return id.toString();

    }

}