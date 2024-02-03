package uz.xia.taxigo.utils.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import uz.xia.taxigo.R
import uz.xia.taxigo.databinding.SimpleSpinnerDropdownItemBinding
import uz.xia.taxigo.utils.dpToPx
import uz.xia.taxigo.utils.gone
import uz.xia.taxigo.utils.px
import uz.xia.taxigo.utils.show

open class SimpleSpinner : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    var itemSelectedListener: ItemSelectedListener? = null
    val textColor: Int by lazy { ContextCompat.getColor(context, R.color.colorTextBlack) }
    val selectedTextColor: Int = R.color.colorTextBlue
    val spinner = AppCompatSpinner(context, Spinner.MODE_DROPDOWN)
    val imageView = ImageView(context)
    val adapter: SimpleSpinnerAdapter
    var inflater: LayoutInflater = LayoutInflater.from(context)

    init {
        addView(spinner)
        addView(imageView)

        adapter = SimpleSpinnerAdapter(context)
        spinner.adapter = adapter

        spinner.setBackgroundColor(Color.TRANSPARENT)
        spinner.setPopupBackgroundResource(R.drawable.shape_for_spinner)
        spinner.post {
            spinner.dropDownWidth = width

        }

        spinner.layoutParams.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT

        }

        imageView.apply {
            setImageResource(R.drawable.ic_arrow_with_bg)
            (layoutParams as LayoutParams).apply {
                width = context.dpToPx(28)
                height = context.dpToPx(28)
                marginEnd = context.dpToPx(12)
                gravity = Gravity.CENTER_VERTICAL or Gravity.END
                rotation = 90f
            }
        }
       // this.setPadding(16.px, 8.px, 16.px, 8.px)
    }


    fun setSpinnerData(
        items: List<String>,
        isRequired: Boolean = true,
        defaultValue: String? = context.getString(R.string.select_one),
        placeHolder: String = context.getString(R.string.select_one)
    ) {
        val hasDefaultValue = defaultValue != null

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
                adapter.notifyDataSetChanged()
            }

            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (!(hasDefaultValue && isRequired)) {
                    itemSelectedListener?.onItemSelected(position - 1)
                } else itemSelectedListener?.onItemSelected(position)
                adapter.selectedPosition = position
            }
        }


        val itemsSpinner = arrayListOf<String>()
        if (!(hasDefaultValue && isRequired)) {
            itemsSpinner.add(placeHolder)
        }

        itemsSpinner.addAll(items)

        var defaultItemPosition = 0

        if (hasDefaultValue && itemsSpinner.contains(defaultValue)) defaultItemPosition =
            itemsSpinner.indexOfFirst { it == defaultValue }

        spinner.isClickable = itemsSpinner.size >= 1


        updateAdapterItems(
            itemsSpinner, isRequired, hasDefaultValue, placeHolder
        )

        if (itemsSpinner.size > 1) {
            spinner.isEnabled = true
            imageView.show()
        } else {
            spinner.isEnabled = false
            imageView.gone()
        }

        spinner.setSelection(defaultItemPosition)

    }

    fun setSelection(position: Int) {
        spinner.setSelection(position)
    }

    private fun updateAdapterItems(
        items: List<String>, isRequired: Boolean, hasDefaultValue: Boolean, placeHolder: String
    ) {
        adapter.clear()
        adapter.addAll(items)
        adapter.isRequired = isRequired
        adapter.hasDefaultValue = hasDefaultValue
        adapter.placeHolder = placeHolder
        adapter.notifyDataSetChanged()

    }

    inner class SimpleSpinnerAdapter(
        context: Context,
        var items: List<String> = arrayListOf(),
        var isRequired: Boolean = true,         // required value, doesn't have none value
        var hasDefaultValue: Boolean = true,    //def value = items[0]
        var placeHolder: String = context.getString(R.string.select_one)
    ) : ArrayAdapter<String>(context, 0, items) {
        var selectedPosition: Int = 0

        var isChangedPos = false

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            isChangedPos = false
            var item = convertView

            if (item == null) {
                val textView = TextView(context).apply {
                    setTextColor(textColor)
                    typeface = ResourcesCompat.getFont(context, R.font.circle_rounded_regular)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, 16f.px)
                }
                item = textView
            }

            (item as TextView).apply {
                text = items[position]
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }
            item.setPadding(
                context.dpToPx(16), context.dpToPx(16), context.dpToPx(32), context.dpToPx(16)
            )
            return item
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val itemBinding = SimpleSpinnerDropdownItemBinding.inflate(inflater, parent, false)
            itemBinding.tvDropDownItem.text = items[position]

            when (position) {
                0 -> itemBinding.root.setPadding(
                    context.dpToPx(16), context.dpToPx(16), context.dpToPx(16), context.dpToPx(8)
                )
                items.size - 1 -> itemBinding.root.setPadding(
                    context.dpToPx(16), context.dpToPx(8), context.dpToPx(16), context.dpToPx(16)
                )
                else -> itemBinding.root.setPadding(
                    context.dpToPx(16), context.dpToPx(8), context.dpToPx(16), context.dpToPx(8)
                )
            }

            if (isRequired && items[position] == placeHolder) {
                itemBinding.tvDropDownItem.setTextColor(
                    ResourcesCompat.getColor(
                        context.resources,
                        R.color.primary_icon,
                        null
                    )
                )
            } else {
                if (position == selectedPosition) itemBinding.tvDropDownItem.setTextColor(
                    ResourcesCompat.getColor(context.resources, R.color.colorTextBlue, null)
                )
                else itemBinding.tvDropDownItem.setTextColor(textColor)
            }

            parent.isVerticalScrollBarEnabled = false
            parent.isHorizontalScrollBarEnabled = false
            parent.isScrollbarFadingEnabled = false
            parent.overScrollMode = View.OVER_SCROLL_NEVER
            return itemBinding.root
        }

        override fun isEnabled(position: Int): Boolean {
            return !(isRequired && items[position] == placeHolder)
        }
    }

    interface ItemSelectedListener {
        fun onItemSelected(position: Int)
    }
}
