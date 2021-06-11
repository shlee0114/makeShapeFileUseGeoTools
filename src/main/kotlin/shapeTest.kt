
import org.geotools.feature.FeatureCollection
import org.geotools.geojson.feature.FeatureJSON
import org.geotools.geojson.geom.GeometryJSON
import org.opengis.feature.simple.SimpleFeature
import org.opengis.feature.simple.SimpleFeatureType
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

fun main() {
    val path = "C:\\Users\\tlgj2\\Downloads\\Test_shapefile_ID-shp\\Test_shapefile_ID.shp"
    val shpFile  = File(path)

    val test = MakeGeoJSON()
    val geom = arrayListOf<ArrayList<Float>>().apply {
        add(arrayListOf(127.065873f,37.5018f))
        add(arrayListOf(127.123873f,37.218f))
        add(arrayListOf(127.365873f,37.38f))
        add(arrayListOf(127.6873f,37.118f))
    }
    val geoJson = test.makeGeoJSON("LineString", geom)
    val inpS : InputStream = FileInputStream(File(geoJson))
    val featureJSON = FeatureJSON(GeometryJSON(15))
    val fc = featureJSON.readFeatureCollection(inpS)

    val writer = WriteShapefile(shpFile)
    writer.writeFeatures(fc as FeatureCollection<SimpleFeatureType, SimpleFeature>)
}
