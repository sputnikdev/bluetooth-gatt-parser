package org.sputnikdev.bluetooth.gattparser.spec;

/*-
 * #%L
 * org.sputnikdev:bluetooth-gatt-parser
 * %%
 * Copyright (C) 2017 Sputnik Dev
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Bluetooth GATT specification reader. Capable of reading Bluetooth SIG GATT specifications for
 * <a href="https://www.bluetooth.com/specifications/gatt">services and characteristics</a>.
 * Stateful but threadsafe.
 *
 * @author Vlad Kolotov
 */
public class BluetoothGattSpecificationReader {

    private static final String MANDATORY_FLAG = "Mandatory";
    private static final String OPTIONAL_FLAG = "Optional";
    private static final String SPEC_ROOT_FOLDER_NAME = "gatt";
    private static final String SPEC_SERVICES_FOLDER_NAME = "service";
    private static final String SPEC_CHARACTERISTICS_FOLDER_NAME = "characteristic";
    private static final String SPEC_REGISTRY_FILE_NAME = "gatt_spec_registry.json";
    private static final String CLASSPATH_SPEC_FULL_SERVICES_FOLDER_NAME = SPEC_ROOT_FOLDER_NAME + "/"
            + SPEC_SERVICES_FOLDER_NAME;
    private static final String CLASSPATH_SPEC_FULL_CHARACTERISTICS_FOLDER_NAME = SPEC_ROOT_FOLDER_NAME + "/"
            + SPEC_CHARACTERISTICS_FOLDER_NAME;
    private static final String CLASSPATH_SPEC_FULL_CHARACTERISTIC_FILE_NAME =
            SPEC_ROOT_FOLDER_NAME + "/" + SPEC_CHARACTERISTICS_FOLDER_NAME + "/" + SPEC_REGISTRY_FILE_NAME;
    private static final String CLASSPATH_SPEC_FULL_SERVICE_FILE_NAME =
            SPEC_ROOT_FOLDER_NAME + "/" + SPEC_CHARACTERISTICS_FOLDER_NAME + "/" + SPEC_REGISTRY_FILE_NAME;
    private final Logger logger = LoggerFactory.getLogger(BluetoothGattSpecificationReader.class);

    private static final FilenameFilter XML_FILE_FILTER = (dir, name) -> name.toLowerCase().endsWith(".xml");

    private final BiMap<String, String> servicesRegistry;
    private final BiMap<String, String> characteristicsRegistry;

    private final Map<String, Service> services = new HashMap<>();
    private final Map<String, Characteristic> characteristicsByUUID = new HashMap<>();
    private final Map<String, Characteristic> characteristicsByType = new HashMap<>();

    /**
     * Creates an instance of GATT specification reader and pre-cache GATT specification files from java classpath
     * by the following paths: gatt/characteristic and gatt/service.
     */
    public BluetoothGattSpecificationReader() {
        servicesRegistry = readServicesRegistryFromClassPath();
        characteristicsRegistry = readCharacteristicsRegistryFromClassPath();
    }

    /**
     * Returns GATT service specification by its UUID.
     *
     * @param uuid an UUID of a GATT service
     * @return GATT service specification
     */
    public Service getService(String uuid) {
        if (services.containsKey(uuid)) {
            return services.get(uuid);
        } else if (servicesRegistry.containsKey(uuid)) {
            synchronized (services) {
                // is it still not loaded?
                if (!services.containsKey(uuid)) {
                    Service service = loadService(uuid);
                    addService(service);
                    return service;
                }
            }
        }
        return null;
    }

    /**
     * Returns GATT characteristic specification by its UUID.
     *
     * @param uuid an UUID of a GATT characteristic
     * @return GATT characteristic specification
     */
    public Characteristic getCharacteristicByUUID(String uuid) {
        if (characteristicsByUUID.containsKey(uuid)) {
            return characteristicsByUUID.get(uuid);
        } else if (characteristicsRegistry.containsKey(uuid)) {
            synchronized (characteristicsByUUID) {
                // is it still not loaded?
                if (!characteristicsByUUID.containsKey(uuid)) {
                    Characteristic characteristic = loadCharacteristic(uuid);
                    addCharacteristic(characteristic);
                    return characteristic;
                }
            }
        }
        return null;
    }

    /**
     * Returns GATT characteristic specification by its type.
     *
     * @param type a type of a GATT characteristic
     * @return GATT characteristic specification
     */
    public Characteristic getCharacteristicByType(String type) {
        if (characteristicsByType.containsKey(type)) {
            return characteristicsByType.get(type);
        } else if (characteristicsRegistry.inverse().containsKey(type)) {
            synchronized (characteristicsByUUID) {
                // is it still not loaded?
                if (!characteristicsByType.containsKey(type)) {
                    Characteristic characteristic = loadCharacteristic(
                            characteristicsRegistry.inverse().get(type));
                    addCharacteristic(characteristic);
                    return characteristic;
                }
            }
        }
        return null;
    }

