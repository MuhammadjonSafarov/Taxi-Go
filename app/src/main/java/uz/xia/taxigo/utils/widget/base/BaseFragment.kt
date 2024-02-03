package uz.xia.taxigo.utils.widget.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import timber.log.Timber

abstract class BaseFragment(
    @LayoutRes private val layoutContentId: Int
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(layoutContentId, container, false)
        v.fitsSystemWindows = true

        Timber.d(this.javaClass.simpleName)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize(view)
        setup()
        observe()
        clicks()
    }

    fun toast(msg:String){
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }
    open fun initialize(view: View) = Unit
    open fun setup() = Unit
    open fun observe() = Unit
    open fun clicks() = Unit
}
