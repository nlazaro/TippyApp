package com.nlazaro.tippyapp

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15

class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var seekBarSpilt: SeekBar
    private lateinit var tvSpiltLabel: TextView
    private lateinit var tvPersonSpilt: TextView
    private lateinit var tvTotalSpilt: TextView
    private lateinit var tvSpiltDescription: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercentLabel = findViewById(R.id.tvTipPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        seekBarSpilt = findViewById(R.id.seekBarSpilt)
        tvSpiltLabel = findViewById(R.id.tvSpiltLabel)
        tvPersonSpilt = findViewById(R.id.tvPersonLabel)
        tvTotalSpilt = findViewById(R.id.tvTotalSpilt)
        tvSpiltDescription = findViewById(R.id.tvSpiltDescription)

        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription((INITIAL_TIP_PERCENT))

        seekBarTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "OnProgressChanged $progress")
                tvTipPercentLabel.text = "$progress%"
                computeTipAndTotal()
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        etBaseAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged $s")
                resetSpilt()
                computeTipAndTotal()
            }
        })

        seekBarSpilt.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                computeSpiltTotal()
                updateSpiltDescription(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun resetSpilt() {
        tvSpiltDescription.text = ""
        tvTotalSpilt.text = ""
    }

    private fun updateSpiltDescription(spiltPercentage: Int) {
        val spiltDescription = when (spiltPercentage) {
            in 0..1 -> "$spiltPercentage Person"
            in 2..10 -> "$spiltPercentage People"
            else -> "??"
        }
        tvSpiltDescription.text = spiltDescription
    }

    private fun computeSpiltTotal() {
        if (etBaseAmount.text.isEmpty()) {
            tvTotalSpilt.text = ""
            return
        }
        if (seekBarSpilt.progress == 0) {
            tvTotalSpilt.text = "None"
            return
        }
        //  1. Get total amount and amount of people to spilt with
        val totalAmount = tvTotalAmount.text.toString().toDouble()
        val spiltPersons = seekBarSpilt.progress
        //  2. Compute the total per person
        val totalSpilt = totalAmount / spiltPersons
        //  3. Update the UI
        tvTotalSpilt.text = totalSpilt.toString()
    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }
        tvTipDescription.text = tipDescription
        //  Update the color based on tipPercent
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.color_worst_tip),
            ContextCompat.getColor(this, R.color.color_best_tip)
        ) as Int
        tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal() {
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }
        //1. Get the value of the base and tip percent
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        //2. Compute the tip and total
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount
        //3. Update the UI
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
    }

}