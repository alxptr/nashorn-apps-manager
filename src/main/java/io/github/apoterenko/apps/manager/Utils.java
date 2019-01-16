package io.github.apoterenko.apps.manager;

import com.google.api.client.http.*;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Utils {
    private static Logger logger = LoggerFactory.getLogger(Utils.class);

    private static final String FILE_SEPARATOR = "/";
    private static final String WORKING_DIRECTORY = "a966927e-4b22-11e8-842f-0ed5f89f718b";
    private static final String CONFIG_FILE_NAME = "config.json";

    /**
     * Stable [14.06.2018]
     *
     * @param buffer buffer
     * @return Decoded string
     */
    public static String fromByteBuffer(ByteBuffer buffer) {
        return StandardCharsets.UTF_8.decode(buffer).toString();
    }

    /**
     * Stable [14.06.2018]
     *
     * @param host host
     * @param port port
     * @return SocketAddress
     */
    public static SocketAddress makeSocketAddress(String host, int port) {
        return new InetSocketAddress(host, port);
    }

    /**
     * Stable [11.06.2018]
     *
     * @return True - if production mode enabled
     */
    public static boolean isProductionModeEnabled() {
        return "true".equals(System.getProperty(Constants.PRODUCTION_MODE_PROPERTY));
    }

    /**
     * Stable [11.06.2018]
     *
     * @return True - if need to use local tasks
     */
    public static boolean isNeedToUseLocalTasks() {
        return "true".equals(System.getProperty(Constants.USE_LOCAL_TASKS_PROPERTY));
    }

    /**
     * Stable [25.05.2018]
     *
     * @param t1 t1
     * @param t2 t2
     * @param <T> <T>
     * @return Object
     */
    @SuppressWarnings("WeakerAccess")
    public static <T> T nvl(T t1, T t2) {
        return t1 != null ? t1 : t2;
    }

    /**
     * Stable [25.05.2018]
     *
     * @param jsonAsString jsonAsString
     * @param clazz clazz
     * @param <T> <T>
     * @return Object
     */
    @Nullable
    public static <T> T fromJson(String jsonAsString, Class<T> clazz) {
        return jsonAsString == null ? null : new Gson().fromJson(jsonAsString, clazz);
    }

    /**
     * Stable [27.05.2018]
     *
     * @param jsonAsString jsonAsString
     * @return Map
     */
    @Nullable
    public static Map<String, Object> fromJson(String jsonAsString) {
        return jsonAsString == null
                ? null
                : new Gson().fromJson(jsonAsString, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    /**
     * Stable [01.05.2018]
     *
     * @param o o
     * @return JSON
     */
    public static String toJson(Object o) {
        return new Gson().toJson(o);
    }

    /**
     * Stable [25.05.2018]
     *
     * @param absoluteFilePath absoluteFilePath
     * @return Split absoluteFilePath
     */
    @SuppressWarnings("WeakerAccess")
    public static String[] splitFilePath(String absoluteFilePath) {
        return absoluteFilePath.split(FILE_SEPARATOR);
    }

    /**
     * Stable [25.05.2018]
     *
     * @param separator separator
     * @param data data
     * @return Joined string
     */
    public static String join(String separator, List<?> data) {
        return Joiner.on(separator).join(data);
    }

    /**
     * Stable [25.05.2018]
     *
     * @param data data
     * @return Absolute file path
     */
    public static String toAbsoluteFilePath(String... data) {
        return join(FILE_SEPARATOR, Lists.newArrayList(data));
    }

    /**
     * Stable [25.05.2018]
     *
     * @param data data
     * @return File
     */
    public static File toFile(String... data) {
        return new File(toAbsoluteFilePath(data));
    }

    /**
     * Stable [25.05.2018]
     *
     * @param data data
     * @return Path
     */
    public static Path toFilePath(String... data) {
        return Paths.get(toAbsoluteFilePath(data));
    }

    /**
     * Stable [25.05.2018]
     *
     * @param data data
     * @return Path
     */
    @SuppressWarnings("WeakerAccess")
    public static Path toFilePath(List<String> data) {
        return Paths.get(toAbsoluteFilePath(data.toArray(new String[data.size()])));
    }

    /**
     * Stable [24.05.2018]
     *
     * @return Current directory
     */
    public static String getCurrentDirectory() {
        try {
            return new File(
                    Utils.class.getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()
                            .getPath()
            ).getParentFile().getPath();
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Stable [25.05.2018]
     *
     * @return Working directory
     */
    public static String getWorkingDirectory() {
        return toAbsoluteFilePath(getCurrentDirectory(), WORKING_DIRECTORY);
    }

    /**
     * Stable [25.05.2018]
     *
     * @param directoryPath directoryPath
     * @throws IOException IOException
     */
    public static void destroyDirectoryRecursively(String directoryPath) throws IOException {
        destroyDirectoryRecursively(Paths.get(directoryPath));
    }

    /**
     * Stable [25.05.2018]
     *
     * @param directoryPath directoryPath
     * @throws IOException IOException
     */
    @SuppressWarnings("WeakerAccess")
    public static void destroyDirectoryRecursively(Path directoryPath) throws IOException {
        if (!Files.exists(directoryPath)) {
            return;
        }
        Files.walk(directoryPath, FileVisitOption.FOLLOW_LINKS)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    /**
     * Stable [25.05.2018]
     *
     * @param fileDir fileDir
     * @param fileName fileName
     * @throws IOException IOException
     */
    public static void buildSubDirectories(String fileDir, String fileName) throws IOException {
        final String[] parts = splitFilePath(fileName);
        if (parts.length <= 1) {
            return;
        }
        final List<String> partsList = Lists.newLinkedList();
        partsList.add(fileDir);

        for (int i = 0; i < parts.length - 1; i++) {
            partsList.add(parts[i]);
            createDirectoryIfNotExists(toFilePath(partsList));
        }
    }

    /**
     * Stable [25.05.2018]
     *
     * @param directoryPath directoryPath
     * @throws IOException IOException
     */
    public static void createDirectoryIfNotExists(String directoryPath) throws IOException {
        createDirectoryIfNotExists(Paths.get(directoryPath));
    }

    /**
     * Stable [25.05.2018]
     *
     * @param directoryPath directoryPath
     * @throws IOException IOException
     */
    @SuppressWarnings("WeakerAccess")
    public static void createDirectoryIfNotExists(Path directoryPath) throws IOException {
        if (Files.exists(directoryPath)) {
            return;
        }
        Files.createDirectory(directoryPath);
    }

    /**
     * Stable [24.05.2018]
     *
     * @return Config
     * @throws IOException IOException
     */
    @Nullable
    public static Config loadConfigFile() throws IOException {
        final String configFilePath = toAbsoluteFilePath(getCurrentDirectory(), CONFIG_FILE_NAME);
        if (!Files.exists(Paths.get(configFilePath))) {
            logger.warn(
                    "The config.json does not exist. Will apply a default config {}.",
                    Utils.toJson(Config.DEFAULT_CONFIG)
            );
            return Config.DEFAULT_CONFIG;
        }
        return fromJson(
                readIO(new FileInputStream(configFilePath), true),
                Config.class
        );
    }

    /**
     * Stable [27.05.2018]
     *
     * @param args args
     * @return Flags
     */
    public static Map<String, String> getFlags(String[] args) {
        final Map<String, String> flags = Maps.newHashMap();
        for (String arg : args) {
            final String[] arg0 = arg.split("=");
            final String paramName = arg0[0];
            final String paramValue = arg0.length > 1 ? arg0[1] : null;
            flags.put(paramName.substring(2, paramName.length()), paramValue);
        }
        return Collections.unmodifiableMap(flags);
    }

    /**
     * Stable [25.05.2018]
     *
     * @param path path
     * @throws IOException IOException
     */
    public static void removeFile(Path path) throws IOException {
        Files.deleteIfExists(path);
    }

    /**
     * Stable [25.05.2018]
     *
     * @param data data
     * @param file file
     * @throws IOException IOException
     */
    public static void writeFile(byte[] data, File file) throws IOException {
        OutputStream out = null;
        try {
            IOUtils.write(data, out = new FileOutputStream(file));
        } finally {
            if (out != null) {
                IOUtils.closeQuietly(out);
            }
        }
    }

    /**
     * Stable [25.05.2018]
     *
     * @param resourceName resourceName
     * @return Resource
     * @throws IOException IOException
     */
    @Nullable
    public static String readResourceAsString(String resourceName) throws IOException {
        return readIO(loadResourceAsInputStream(resourceName), true);
    }

    /**
     * Stable [01.05.2018]
     *
     * @param io io
     * @param asString asString
     * @return Content
     * @throws IOException IOException
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T readIO(@Nullable InputStream io, boolean asString) throws IOException {
        try {
            return io == null
                    ? null
                    : asString ? (T) IOUtils.toString(io, "UTF-8") : (T) IOUtils.toByteArray(io);
        } finally {
            if (io != null) {
                IOUtils.closeQuietly(io);
            }
        }
    }

    /**
     * Stable [25.05.2018]
     *
     * @param file file
     * @return File
     * @throws IOException IOException
     */
    @Nullable
    public static byte[] readFile(File file) throws IOException {
        return readIO(new FileInputStream(file), false);
    }

    /**
     * Stable [25.05.2018]
     *
     * @param data data
     * @return BufferedImage
     * @throws IOException IOException
     */
    public static BufferedImage readBufferedImage(byte[] data) throws IOException {
        InputStream io = null;
        try {
            return ImageIO.read(io = new ByteArrayInputStream(data));
        } finally {
            if (io != null) {
                IOUtils.closeQuietly(io);
            }
        }
    }

    /**
     * Stable [27.05.2018]
     *
     * @param factory factory
     * @param path path
     * @param asString asString
     * @param <T> <T>
     * @return Loaded file
     * @throws IOException IOException
     */
    @Nullable
    public static <T> T loadFile(HttpRequestFactory factory, String path, boolean asString) throws IOException {
        return readIO(
                buildGetRequest(factory, path).getContent(),
                asString
        );
    }

    /**
     * Stable [25.05.2018]
     *
     * @param resourceName resourceName
     * @return InputStream
     */
    @Nullable
    @SuppressWarnings("WeakerAccess")
    public static InputStream loadResourceAsInputStream(String resourceName) {
        final ClassLoader loader = Utils.class.getClassLoader();
        return loader.getResourceAsStream(resourceName);
    }

    /**
     * Stable [23.05.2018]
     *
     * @param factory factory
     * @param path path
     * @return HttpResponse
     * @throws IOException IOException
     */
    public static HttpResponse buildGetRequest(HttpRequestFactory factory, String path) throws IOException {
        return factory.buildGetRequest(new GenericUrl(path)).execute();
    }

    /**
     * Stable [14.08.2018]
     *
     * @param factory factory
     * @param path path
     * @param type type
     * @param data data
     * @return HttpResponse
     * @throws IOException IOException
     */
    public static HttpResponse buildPostRequest(HttpRequestFactory factory, String path, String type, byte[] data) throws IOException {
        final HttpContent content = new ByteArrayContent(type, data);
        return factory.buildPostRequest(new GenericUrl(path), content).execute();
    }

    /**
     * Stable [23.05.2018]
     *
     * @param jsonObject jsonObject
     * @param field field
     * @return String value
     * @throws JSONException JSONException
     */
    public static String getStringFromJSONObject(JSONObject jsonObject, String field) throws JSONException {
        return jsonObject.has(field) ? jsonObject.getString(field) : null;
    }

    /**
     * Stable [23.05.2018]
     *
     * @param jsonObject jsonObject
     * @param field field
     * @return Object value
     * @throws JSONException JSONException
     */
    public static Object getObjectFromJSONObject(JSONObject jsonObject, String field) throws JSONException {
        return jsonObject.has(field) ? jsonObject.get(field) : null;
    }
}