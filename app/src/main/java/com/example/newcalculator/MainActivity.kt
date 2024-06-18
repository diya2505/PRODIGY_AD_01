package com.example.newcalculator


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.newcalculator.databinding.ActivityMainBinding
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var lastNumeric = false
    private var stateError = false
    private var lastDot = false
    private lateinit var expression: Expression

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateTextWithCursor()
    }

    private fun updateTextWithCursor() {
        val text = binding.textView2.text.toString()
        if (!text.endsWith("|")) {
            binding.textView2.text = "$text|"
        }
    }

    private fun removeCursor() {
        val text = binding.textView2.text.toString()
        if (text.endsWith("|")) {
            binding.textView2.text = text.dropLast(1)
        }
    }

    fun onAllClearCLick(view: View) {
        removeCursor()
        binding.textView2.text = ""
        binding.textView.text = ""
        stateError = false
        lastDot = false
        lastNumeric = false
        binding.textView.visibility = View.GONE
        updateTextWithCursor()
    }

    fun onEqualCLick(view: View) {
        onEqual()
        binding.textView2.text = binding.textView.text.toString().drop(1)
        updateTextWithCursor()
    }

    fun onDigitCLick(view: View) {
        removeCursor()
        if (stateError) {
            binding.textView2.text = (view as Button).text
            stateError = false
        } else {
            binding.textView2.append((view as Button).text)
        }
        lastNumeric = true
        onEqual()
        updateTextWithCursor()
    }

    fun onOperatorCLick(view: View) {
        removeCursor()
        if (!stateError && lastNumeric) {
            binding.textView2.append((view as Button).text)
            lastDot = false
            lastNumeric = false
            onEqual()
            updateTextWithCursor()
        }
    }

    fun onSignsCLick(view: View) {}

    fun onBackCLick(view: View) {
        removeCursor()
        val currentText = binding.textView2.text.toString()
        if (currentText.isNotEmpty()) {
            binding.textView2.text = currentText.dropLast(1)
            try {
                val lastChar = binding.textView2.text.toString().last()
                if (lastChar.isDigit()) {
                    onEqual()
                }
            } catch (e: Exception) {
                binding.textView.text = ""
                binding.textView.visibility = View.GONE
                Log.e("last char error", e.toString())
            }
        }
        updateTextWithCursor()
    }

    fun onEqual() {
        removeCursor()
        if (lastNumeric && !stateError) {
            val txt = binding.textView2.text.toString()
            expression = ExpressionBuilder(txt).build()

            if (txt.isNotEmpty() && txt.last().isOperator()) {
                binding.textView.text = "Invalid Format"
                stateError = true
                lastNumeric = false
                updateTextWithCursor()
                return
            }

            expression = ExpressionBuilder(txt).build()


            try {
                expression = ExpressionBuilder(txt).build()
                val result = expression.evaluate()

                // Format the result to remove trailing zeros after the decimal point
                val resultText = if (result == result.toInt().toDouble()) {
                    result.toInt().toString()
                } else {
                    String.format("%.8f", result).replace(Regex("\\.?0+$"), "")
                }

                binding.textView.visibility = View.VISIBLE
                binding.textView.text = "=$resultText"


            } catch (ex: ArithmeticException) {
                    Log.e("evaluate error", ex.toString())
                    binding.textView.text = "Error"
                    stateError = true
                    lastNumeric = false
                }
            updateTextWithCursor()
            }

    }

    // Extension function to check if a character is an operator
    private fun Char.isOperator(): Boolean {
        return this == '+' || this == '-' || this == '*' || this == '/'
    }
}
