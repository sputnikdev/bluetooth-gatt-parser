import groovy.json.JsonBuilder

class GattRegistryGenerator {

    public static generate(project) {

        generate("$project.basedir/src/main/resources/gatt/characteristic",
                "$project.build.directory/generated-sources/groovy/gatt/characteristic/gatt_spec_registry.json")

        generate("$project.basedir/src/main/resources/gatt/service",
                "$project.build.directory/generated-sources/groovy/gatt/service/gatt_spec_registry.json")

    }

    public static generate(String inputFolderName, String registryFileName) {

        final File registryFile = new File(registryFileName)
        registryFile.parentFile.mkdirs()
        registryFile.createNewFile()

        final File directory = new File(inputFolderName)
        final XmlParser parser = new XmlParser()
        parser.setFeature('http://apache.org/xml/features/disallow-doctype-decl', true)
        final def registry = new HashMap()
        directory.eachFileMatch(~/.*.xml/) { file ->
            def xml = parser.parse(file)
            def type = xml.attributes()['type'];
            if ("${type}.xml" != file.name) {
                throw new IllegalStateException(
                        "GATT registry generation failed. 'type' attribute ($type) does not match to its file name ($file.name)");
            }
            registry.put(xml.attributes()["uuid"], xml.attributes()["type"])
        }
        registryFile.write(new JsonBuilder(registry).toPrettyString())
    }

}