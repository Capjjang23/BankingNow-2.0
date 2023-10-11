package com.example.bankingnow.ui

import com.example.bankingnow.R
import com.example.bankingnow.base.BaseDialogFragment
import com.example.bankingnow.databinding.DialogDrawBinding
import com.example.bankingnow.util.DrawingView

class DrawDialog: BaseDialogFragment<DialogDrawBinding>(R.layout.dialog_draw) {
    private lateinit var view_draw: DrawingView

    override fun initStartView() {
        super.initStartView()

        view_draw = binding.viewDraw
    }
}