    /**
     * Returns all registered GATT characteristic specifications.
     *
     * @return all registered characteristic specifications
     */
    public Collection<Characteristic> getCharacteristics() {
        return new ArrayList<>(characteristicsByUUID.values());
    }

    /**
     * Returns all registered GATT service specifications.
     *
     * @return all registered GATT service specifications
     */
    public Collection<Service> getServices() {
        return new ArrayList<>(services.values());
    }

    /**
     * Returns a list of field specifications for a given characteristic.
     * Note that field references are taken into account. Referencing fields are not returned,
     * referenced fields returned instead (see {@link Field#getReference()}).
     *
     * @param characteristic a GATT characteristic specification object
     * @return a list of field specifications for a given characteristic
     */
    public List<Field> getFields(Characteristic characteristic) {
        List<Field> fields = new ArrayList<>();
        if (characteristic.getValue() == null) {
            return Collections.emptyList();
        }
        for (Field field : characteristic.getValue().getFields()) {
            if (field.getReference() == null) {
                fields.add(field);
            } else {
                //TODO prevent recursion loops
                fields.addAll(getFields(getCharacteristicByType(field.getReference().trim())));
            }
        }
        return Collections.unmodifiableList(fields);
    }

    /**
     * This method is used to load/register custom services and characteristics
     * (defined in GATT XML specification files,
     * see an example <a href="https://www.bluetooth.com/api/gatt/XmlFile?xmlFileName=org.bluetooth.characteristic.battery_level.xml">here</a>)
     * from a folder. The folder must contain two sub-folders for services and characteristics respectively:
     * "path"/service and "path"/characteristic. It is also possible to override existing services and characteristics
     * by matching UUIDs of services and characteristics in the loaded files.
     * @param path a root path to a folder containing definitions for custom services and characteristics
     */
    public void loadExtensionsFromFolder(String path) {
        logger.info("Reading services and characteristics from folder: " + path);
        String servicesFolderName = path + File.separator + SPEC_SERVICES_FOLDER_NAME;
        String characteristicsFolderName = path + File.separator + SPEC_CHARACTERISTICS_FOLDER_NAME;
        logger.info("Reading services from folder: " + servicesFolderName);
        readServices(getFilesFromFolder(servicesFolderName));
        logger.info("Reading characteristics from folder: " + characteristicsFolderName);
        readCharacteristics(getFilesFromFolder(characteristicsFolderName));
    }

    Set<String> getRequirements(List<Field> fields, Field flags) {
        Set<String> result = new HashSet<>();
        for (Iterator<Field> iterator = fields.iterator(); iterator.hasNext();) {
            Field field = iterator.next();
            if (field.getBitField() != null) {
                continue;
            }
            List<String> requirements = field.getRequirements();
            if (requirements == null || requirements.isEmpty()) {
                continue;
            }
            if (requirements.contains(MANDATORY_FLAG)) {
                continue;
            }
            if (requirements.size() == 1 && requirements.contains(OPTIONAL_FLAG) && !iterator.hasNext()) {
                continue;
            }
            result.addAll(requirements);
        }
        return result;
    }

    private BiMap<String, String> readCharacteristicsRegistryFromClassPath() {
        return Maps.unmodifiableBiMap(HashBiMap.create(
                readRegistryFromClassPath(CLASSPATH_SPEC_FULL_CHARACTERISTIC_FILE_NAME)));
    }

    private BiMap<String, String> readServicesRegistryFromClassPath() {
        return Maps.unmodifiableBiMap(HashBiMap.create(
                readRegistryFromClassPath(CLASSPATH_SPEC_FULL_SERVICE_FILE_NAME)));
    }

    private void addCharacteristic(Characteristic characteristic) {
        validate(characteristic);
        characteristicsByUUID.put(characteristic.getUuid(), characteristic);
        characteristicsByType.put(characteristic.getType().trim(), characteristic);
    }

    private void addService(Service service) {
        services.put(service.getUuid(), service);
    }

    private void validate(Characteristic characteristic) {
        List<Field> fields = characteristic.getValue().getFields();
        if (fields.isEmpty()) {
            logger.warn("Characteristic \"{}\" does not have any Fields tags, "
                    + "therefore reading this characteristic will not be possible.", characteristic.getName());
            return;
        }
        Field flags = FlagUtils.getFlags(fields);
        Set<String> readFlags = flags != null ? FlagUtils.getAllReadFlags(flags) : Collections.<String>emptySet();
        Set<String> writeFlags = flags != null ? FlagUtils.getAllWriteFlags(flags) : Collections.<String>emptySet();
        Set<String> requirements = getRequirements(fields, flags);

        Set<String> unfulfilledReadRequirements = new HashSet<>(requirements);
        unfulfilledReadRequirements.removeAll(readFlags);
        Set<String> unfulfilledWriteRequirements = new HashSet<>(requirements);
        unfulfilledWriteRequirements.removeAll(writeFlags);

        if (unfulfilledReadRequirements.isEmpty()) {
            characteristic.setValidForRead(true);
        }

        if (unfulfilledWriteRequirements.isEmpty()) {
            characteristic.setValidForWrite(true);
        }

        if (!unfulfilledReadRequirements.isEmpty() && !unfulfilledWriteRequirements.isEmpty()) {
            logger.warn("Characteristic \"{}\" is not valid neither for read nor for write operation "
                    + "due to unfulfilled requirements: read ({}) write ({}).",
                    characteristic.getName(), unfulfilledReadRequirements, unfulfilledWriteRequirements);
        }
    }

