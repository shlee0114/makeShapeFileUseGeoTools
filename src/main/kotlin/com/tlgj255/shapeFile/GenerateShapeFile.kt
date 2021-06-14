package com.tlgj255.shapeFile
import org.geotools.data.DataUtilities
import org.geotools.geometry.jts.JTSFactoryFinder
import org.locationtech.jts.geom.Coordinate
import org.opengis.feature.simple.SimpleFeatureType
import java.io.File
//"LINE", "centerline:LineString,name:\"\",id:0"
class GenerateShapeFile(val type : String,val typeSpec : String){
    //path : 파일을 저장할 경로
    //coordinate : 추출할 좌표값들 Coordinate 생성 방법 -> Coordinate(x, y)
    fun generateShpFile(path : String, coordinate : Array<Coordinate>){
        generateLineString(path, coordinate)
    }

    //path : 파일을 저장할 경로
    //coordinate : 추출할 좌표값들 arraylist에도 입력 가능하도록 추가
    fun generateShpFile(path : String, coordinate : ArrayList<ArrayList<Float>>){
        generateLineString(path, coordinate.translateGeometry())
    }

    private fun ArrayList<ArrayList<Float>>.translateGeometry() : Array<Coordinate>{
        val coordinate = arrayOf<Coordinate>()
        this.forEach {
            coordinate[coordinate.size] = Coordinate(it[0].toDouble(), it[1].toDouble())
        }
        return coordinate
    }

    //path : 파일을 저장할 경로
    //coordinate : 추출할 좌표값들 Coordinate 생성 방법 -> Coordinate(x, y)
    private fun generateLineString(path : String, coordinate : Array<Coordinate>){
        val geometry = JTSFactoryFinder.getGeometryFactory()
        val lineString = geometry.createLineString(coordinate)
        val schema: SimpleFeatureType = DataUtilities.createType(type, typeSpec)
        val feature = DataUtilities.template(schema).apply {
            defaultGeometry = lineString
        }

        val collection = DataUtilities.collection(feature)

        WriteShapefile(File(path)).also {
            it.writeFeatures(collection)
        }
    }
}