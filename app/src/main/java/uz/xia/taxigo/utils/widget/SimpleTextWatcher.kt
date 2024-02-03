package uz.xia.taxigo.utils.widget

import android.text.Editable
import android.text.TextWatcher

class SimpleTextWatcher(private val listener:IWatcher):TextWatcher {

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (!text.isNullOrEmpty()){
            listener.onTextChange(text.toString())
        }
    }

    override fun afterTextChanged(p0: Editable?) {

    }
}