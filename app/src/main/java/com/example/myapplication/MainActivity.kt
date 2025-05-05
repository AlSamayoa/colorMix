package com.example.myapplication

//for testing
//import android.util.Log

//Imports needed to run the app
import android.graphics.Color
import android.text.TextWatcher
import android.text.Editable
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.SeekBar


class MainActivity : AppCompatActivity() {

    private lateinit var redSeekBar: SeekBar
    private lateinit var greenSeekBar: SeekBar
    private lateinit var blueSeekBar: SeekBar
    private lateinit var redNum: EditText
    private lateinit var greenNum: EditText
    private lateinit var blueNum: EditText
    private lateinit var redCheckBox: CheckBox
    private lateinit var greenCheckBox: CheckBox
    private lateinit var blueCheckBox: CheckBox
    private lateinit var colorBox: View
    private lateinit var resetButton: Button

    //Function that run when the app starts
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        redSeekBar = findViewById(R.id.redSeekBar)
        greenSeekBar = findViewById(R.id.greenSeekBar)
        blueSeekBar = findViewById(R.id.blueSeekBar)
        redNum = findViewById(R.id.redNum)
        greenNum = findViewById(R.id.greenNum)
        blueNum = findViewById(R.id.blueNum)
        redCheckBox = findViewById(R.id.redCheckBox)
        greenCheckBox = findViewById(R.id.greenCheckBox)
        blueCheckBox = findViewById(R.id.blueCheckBox)
        colorBox = findViewById(R.id.colorBox)
        resetButton = findViewById(R.id.resetButton)

        setupSync(redSeekBar, redNum, redCheckBox)
        setupSync(greenSeekBar, greenNum, greenCheckBox)
        setupSync(blueSeekBar, blueNum, blueCheckBox)

        //function that will seek for click on the Reset Button
        resetButton.setOnClickListener {
            // Uncheck all checkboxes
            redCheckBox.isChecked = false
            greenCheckBox.isChecked = false
            blueCheckBox.isChecked = false

            // Reset SeekBars and EditTexts
            redSeekBar.progress = 0
            greenSeekBar.progress = 0
            blueSeekBar.progress = 0

            redNum.setText("0.0")
            greenNum.setText("0.0")
            blueNum.setText("0.0")

            // Animate color box to black
            val currentColor = (colorBox.background as? android.graphics.drawable.ColorDrawable)?.color ?: Color.BLACK
            val animator = ValueAnimator.ofObject(ArgbEvaluator(), currentColor, Color.BLACK)
            animator.duration = 300 // ms
            animator.addUpdateListener { animation ->
                colorBox.setBackgroundColor(animation.animatedValue as Int)
            }
            animator.start()
        }
    }

    //master function that will check for changes on the seekbar, text, or checkbox to sinc tha changes
    private fun setupSync(seekBar: SeekBar, editText: EditText, checkBox: CheckBox) {
        val updateColor = { updateColorBox() }

        fun syncEnabledState(isChecked: Boolean) {
            seekBar.isEnabled = isChecked
            editText.isEnabled = isChecked
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            syncEnabledState(isChecked)
            updateColor()
        }

        // Initial state checkboxes are unchecked by default
        syncEnabledState(checkBox.isChecked)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val value = progress.toDouble() / 255.0
                    editText.setText("%.2f".format(value))
                }
                updateColor()
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        //change the text in the editText objects when the seekbar changes between 0.0 and 1.0
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().toDoubleOrNull()
                if (value != null && value in 0.0..1.0) {
                    seekBar.progress = (value * 255).toInt()
                }
                updateColor()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    //Function to pass the values on the seekbar to RGB and change the background color of the view
    private fun updateColorBox() {
        val r = if (redCheckBox.isChecked) redSeekBar.progress else 0
        val g = if (greenCheckBox.isChecked) greenSeekBar.progress else 0
        val b = if (blueCheckBox.isChecked) blueSeekBar.progress else 0

        val color = Color.rgb(r, g, b)
        //Log.d("DEBUG", "RGB = ($r, $g, $b)")
        colorBox.setBackgroundColor(color)
    }}