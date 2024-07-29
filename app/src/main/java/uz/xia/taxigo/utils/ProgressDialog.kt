package uz.xia.taxigo.utils

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import uz.xia.taxigo.R

class ProgressDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.layout_progressbar, null, false)
        val alertDialogLoader =
            AlertDialog.Builder(requireContext(), R.style.FullScreenDialogTransparent)
                .setView(view)
                .setCancelable(false)
                .create()
        super.setCancelable(false)
        return alertDialogLoader
    }

    override fun getTheme() = R.style.FullScreenDialogTransparent
}
