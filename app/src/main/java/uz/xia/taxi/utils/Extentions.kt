package uz.xia.taxi.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import org.osmdroid.util.GeoPoint
import java.lang.Math.*
import java.security.MessageDigest
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.ln

fun AppCompatActivity.makeStatusBarTransparent(isDarkMode: Boolean) {
    window.statusBarColor = Color.TRANSPARENT
    if (isDarkMode) window.decorView.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    else window.decorView.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
}

fun AppCompatActivity.setStatusBarColor(color: Int, navColor: Int, isDarkMode: Boolean) {
    when (color) {
        Color.TRANSPARENT -> this.makeStatusBarTransparent(isDarkMode)
        else -> {
            window.statusBarColor = ResourcesCompat.getColor(this.resources, color, null)
            window.navigationBarColor = ResourcesCompat.getColor(this.resources, navColor, null)
            if (isDarkMode) window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                else
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            )
        }
    }
}

fun <T> lazyFast(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)
inline val Float.px get() = this * Resources.getSystem().displayMetrics.density
inline val Int.px get() = this * Resources.getSystem().displayMetrics.density.toInt()
fun Context.dpToPx(dp: Int): Int = (dp * this.resources.displayMetrics.density).toInt()
fun Context.color(colorRes: Int) = ContextCompat.getColor(this, colorRes)

fun lerp(a: Float, b: Float, t: Float): Float =
    a + (b - a) * t

fun View.show() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun Context.getBitmapFromVector(drawableId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null

    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    ) ?: return null
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}

fun Context.inflateLayout(
    @LayoutRes layoutId: Int, parent: ViewGroup? = null,
    attachToRoot: Boolean = false
): View = LayoutInflater.from(this).inflate(layoutId, parent, attachToRoot)

fun String.sha512(): String {
    val bytes = this.toByteArray()
    val md = MessageDigest.getInstance("SHA-512")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}

fun String.sha256(): String {
    val bytes = this.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digested = md.digest(toByteArray())
    return digested.joinToString("") {
        String.format("%02x", it)
    }
}
fun Context.isCheckStoragePermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this, Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this, Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}
fun Context.isCheckLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.locationEnabled() : Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

 /*  Ikki nuqta orasidagi markazni topish */
fun getCenterBetweenPoints(point1: GeoPoint, point2: GeoPoint): GeoPoint {
    val latCenter = (point1.latitude + point2.latitude) / 2
    val lngCenter = (point1.longitude + point2.longitude) / 2
    return GeoPoint(latCenter, lngCenter)
}

 /* Ikki nuqta orasidagi zoom levelni topish */
fun getZoomLevel(point1: GeoPoint, point2: GeoPoint): Double {
    val WORLD_DIMENSION = 256
    val ZOOM_MAX = 21
    val latDiff = abs(point1.latitude - point2.latitude)
    val lngDiff = abs(point1.longitude - point2.longitude)
    val latZoom = floor(ln(WORLD_DIMENSION / latDiff) / ln(2.0))
    val lngZoom = floor(ln(WORLD_DIMENSION / lngDiff) / ln(2.0))
    val zoom = latZoom.coerceAtMost(lngZoom)
    return min(max(zoom, 0.0), ZOOM_MAX.toDouble()) +3
}
fun String?.latinToKrill(): String? {
    val text = this ?: return null
    val a = hashMapOf(
         "A" to "А",
         "B" to "Б",
         "V" to "В",
         "G" to "Г",
         "D" to "Д",
         "E" to "Е",
         "YO" to "Ё",
         "J" to "Ж",
         "Z" to "З",
         "I" to "И",
         "Y" to "Й",
         "K" to "К",
         "L" to "Л",
         "M" to "М",
         "N" to "Н",
         "O" to "О",
         "P" to "П",
         "R" to "Р",
         "S" to "С",
         "T" to "Т",
         "U" to "У",
         "F" to "Ф",
         "X" to "Х",
         "TS" to "Ц",
         "CH" to "Ч",
         "SH" to "Ш",
         "SH" to "Щ",
         "E" to "Э",
         "YU" to "Ю",
         "YA" to "Я",
         "Q" to "Қ",
         "O'" to "Ў",
         "H" to "Ҳ",
         "G'" to "Ғ",
         "g'" to "ғ",
         "h" to "ҳ",
         "a" to "а",
         "b" to "б",
         "v" to "в",
         "g" to "г",
         "d" to "д",
         "e" to "е",
         "yo" to "ё",
         "j" to "ж",
         "z" to "з",
         "i" to "и",
         "y" to "й",
         "k" to "к",
         "l" to "л",
         "m" to "м",
         "n" to "н",
         "o" to "о",
         "p" to "п",
         "r" to "р",
         "s" to "с",
         "t" to "т",
         "u" to "у",
         "f" to "ф",
         "x" to "х",
         "ts" to "ц",
         "ch" to "ч",
         "sh" to "ш",
         "sh" to "щ",
         "iy" to "ы",
         "e" to "э",
         "yu" to "ю",
         "ya" to "я",
         "q" to "қ",
         "ʹ" to "ь",)
    var temp = ""
    text.forEach {
        val s = a[it.toString()]
        if (s != null) temp += s
        else temp += it
    }
    return temp
}

