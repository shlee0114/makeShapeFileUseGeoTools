import kotlinx.serialization.json.Json
import java.io.File

class MakeGeoJSON {
    fun makeGeoJSON(type : String, geometry : ArrayList<ArrayList<Float>>) : String{
        return generateGeoJSON(type, geometry)
    }
    fun makeMultiGeoJSON(type : String, geometry : ArrayList<ArrayList<ArrayList<Float>>>){
        when(type){
            "MultiPoint" -> {

            }
            "MultiLintString" -> {

            }
            "MultiPolygon" -> {

            }
        }
    }

    private fun generateGeoJSON(type : String, geometry: ArrayList<ArrayList<Float>>) : String{
        var coordinate = ""
        for(i in geometry){
            coordinate += "[${i[0]}, ${i[1]}], "
        }
        coordinate = coordinate.removeRange(coordinate.lastIndex - 1, coordinate.lastIndex)

        val test =  "{\n" +
                "  \"type\": \"FeatureCollection\",\n" +
                "  \"features\": [\n" +
                "    {\n" +
                "      \"type\": \"Feature\",\n" +
                "      \"properties\": {},\n" +
                "      \"geometry\": {\n" +
                "        \"type\": \"${type}\",\n" +
                "        \"coordinates\": [${coordinate}]\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}"

        val path = "C:\\test\\test.json"
        val testFile = File(path)
        testFile.writeText(test)
        testFile.createNewFile()
        return path
    }
}