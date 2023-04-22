package org.mobileapp.localdata

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.mobileapp.tracking.track.Track
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object TrackLocalData {
    fun readTrack(context: Context, fileUri: Uri): Track {
        val json: String = LocalData.readTextFile(context, fileUri)
        var track: Track = Track()

        if (json.isNotEmpty()) {
            try {
                track = getCustomGson().fromJson(json, Track::class.java)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
        return track
    }

    suspend fun saveTempTrackSuspended(context: Context, track: Track) {
        return suspendCoroutine { cont ->
            cont.resume(saveTempTrack(context, track))
        }
    }

    private fun saveTrack(track: Track, saveGpxToo: Boolean) {
        val jsonString: String = trackToJsonString(track)

        if (jsonString.isNotBlank()) {
            // write track file
            LocalData.writeTextFile(jsonString, track.trackUriString.toUri())
        }

        if (saveGpxToo) {
            val gpxString: String = createGpxString(track)
            if (gpxString.isNotBlank()) {
                // write GPX file
                LocalData.writeTextFile(gpxString, track.gpxUriString.toUri())
            }
        }
    }

    private fun saveTempTrack(context: Context, track: Track) {
        val jsonString: String = trackToJsonString(track)

        if (jsonString.isNotBlank()) {
            LocalData.writeTextFile(jsonString, LocalData.getTempFileUri(context))
        }
    }

    private fun trackToJsonString(track: Track): String {
        val gson: Gson = getCustomGson()
        var json: String = String()

        try {
            json = gson.toJson(track)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        return json
    }

    private fun createGpxString(track: Track): String {
        var gpxString: String = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
                "<gpx version=\"1.1\" creator=\"Trackbook App (Android)\"\n" +
                "     xmlns=\"http://www.topografix.com/GPX/1/1\"\n" +
                "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "     xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n"

        gpxString += createGpxName(track)
        gpxString += createGpxTrk(track)

        gpxString += "</gpx>\n"

        return gpxString
    }

    private fun createGpxName(track: Track): String {
        val gpxName = StringBuilder("")
        gpxName.append("\t<metadata>\n")
        gpxName.append("\t\t<name>")
        gpxName.append("Trackbook Recording: ${track.name}")
        gpxName.append("</name>\n")
        gpxName.append("\t</metadata>\n")
        return gpxName.toString()
    }

    private fun createGpxTrk(track: Track): String {
        val gpxTrack = StringBuilder("")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        gpxTrack.append("\t<trk>\n")

        gpxTrack.append("\t\t<name>")
        gpxTrack.append("Track")
        gpxTrack.append("</name>\n")

        gpxTrack.append("\t\t<trkseg>\n")

        track.trackNodes.forEach { wayPoint ->
            gpxTrack.append("\t\t\t<trkpt lat=\"")
            gpxTrack.append(wayPoint.latitude)
            gpxTrack.append("\" lon=\"")
            gpxTrack.append(wayPoint.longitude)
            gpxTrack.append("\">\n")

            gpxTrack.append("\t\t\t\t<ele>")
            gpxTrack.append(wayPoint.altitude)
            gpxTrack.append("</ele>\n")

            gpxTrack.append("\t\t\t\t<time>")
            gpxTrack.append(dateFormat.format(Date(wayPoint.time)))
            gpxTrack.append("</time>\n")

            gpxTrack.append("\t\t\t</trkpt>\n")
        }

        gpxTrack.append("\t\t</trkseg>\n")

        gpxTrack.append("\t</trk>\n")

        return gpxTrack.toString()
    }

    private fun getCustomGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setDateFormat("M/d/yy hh:mm a")
        gsonBuilder.excludeFieldsWithoutExposeAnnotation()
        return gsonBuilder.create()
    }
}