/*
TableInfo{name='region_data', columns={id=Column{name='id', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=1, defaultValue='null'}, name_uz_kr=Column{name='name_uz_kr', type='TEXT', affinity='2', notNull=true, primaryKeyPosition=0, defaultValue='null'}, name_ru=Column{name='name_ru', type='TEXT', affinity='2', notNull=true, primaryKeyPosition=0, defaultValue='null'}, name_uz_lt=Column{name='name_uz_lt', type='TEXT', affinity='2', notNull=true, primaryKeyPosition=0, defaultValue='null'}, name_en=Column{name='name_en', type='TEXT', affinity='2', notNull=true, primaryKeyPosition=0, defaultValue='null'}}, foreignKeys=[], indices=[]}
                                                                                                     Found:
TableInfo{name='region_data', columns={id=Column{name='id', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=1, defaultValue='null'},name_uz_kr=Column{name='name_uz_kr', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='null'}, name_ru=Column{name='name_ru', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='null'}, name_uz_lt=Column{name='name_uz_lt', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='null'}, name_en=Column{name='name_en', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='null'}}, foreignKeys=[], indices=[]}
                                                                                                                                                                                                      */
/*
val format = SimpleDateFormat("HH", Locale.getDefault())
        val hour: String = format.format()
//night mode after 7 PM and before 7 AM

//night mode after 7 PM and before 7 AM
//taking cue from https://medium.com/square-corner-blog/welcome-to-the-color-matrix-64d112e3f43d
val inverseMatrix = ColorMatrix(
    floatArrayOf(
        -1.0f, 0.0f, 0.0f, 0.0f, 255f,
        0.0f, -1.0f, 0.0f, 0.0f, 255f,
        0.0f, 0.0f, -1.0f, 0.0f, 255f,
        0.0f, 0.0f, 0.0f, 1.0f, 0.0f
    )
)
val destinationColor = Color.parseColor("#FF2A2A2A")
val lr = (255.0f - Color.red(destinationColor)) / 255.0f
val lg = (255.0f - Color.green(destinationColor)) / 255.0f
val lb = (255.0f - Color.blue(destinationColor)) / 255.0f
val grayscaleMatrix = ColorMatrix(
    floatArrayOf(
        lr, lg, lb, 0f, 0f,  //
        lr, lg, lb, 0f, 0f,  //
        lr, lg, lb, 0f, 0f,  //
        0f, 0f, 0f, 0f, 255f
    )
)
grayscaleMatrix.preConcat(inverseMatrix)
val dr = Color.red(destinationColor)
val dg = Color.green(destinationColor)
val db = Color.blue(destinationColor)
val drf = dr / 255f
val dgf = dg / 255f
val dbf = db / 255f
val tintMatrix = ColorMatrix(
    floatArrayOf(
        drf, 0f, 0f, 0f, 0f,  //
        0f, dgf, 0f, 0f, 0f,  //
        0f, 0f, dbf, 0f, 0f,  //
        0f, 0f, 0f, 1f, 0f
    )
)
tintMatrix.preConcat(grayscaleMatrix)
val lDestination = drf * lr + dgf * lg + dbf * lb
val scale = 1f - lDestination
val translate = 1 - scale * 0.5f
val scaleMatrix = ColorMatrix(
    floatArrayOf(
        scale, 0f, 0f, 0f, dr * translate,  //
        0f, scale, 0f, 0f, dg * translate,  //
        0f, 0f, scale, 0f, db * translate,  //
        0f, 0f, 0f, 1f, 0f
    )
)
scaleMatrix.preConcat(tintMatrix)
val filter = ColorMatrixColorFilter(scaleMatrix)
map?.overlayManager?.tilesOverlay?.setColorFilter(filter)*/

// Route
/*class RouteTask(
        private val startPoint: GeoPoint, private val endPoint: GeoPoint, context: Context
    ) : AsyncTask<Void, Void, String>() {
        private val roadManager: RoadManager = OSRMRoadManager(context, "")
        override fun doInBackground(vararg p0: Void?): String {
            val waypoints = ArrayList<GeoPoint>()
            waypoints.add(startPoint)
            waypoints.add(endPoint)
            try {
                val road = roadManager.getRoad(waypoints)
                //Log.d(TAG,"roads : ${road.mRouteHigh}")
                val roadOverlay: Polyline = buildRoadOverlay(road, Color.BLUE, 8f)
                Log.d(TAG, "overlys ${roadOverlay.points}")
                map!!.overlays.add(roadOverlay)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

        override fun onPostExecute(result: String?) {
            Timber.d("$TAG task finished")
            map?.invalidate()
            super.onPostExecute(result)
        }
}*/
// todo dark mode code map?.getOverlayManager()?.getTilesOverlay()?.setColorFilter(TilesOverlay.INVERT_COLORS)

/*  private val mReceive: MapEventsReceiver = object : MapEventsReceiver {
      override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {

          return false
      }

      override fun longPressHelper(p: GeoPoint): Boolean {
          return false
      }
  }*/
