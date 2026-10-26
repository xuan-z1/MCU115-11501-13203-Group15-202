package com.example.mycalculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    //  1. 宣告介面元件變數
    private lateinit var tvExpression: TextView
    private lateinit var tvResult: TextView

    // 第 1 列按鈕
    private lateinit var btnAC: Button
    private lateinit var btnClear: Button
    private lateinit var btnBackspace: Button
    private lateinit var btnDivide: Button

    // 第 2 列按鈕
    private lateinit var btn7: Button
    private lateinit var btn8: Button
    private lateinit var btn9: Button
    private lateinit var btnMultiply: Button

    // 第 3 列按鈕
    private lateinit var btn4: Button
    private lateinit var btn5: Button
    private lateinit var btn6: Button
    private lateinit var btnSubtract: Button

    // 第 4 列按鈕
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btnAdd: Button

    // 第 5 列按鈕
    private lateinit var btnDot: Button
    private lateinit var btn0: Button
    private lateinit var btnEquals: Button

    // ===== 2. 宣告狀態變數 =====
    private var currentNumber: String = "0"        // 目前正在輸入的數字字串，預設 "0"
    private var firstOperand: Double? = null      // 儲存第一個運算元
    private var pendingOperator: String? = null   // 儲存尚未執行的運算符 (+ − × ÷)，預設 null
    private var shouldStartNewNumber: Boolean = false // 布林值：判斷是否要在下次按數字時重新輸入新數字

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 處理全螢幕與系統欄邊界
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 綁定介面元件
        initViews()

        // 綁定按鈕點擊事件
        setupListeners()
    }

    private fun initViews() {
        tvExpression = findViewById(R.id.tvExpression)
        tvResult = findViewById(R.id.tvResult)

        btnAC = findViewById(R.id.btnAC)
        btnClear = findViewById(R.id.btnClear)
        btnBackspace = findViewById(R.id.btnBackspace)
        btnDivide = findViewById(R.id.btnDivide)

        btn7 = findViewById(R.id.btn7)
        btn8 = findViewById(R.id.btn8)
        btn9 = findViewById(R.id.btn9)
        btnMultiply = findViewById(R.id.btnMultiply)

        btn4 = findViewById(R.id.btn4)
        btn5 = findViewById(R.id.btn5)
        btn6 = findViewById(R.id.btn6)
        btnSubtract = findViewById(R.id.btnSubtract)

        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        btnAdd = findViewById(R.id.btnAdd)

        btnDot = findViewById(R.id.btnDot)
        btn0 = findViewById(R.id.btn0)
        btnEquals = findViewById(R.id.btnEquals)
    }

    private fun setupListeners() {
        // 數字鍵點擊事件
        btn0.setOnClickListener { appendNumber("0") }
        btn1.setOnClickListener { appendNumber("1") }
        btn2.setOnClickListener { appendNumber("2") }
        btn3.setOnClickListener { appendNumber("3") }
        btn4.setOnClickListener { appendNumber("4") }
        btn5.setOnClickListener { appendNumber("5") }
        btn6.setOnClickListener { appendNumber("6") }
        btn7.setOnClickListener { appendNumber("7") }
        btn8.setOnClickListener { appendNumber("8") }
        btn9.setOnClickListener { appendNumber("9") }

        // 小數點
        btnDot.setOnClickListener { appendDot() }

        // 運算符鍵點擊事件
        btnAdd.setOnClickListener { setOperator("+") }
        btnSubtract.setOnClickListener { setOperator("−") }
        btnMultiply.setOnClickListener { setOperator("×") }
        btnDivide.setOnClickListener { setOperator("÷") }

        // 功能鍵點擊事件
        btnEquals.setOnClickListener { calculateResult() }
        btnClear.setOnClickListener { clearEntry() }
        btnAC.setOnClickListener { clearAll() }
        btnBackspace.setOnClickListener { backspace() }
    }

    private fun appendNumber(digit: String) {
        if (shouldStartNewNumber || currentNumber == "Error") {
            // 若上一次剛按完等號 (=)，開始輸入全新數字時清空上方算式
            if (pendingOperator == null) {
                firstOperand = null
                tvExpression.text = ""
            }
            currentNumber = digit
            shouldStartNewNumber = false
        } else {
            if (currentNumber == "0") {
                currentNumber = digit
            } else {
                currentNumber += digit
            }
        }
        updateDisplay()
    }

    private fun appendDot() {
        if (shouldStartNewNumber || currentNumber == "Error") {
            // 若上一次剛按完等號 (=)，開始輸入全新數字時清空上方算式
            if (pendingOperator == null) {
                firstOperand = null
                tvExpression.text = ""
            }
            currentNumber = "0."
            shouldStartNewNumber = false
        } else if (!currentNumber.contains(".")) {
            currentNumber += "."
        }
        updateDisplay()
    }


    private fun setOperator(op: String) {
        if (currentNumber == "Error") return

        val inputVal = currentNumber.toDoubleOrNull() ?: 0.0

        // 若先前已選擇過運算符且尚未按等號，連續按下新的運算符時先計算前一次結果
        if (pendingOperator != null && !shouldStartNewNumber) {
            val result = calculate(firstOperand ?: 0.0, inputVal, pendingOperator!!)
            if (result == null) {
                showError()
                return
            }
            firstOperand = result
            currentNumber = formatDouble(result)
        } else {
            firstOperand = inputVal
        }

        pendingOperator = op
        shouldStartNewNumber = true
        updateDisplay()
    }


    private fun calculateResult() {
        if (pendingOperator == null || firstOperand == null || currentNumber == "Error") return

        val secondOperand = currentNumber.toDoubleOrNull() ?: 0.0
        val op = pendingOperator!!

        val result = calculate(firstOperand!!, secondOperand, op)

        if (result == null) {
            showError()
        } else {
            val firstStr = formatDouble(firstOperand!!)
            val secondStr = formatDouble(secondOperand)
            val resultStr = formatDouble(result)

            // 顯示完整算式在 tvExpression (例如「1831 × 2 =」)
            tvExpression.text = "$firstStr $op $secondStr ="
            tvResult.text = resultStr

            // 讓結果能被繼續運算
            currentNumber = resultStr
            firstOperand = result
            pendingOperator = null
            shouldStartNewNumber = true
        }
    }

    private fun clearEntry() {
        currentNumber = "0"
        shouldStartNewNumber = false
        tvResult.text = "0"
    }

    private fun clearAll() {
        currentNumber = "0"
        firstOperand = null
        pendingOperator = null
        shouldStartNewNumber = false
        tvExpression.text = ""
        tvResult.text = "0"
    }


    private fun backspace() {
        if (shouldStartNewNumber || currentNumber == "Error") return

        if (currentNumber.length > 1) {
            currentNumber = currentNumber.substring(0, currentNumber.length - 1)
            if (currentNumber == "-" || currentNumber == "-0") {
                currentNumber = "0"
            }
        } else {
            currentNumber = "0"
        }
        tvResult.text = currentNumber
    }

    private fun updateDisplay() {
        tvResult.text = currentNumber
        if (firstOperand != null && pendingOperator != null) {
            tvExpression.text = "${formatDouble(firstOperand!!)} $pendingOperator"
        }
    }

    private fun calculate(num1: Double, num2: Double, op: String): Double? {
        return when (op) {
            "+" -> num1 + num2
            "−", "-" -> num1 - num2
            "×", "*" -> num1 * num2
            "÷", "/" -> if (num2 == 0.0) null else num1 / num2
            else -> num2
        }
    }


    private fun showError() {
        tvResult.text = "Error"
        tvExpression.text = ""
        currentNumber = "Error"
        firstOperand = null
        pendingOperator = null
        shouldStartNewNumber = true
    }


    private fun formatDouble(value: Double): String {
        return if (value % 1.0 == 0.0 && !value.isInfinite() && !value.isNaN()) {
            value.toLong().toString()
        } else {
            value.toString()
        }
    }
}