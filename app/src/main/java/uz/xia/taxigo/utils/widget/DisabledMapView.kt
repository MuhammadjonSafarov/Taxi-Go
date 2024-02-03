package uz.xia.taxigo.utils.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import org.osmdroid.views.MapView

class DisabledMapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MapView(context, attrs) {

    private var isUserInteractionEnabled = true

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (isUserInteractionEnabled.not()) {
            return false
        }
        return super.dispatchTouchEvent(event)
    }

    fun setUserInteractionEnabled(isUserInteractionEnabled: Boolean) {
        this.isUserInteractionEnabled = isUserInteractionEnabled
    }
}