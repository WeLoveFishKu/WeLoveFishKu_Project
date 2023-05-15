package id.fishku.fishersellercore.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import androidx.core.graphics.drawable.toDrawable
import id.fishku.fishersellercore.R

class LottieLoading {
    private var dialog: Dialog? = null

    fun start(context: Context){
        dialog = setLoadDataDialog(context)
    }
    fun stop(){
        if (dialog?.isShowing == true) dialog?.cancel()
    }

    private fun setLoadDataDialog(context: Context): Dialog? {

        dialog = Dialog(context)

        dialog.let {
            it?.show()
            it?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            it?.setContentView(R.layout.lottie_load_data)
            it?.setCancelable(false)
            it?.setCanceledOnTouchOutside(false)
            return it
        }
    }
}