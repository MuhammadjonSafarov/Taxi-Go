package uz.xia.taxigo.ui.participants.chat.tool.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ThreeGridItemDecorator(
    private val spaceEdge: Int,
    private val space: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        when {
            position % 3 == 0 -> {
                outRect.left = spaceEdge
                outRect.top = space / 2
                outRect.right = space / 2
                outRect.bottom = space / 2
            }
            position % 3 == 1 -> {
                outRect.left = space / 2
                outRect.top = space / 2
                outRect.right = space / 2
                outRect.bottom = space / 2
            }
            else -> {
                outRect.left = space / 2
                outRect.top = space / 2
                outRect.right = spaceEdge
                outRect.bottom = space / 2
            }
        }
    }
}