    private List<URL> getFilesFromFolder(String folder) {
        File folderFile = new File(folder);
        File[] files = folderFile.listFiles();
        if (!folderFile.exists() || !folderFile.isDirectory() || files == null || files.length == 0) {
            return Collections.emptyList();
        }
        List<URL> urls = new ArrayList<>();
        try {
            for (File file : files) {
                urls.add(file.toURI().toURL());
            }
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
        return urls;
    }

    private List<URL> getFilesFromClassPath(String rootFolder, URL fileList) {
        logger.debug("Getting spec list from file: " + fileList.getPath());
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            List<URL> files = new ArrayList<>();
            String content = new Scanner(fileList.openStream(), "UTF-8").useDelimiter("\\A").next();
            for (String fileName : content.split("\\r?\\n")) {
                URL file = classLoader.getResource(rootFolder + fileName.trim());
                if (file != null) {
                    files.add(file);
                }
            }
            logger.debug("Found specs: " + files.size());
            return files;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private List<URL> getAllFilesFromClassPath(String rootFolder) {
        logger.debug("Getting all specs from folder: {}", rootFolder);
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            List<URL> files = new ArrayList<>();
            for (File file : new File(classLoader.getResource(rootFolder).toURI()).listFiles(XML_FILE_FILTER)) {
                files.add(file.toURI().toURL());
            }
            logger.debug("Found specs: {}", files.size());
            return files;
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    private Service loadService(String uuid) {
        String fileName = servicesRegistry.get(uuid);
        URL url = getClass().getClassLoader().getResource(
                CLASSPATH_SPEC_FULL_SERVICES_FOLDER_NAME + "/" + fileName + ".xml");
        return getService(url);
    }

    private Characteristic loadCharacteristic(String uuid) {
        String fileName = characteristicsRegistry.get(uuid);
        URL url = getClass().getClassLoader().getResource(
                CLASSPATH_SPEC_FULL_CHARACTERISTICS_FOLDER_NAME + "/" + fileName + ".xml");
        return getCharacteristic(url);
    }

    private void readServices(List<URL> files) {
        for (URL file : files) {
            Service service = getService(file);
            if (service != null) {
                addService(service);
            }
        }
    }

    private void readCharacteristics(List<URL> files) {
        for (URL file : files) {
            Characteristic characteristic = getCharacteristic(file);
            if (characteristic != null) {
                addCharacteristic(characteristic);
            }
        }
    }

    private Service getService(URL file) {
        return getSpec(file);
    }

    private Characteristic getCharacteristic(URL file) {
        return getSpec(file);
    }

    private <T> T getSpec(URL file) {
        try {
            XStream xstream = new XStream(new DomDriver());
            xstream.autodetectAnnotations(true);
            xstream.processAnnotations(Bit.class);
            xstream.processAnnotations(BitField.class);
            xstream.processAnnotations(Characteristic.class);
            xstream.processAnnotations(Enumeration.class);
            xstream.processAnnotations(Enumerations.class);
            xstream.processAnnotations(Field.class);
            xstream.processAnnotations(InformativeText.class);
            xstream.processAnnotations(Service.class);
            xstream.processAnnotations(Value.class);
            xstream.processAnnotations(Reserved.class);
            xstream.processAnnotations(Examples.class);
            xstream.processAnnotations(CharacteristicAccess.class);
            xstream.processAnnotations(Characteristics.class);
            xstream.processAnnotations(Properties.class);
            xstream.ignoreUnknownElements();
            xstream.setClassLoader(Characteristic.class.getClassLoader());
            return (T) xstream.fromXML(file);
        } catch (Exception e) {
            logger.error("Could not read file: " + file, e);
        }
        return null;
    }

    private Map<String, String> readRegistryFromClassPath(String fileName) {
        logger.info("Reading GATT registry from: {}", fileName);

        URL serviceRegistry = getClass().getClassLoader().getResource(fileName);

        if (serviceRegistry == null) {
            throw new IllegalStateException("GATT spec registry file is missing");
        }

        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Gson gson = new Gson();

        JsonReader jsonReader = null;
        try {
            jsonReader = new JsonReader(new InputStreamReader(serviceRegistry.openStream(), "UTF-8"));
            return gson.fromJson(jsonReader, type);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (jsonReader != null) {
                try {
                    jsonReader.close();
                } catch (IOException e) {
                    logger.error("Could not close stream", e);
                }
            }
        }
    }

}
