package uz.xia.taxi.ui.participants.chat.photo

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import uz.xia.taxi.ui.participants.chat.photo.adapter.FilesPagerAdapter
import uz.xia.taxi.ui.participants.chat.photo.photo.adapter.PhotoSelectListener
import uz.xia.taxi.R
import uz.xia.taxi.databinding.LayoutAnnouncementAddPhotoBinding

private const val COLLAPSED_HEIGHT = 228

class AddPhotoFragment : BottomSheetDialogFragment(), PhotoSelectListener {

    // Можно обойтись без биндинга и использовать findViewById
    lateinit var binding: LayoutAnnouncementAddPhotoBinding

    // Переопределим тему, чтобы использовать нашу с закруглёнными углами
    override fun getTheme() = R.style.CustomBottomSheetDialogTheme

    private val photoSelectList = mutableListOf<String>()
    private lateinit var nextButton: AppCompatTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = LayoutAnnouncementAddPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Плотность понадобится нам в дальнейшем
        val density = requireContext().resources.displayMetrics.density
        dialog?.let {
            // Находим сам bottomSheet и достаём из него Behaviour
            val bottomSheet =
                it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)

            // Выставляем высоту для состояния collapsed и выставляем состояние collapsed
            behavior.peekHeight = (COLLAPSED_HEIGHT * density).toInt()
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true

            // Достаём корневые лэйауты
            val coordinator =
                (it as BottomSheetDialog).findViewById<CoordinatorLayout>(com.google.android.material.R.id.coordinator)
            val containerLayout =
                it.findViewById<FrameLayout>(com.google.android.material.R.id.container)

            // Надуваем наш лэйаут с кнопкой
            val buttons = it.layoutInflater.inflate(R.layout.button, null)
            // Выставояем параметры для нашей кнопки
            buttons.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                height = (64 * density).toInt()
                gravity = Gravity.BOTTOM
            }
            // Добавляем кнопку в контейнер
            containerLayout?.addView(buttons)

            // Перерисовываем лэйаут
            buttons.post {
                (coordinator?.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    buttons.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    // Устраняем разрыв между кнопкой и скролящейся частью
                    this.bottomMargin = (buttons.measuredHeight - 8 * density).toInt()
                    containerLayout?.requestLayout()
                }
            }
            val btnCancel = buttons.findViewById<AppCompatTextView>(R.id.button_cancel)
            nextButton = buttons.findViewById(R.id.button_next)
            btnCancel.setOnClickListener {
                dismiss()
            }
            binding.viewPager.adapter = FilesPagerAdapter(this, childFragmentManager, lifecycle)
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Rasmlar"
                    1 -> "Videolar"
                    else -> "Avdiolar"
                }
            }.attach()
        }

    }

    override fun onItemSelect(imagePath: String, isChecked: Boolean) {
        if (isChecked) photoSelectList.add(imagePath)
        else photoSelectList.remove(imagePath)
        nextButton.isEnabled = photoSelectList.isNotEmpty()
    }
}
/*
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Нам не нужны действия по этому колбеку
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                *//* with(binding) {
                         // Нас интересует только положительный оффсет, тк при отрицательном нас устроит стандартное поведение - скрытие фрагмента
                         if (slideOffset > 0) {
                             // Делаем "свёрнутый" layout более прозрачным
                             layoutCollapsed.alpha = 1 - 2 * slideOffset
                             // И в то же время делаем "расширенный layout" менее прозрачным
                             layoutExpanded.alpha = slideOffset * slideOffset

                             // Когда оффсет превышает половину, мы скрываем collapsed layout и делаем видимым expanded
                             if (slideOffset > 0.5) {
                                 layoutCollapsed.visibility = View.GONE
                                 layoutExpanded.visibility = View.VISIBLE
                             }

                             // Если же оффсет меньше половины, а expanded layout всё ещё виден, то нужно скрывать его и показывать collapsed
                             if (slideOffset < 0.5 && binding.layoutExpanded.visibility == View.VISIBLE) {
                                 layoutCollapsed.visibility = View.VISIBLE
                                 layoutExpanded.visibility = View.INVISIBLE
                             }
                         }
                     }*//*
                }
            })
        }*/
