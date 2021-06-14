
import org.geotools.feature.FeatureCollection
import org.geotools.geojson.feature.FeatureJSON
import org.geotools.geojson.geom.GeometryJSON
import org.opengis.feature.simple.SimpleFeature
import org.opengis.feature.simple.SimpleFeatureType
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
class GenerateShapeFile{
    //path : 파일을 저장할 경로
    //geoJson : geoJson파일이 저장되어있는 경로 및 파일 명
    fun generateShpFile(path : String, geoJson : String){
        val shpFile  = File(path)

        //val geoJson = test.makeGeoJSON("LineString", geom)
        val inpS : InputStream = FileInputStream(File(geoJson))
        val featureJSON = FeatureJSON(GeometryJSON(15))
        val fc = featureJSON.readFeatureCollection(inpS)

        val writer = WriteShapefile(shpFile)
        writer.writeFeatures(fc as FeatureCollection<SimpleFeatureType, SimpleFeature>)
    }
}

