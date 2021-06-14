import org.geotools.data.DataUtilities
import org.geotools.geometry.jts.JTSFactoryFinder
import org.locationtech.jts.geom.Coordinate
import org.opengis.feature.simple.SimpleFeatureType
import java.io.File

class GenerateShapeFile{
    //path : 파일을 저장할 경로
    //coordinate : 추출할 좌표값들 Coordinate 생성 방법 -> Coordinate(x, y)
    fun generateShpFile(path : String, coordinate : Array<Coordinate>){

        val geometry = JTSFactoryFinder.getGeometryFactory()
        val lineString = geometry.createLineString(coordinate)
        val schema: SimpleFeatureType = DataUtilities.createType("LINE", "centerline:LineString,name:\"\",id:0")

        val feature = DataUtilities.template(schema).apply {
            defaultGeometry = lineString
        }

        val collection = DataUtilities.collection(feature)

        WriteShapefile(File(path)).also {
            it.writeFeatures(collection)
        }
    }
}