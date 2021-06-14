package com.tlgj255.shapeFile

import org.geotools.data.DataUtilities
import org.geotools.data.DefaultTransaction
import org.geotools.data.Transaction
import org.geotools.data.collection.ListFeatureCollection
import org.geotools.data.shapefile.ShapefileDataStore
import org.geotools.data.shapefile.ShapefileDataStoreFactory
import org.geotools.data.simple.SimpleFeatureCollection
import org.geotools.data.simple.SimpleFeatureSource
import org.geotools.data.simple.SimpleFeatureStore
import org.geotools.feature.FeatureCollection
import org.geotools.feature.NameImpl
import org.geotools.feature.simple.SimpleFeatureTypeImpl
import org.geotools.feature.type.GeometryDescriptorImpl
import org.geotools.feature.type.GeometryTypeImpl
import org.opengis.feature.simple.SimpleFeature
import org.opengis.feature.simple.SimpleFeatureType
import org.opengis.feature.type.AttributeDescriptor
import org.opengis.feature.type.GeometryDescriptor
import org.opengis.feature.type.GeometryType
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.net.MalformedURLException

class WriteShapefile(outfile: File) {
    private var shpDataStore: ShapefileDataStore? = null

    init {
        val dataStoreFactory = ShapefileDataStoreFactory()
        val params: MutableMap<String, Serializable?> = HashMap()
        try {
            params["url"] = outfile.toURI().toURL()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        params["create spatial index"] = true
        try {
            shpDataStore = dataStoreFactory.createNewDataStore(params) as ShapefileDataStore
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun writeFeatures(features: FeatureCollection<SimpleFeatureType, SimpleFeature>): Boolean {
        checkNotNull(shpDataStore) { "Datastore can not be null when writing" }
        val schema = features.schema
        val geom = schema.geometryDescriptor
        var oldGeomAttrib = ""
        try {

            /*
			 * Write the features to the shapefile
			 */
            val transaction: Transaction = DefaultTransaction("create")
            val typeName = shpDataStore!!.typeNames[0]
            val featureSource: SimpleFeatureSource = shpDataStore!!.getFeatureSource(typeName)

            /*
			 * The Shapefile format has a couple limitations: - "the_geom" is always
			 * first, and used for the geometry attribute name - "the_geom" must be of
			 * type Point, MultiPoint, MuiltiLineString, MultiPolygon - Attribute
			 * names are limited in length - Not all data types are supported (example
			 * Timestamp represented as Date)
			 *
			 * Because of this we have to rename the geometry element and then rebuild
			 * the features to make sure that it is the first attribute.
			 */
            val attributes = schema.attributeDescriptors
            var geomType: GeometryType? = null
            val attribs: MutableList<AttributeDescriptor> = ArrayList()
            for (attrib in attributes) {
                val type = attrib.type
                if (type is GeometryType) {
                    geomType = type
                    oldGeomAttrib = attrib.localName
                } else {
                    attribs.add(attrib)
                }
            }
            val gt = GeometryTypeImpl(
                NameImpl("the_geom"), geomType!!.binding,
                geomType.coordinateReferenceSystem, geomType.isIdentified, geomType.isAbstract,
                geomType.restrictions, geomType.getSuper(), geomType.description
            )
            val geomDesc: GeometryDescriptor = GeometryDescriptorImpl(
                gt, NameImpl("the_geom"), geom.minOccurs,
                geom.maxOccurs, geom.isNillable, geom.defaultValue
            )
            attribs.add(0, geomDesc)
            val shpType: SimpleFeatureType = SimpleFeatureTypeImpl(
                schema.name, attribs, geomDesc, schema.isAbstract,
                schema.restrictions, schema.getSuper(), schema.description
            )
            shpDataStore!!.createSchema(shpType)
            return if (featureSource is SimpleFeatureStore) {
                val featureStore = featureSource
                val feats: MutableList<SimpleFeature> = ArrayList()
                val features2 = features.features()
                while (features2.hasNext()) {
                    val f = features2.next()
                    val reType = DataUtilities.reType(shpType, f, true)
                    //set the default Geom (the_geom) from the original Geom
                    reType.setAttribute("the_geom", f.getAttribute(oldGeomAttrib))
                    feats.add(reType)
                }
                features2.close()
                val collection: SimpleFeatureCollection = ListFeatureCollection(shpType, feats)
                featureStore.transaction = transaction
                try {
                    featureStore.addFeatures(collection)
                    transaction.commit()
                } catch (problem: Exception) {
                    problem.printStackTrace()
                    transaction.rollback()
                } finally {
                    transaction.close()
                }
                shpDataStore!!.dispose()
                true
            } else {
                shpDataStore!!.dispose()
                System.err.println("ShapefileStore not writable")
                false
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }
}