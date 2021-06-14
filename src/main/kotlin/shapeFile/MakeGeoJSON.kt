package shapeFile

import java.io.File

class MakeGeoJSON {
    //type : 폴리곤의 타입 예) Point, LineString, Polygon
    //geometry : 좌표값 [[x, y],[x,y]]형식의 배열
    //path : geoJson파일이 저장될 파일 경로 및 파일 이름
    fun makeGeoJSON(type : String, geometry : ArrayList<ArrayList<Float>>, path : String) : String{
        generateGeoJSON(type, geometry, path)
        return path
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

    //type : 폴리곤의 타입 예) Point, LineString, Polygon
    //geometry : 좌표값 [[x, y],[x,y]]형식의 배열
    //path : geoJson파일이 저장될 파일 경로 및 파일 이름
    private fun generateGeoJSON(type : String, geometry: ArrayList<ArrayList<Float>>, path : String){
        var coordinate = ""
        for(i in geometry){
            coordinate += "[${i[0]}, ${i[1]}], "
        }
        coordinate = coordinate.removeRange(coordinate.lastIndex - 1, coordinate.lastIndex)

        val geoJson =  "{\n" +
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

        File(path).apply {
            writeText(geoJson)
            createNewFile()
        }
    }
}