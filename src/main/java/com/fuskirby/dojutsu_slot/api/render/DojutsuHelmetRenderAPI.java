package com.fuskirby.dojutsu_slot.api.render;

import com.fuskirby.dojutsu_slot.api.texture.DojutsuTextureConfig;
import com.fuskirby.dojutsu_slot.api.texture.DojutsuTextureConfig.TextureInfo;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Helmet slot render control API.
 * Allows addon mods to specify which texture a vanilla helmet slot item should use.
 * Texture paths are obtained from the existing DojutsuTextureConfig.
 */
public class DojutsuHelmetRenderAPI {

    private static final DojutsuHelmetRenderAPI INSTANCE = new DojutsuHelmetRenderAPI();
    private final Map<String, Integer> helmetRenderModes = new HashMap<>();

    public enum RenderMode {
        USE_DEFAULT(0),
        USE_LEFT_EYE(1),
        USE_RIGHT_EYE(2),
        AUTO(3);

        private final int value;
        RenderMode(int value) { this.value = value; }
        public int getValue() { return value; }
        public static RenderMode fromValue(int value) {
            for (RenderMode mode : values()) {
                if (mode.value == value) return mode;
            }
            return AUTO;
        }
    }

    private DojutsuHelmetRenderAPI() {}

    public static DojutsuHelmetRenderAPI getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the render mode for a helmet slot item.
     * @param itemId Item ID (format: "modid:itemname")
     * @param mode Render mode
     */
    public void setHelmetRenderMode(String itemId, RenderMode mode) {
        if (itemId == null) return;
        if (mode == RenderMode.AUTO) {
            helmetRenderModes.remove(itemId);
        } else {
            helmetRenderModes.put(itemId, mode.getValue());
        }
    }

    /**
     * Gets the render mode for a helmet slot item.
     * @param itemId Item ID
     * @return Render mode, or AUTO if not set
     */
    public RenderMode getHelmetRenderMode(String itemId) {
        if (itemId == null || !helmetRenderModes.containsKey(itemId)) {
            return RenderMode.AUTO;
        }
        return RenderMode.fromValue(helmetRenderModes.get(itemId));
    }

    /**
     * Removes the render mode setting for a helmet slot item (reverts to AUTO).
     * @param itemId Item ID
     */
    public void removeHelmetRenderMode(String itemId) {
        if (itemId != null) {
            helmetRenderModes.remove(itemId);
        }
    }

    /**
     * Gets the texture that should be used for rendering.
     * @param itemId Item ID
     * @return Texture resource location, or null if the default texture should be used
     */
    public ResourceLocation getTextureForHelmetSlot(String itemId) {
        if (itemId == null) return null;

        RenderMode mode = getHelmetRenderMode(itemId);

        if (mode == RenderMode.AUTO || mode == RenderMode.USE_DEFAULT) {
            return null;
        }

        String[] parts = itemId.split(":");
        if (parts.length != 2) return null;

        TextureInfo texInfo = DojutsuTextureConfig.getTextureInfo(parts[0], parts[1]);
        if (texInfo == null) return null;

        switch (mode) {
            case USE_LEFT_EYE:
                return texInfo.leftEyeTexture != null ? texInfo.leftEyeTexture : texInfo.defaultTexture;
            case USE_RIGHT_EYE:
                return texInfo.rightEyeTexture != null ? texInfo.rightEyeTexture : texInfo.defaultTexture;
            default:
                return null;
        }
    }

    /**
     * Checks whether a helmet slot item requires special rendering.
     * @param itemId Item ID
     * @return Whether a special texture should be used for rendering
     */
    public boolean shouldOverrideHelmetRender(String itemId) {
        if (itemId == null) return false;
        RenderMode mode = getHelmetRenderMode(itemId);
        return mode == RenderMode.USE_LEFT_EYE || mode == RenderMode.USE_RIGHT_EYE;
    }

    /**
     * Gets all items that have a render mode set.
     * @return Map of item ID to render mode
     */
    public Map<String, RenderMode> getAllRenderModes() {
        Map<String, RenderMode> result = new HashMap<>();
        helmetRenderModes.forEach((id, value) -> result.put(id, RenderMode.fromValue(value)));
        return result;
    }

    /**
     * Clears all render mode settings.
     */
    public void clearAllRenderModes() {
        helmetRenderModes.clear();
    }
}