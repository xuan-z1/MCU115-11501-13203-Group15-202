package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class StartFragment : Fragment(R.layout.frag_start) {
    private val vm: WorkViewModel by activityViewModels()

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val replyText = result.data?.getStringExtra(SecActivity.EXTRA_REPLY_TEXT)
            if (!replyText.isNullOrEmpty()) {
                view?.findViewById<TextView>(R.id.tvResult)?.apply {
                    text = replyText
                    alpha = 0f
                    animate().alpha(1f).setDuration(300).start()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnStart)?.setOnClickListener { vm.start() }
        view.findViewById<Button>(R.id.btnCancel)?.setOnClickListener { vm.cancel() }

        // HW5 Activity Page Switching controls
        val etInput = view.findViewById<EditText>(R.id.etInput)
        val btnSwitch = view.findViewById<Button>(R.id.btnSwitch)

        btnSwitch?.setOnClickListener {
            hideKeyboard()
            val textToSend = etInput?.text?.toString().orEmpty()
            val act = requireActivity()
            val intent = Intent(act, SecActivity::class.java).apply {
                putExtra(SecActivity.EXTRA_INPUT_TEXT, textToSend)
            }
            startForResult.launch(intent)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                act.overrideActivityTransition(
                    Activity.OVERRIDE_TRANSITION_OPEN,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            } else {
                @Suppress("DEPRECATION")
                act.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val focusView = activity?.currentFocus ?: view
        focusView?.let {
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}