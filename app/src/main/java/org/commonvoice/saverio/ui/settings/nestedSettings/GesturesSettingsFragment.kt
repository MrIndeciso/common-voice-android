package org.commonvoice.saverio.ui.settings.nestedSettings

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_gestures_settings.*
import org.commonvoice.saverio.databinding.FragmentGesturesSettingsBinding
import org.commonvoice.saverio.ui.viewBinding.ViewBoundFragment

class GesturesSettingsFragment : ViewBoundFragment<FragmentGesturesSettingsBinding>() {

    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGesturesSettingsBinding {
        return FragmentGesturesSettingsBinding.inflate(layoutInflater, container, false)
    }

    override fun onStart() {
        super.onStart()

        buttonBackSettingsSubSectionGestures.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}