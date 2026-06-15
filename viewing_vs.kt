package com.example.budgetasyougo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import android.app.AlertDialog
import android.provider.ContactsContract.CommonDataKinds.Email
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager

import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import org.json.JSONObject

class dashboard : AppCompatActivity() {

    private lateinit var sliderView: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_page)




        val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val prefs = getSharedPreferences("budgetAppPrefs", Context.MODE_PRIVATE)
        val userEmail = sharedPrefs.getString("email", "") ?: ""
        val balanceKey = "balance_$userEmail"

        val currentBalance = prefs.getFloat(balanceKey, -1f)
        val rootView = findViewById<View>(android.R.id.content)
        val balance = getUserBalance()
        if (currentBalance < 0f) {
            // first time, no budget saved yet
            askToSetInitialBudget(balanceKey, rootView)
        } else if (currentBalance == 0f) {
            // balance zero, suggest topping up
            askToSetInitialBudget(balanceKey, rootView)
        } else {
            // balance exists, just show or proceed
            findViewById<TextView>(R.id.balanceAmount).text = " %.2f".format(currentBalance)+" ZAR"
        }

        findViewById<TextView>(R.id.mainBudgetAmount).text = ""+balance+" ZAR"

totals()
        // Welcome user
        val userName = sharedPrefs.getString("name", "")
        val nameTextView: TextView = findViewById(R.id.userName)
        nameTextView.text = "Budget As you Walk ${userName ?: "User"}"

        // Set up the slider
        sliderView = findViewById(R.id.sliderView)

        val cardItems = listOf(
            CardInfo(
                R.drawable.shop,
                R.drawable.budget,
                "Track Spending",
                "Tracking your spending helps you understand exactly where your money goes. By staying aware of your daily expenses, you can identify patterns, avoid unnecessary purchases, and take control of your financial future with confidence."
            )
,
            CardInfo(
                R.drawable.vs,
                R.drawable.budget,
                "Visual Budget",
                "Visualizing your budget lets you quickly see how much you’ve spent, what remains, and where you’re heading. It makes managing money easier by turning numbers into clear insights, helping you stay on track and avoid surprises."
            )
,
            CardInfo(
                R.drawable.gm,
                R.drawable.budget,
                "Budget Game",
                "Learn how to manage money through fun, interactive challenges. The Budget Game helps you build smart financial habits by turning real-life budgeting scenarios into engaging and rewarding experiences."
            )

        )

        sliderView.adapter = SlideCardAdapter(cardItems)


        val categories = loadCategories()
        val (unlockedAchievements, unlockedCount) = getUnlockedAchievements(categories)

        val totalAchievements = 10
        val progress = "$unlockedCount/$totalAchievements"


       findViewById<TextView>(R.id.gamins).text ="Rewards "+progress

    }

    fun totals(){

        val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val prefs = getSharedPreferences("budgetAppPrefs", Context.MODE_PRIVATE)
        val userEmail = sharedPrefs.getString("email", "") ?: ""
        val mainKey = "balance_$userEmail"

        // Check if there's enough main budget left for this category budget
        val mainBudget = prefs.getFloat(mainKey, -1f).toDouble()
        val categoryKey = "categories_$userEmail"
        val existingCategoriesJson = prefs.getString(categoryKey, "[]")
        val categoryArray = JSONArray(existingCategoriesJson)
        var totalCategoryBudget = 0.0
        for (i in 0 until categoryArray.length()) {
            val obj = categoryArray.getJSONObject(i)
            totalCategoryBudget += obj.optDouble("budget", 0.0)
        }

        findViewById<TextView>(R.id.spendingAmount).text =""+totalCategoryBudget+" ZAR"

    }





    private fun loadCategories(): List<JSONObject> {
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userEmail = sharedPref.getString("email", "") ?: ""
        val categoryKey = "categories_$userEmail"
        val prefs = getSharedPreferences("budgetAppPrefs", MODE_PRIVATE)

        val jsonStr = prefs.getString(categoryKey, "[]") ?: "[]"
        val jsonArray = JSONArray(jsonStr)
        val list = mutableListOf<JSONObject>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getJSONObject(i))
        }
        return list
    }

    // Change return type to Pair<List<gaming.Achievement>, Int> so you also get count
    private fun getUnlockedAchievements(categories: List<JSONObject>): Pair<List<gaming.Achievement>, Int> {
        val unlocked = mutableListOf<gaming.Achievement>()
        var counting = 0

        val categoryCount = categories.size
        val totalBudget = categories.sumOf { it.optDouble("budget", 0.0) }
        val totalSpent = categories.sumOf { it.optDouble("spent", 0.0) }

        fun add(title: String, desc: String, img: Int) {
            unlocked.add(gaming.Achievement(title, desc, img))
            counting++  // count every added achievement
        }

        // Achievements based on number of categories
        if (categoryCount >= 1) add("First Category", "Created your first category!", R.drawable.baa)
        if (categoryCount >= 3) add("Triple Tracker", "You now have 3+ categories!", R.drawable.baa)
        if (categoryCount >= 5) add("Budget Boss", "You now manage 5+ categories!", R.drawable.baa)
        if (categoryCount >= 10) add("Planner Master", "10 categories created!", R.drawable.baa)

        // Spending Achievements
        if (categories.any { it.optDouble("spent", 0.0) < it.optDouble("budget", 0.0) })
            add("Smart Spender", "You spent less than budget in a category!", R.drawable.baa)

        if (categories.count { it.optDouble("spent", 0.0) == 0.0 } >= 3)
            add("Thrifty Beginner", "You haven’t spent in 3 categories!", R.drawable.baa)

        if (totalSpent < totalBudget * 0.5)
            add("Economist", "You’ve spent less than 50% of your total budget!", R.drawable.baa)

        if (totalSpent > totalBudget)
            add("Warning Sign", "You've overspent across all categories!", R.drawable.budget)

        if (categories.all { it.optDouble("spent", 0.0) <= it.optDouble("budget", 0.0) && it.optDouble("budget", 0.0) > 0 })
            add("Perfect Planner", "All your spending is within budget!", R.drawable.baa)

        if (categoryCount >= 1 && totalSpent == 0.0)
            add("Budget Setup Complete", "You've created categories but haven't spent yet!", R.drawable.budget)

        return Pair(unlocked, counting)
    }







    private fun getUserBalance(): Float {
        val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userEmail = sharedPrefs.getString("email", "") ?: ""
        val mainSpends = getSharedPreferences("main_spends", Context.MODE_PRIVATE)
        return mainSpends.getFloat(userEmail, 0f)
    }

    private fun initializeUserBalance(initialBalance: Float) {
        val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userEmail = sharedPrefs.getString("email", "") ?: ""
        val mainSpends = getSharedPreferences("main_spends", Context.MODE_PRIVATE)

        // Check if user has a balance
        if (!mainSpends.contains(userEmail)) {
            mainSpends.edit().putFloat(userEmail, initialBalance.toFloat()).apply()
        }
    }
    private fun addToUserBalance(amountToAdd: Float) {
        val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userEmail = sharedPrefs.getString("email", "") ?: ""
        val mainSpends = getSharedPreferences("main_spends", Context.MODE_PRIVATE)

        val currentBalance = mainSpends.getFloat(userEmail, 0f)
        val newBalance = currentBalance + amountToAdd

        mainSpends.edit().putFloat(userEmail, newBalance).apply()
    }
    data class CardInfo(
        val backgroundResId: Int,
        val iconResId: Int,
        val title: String,
        val description: String
    )
    private fun askToSetInitialBudget(balanceKey: String, rootView: View) {
        AlertDialog.Builder(this)
            .setTitle("Set Initial Budget")
            .setMessage("You have no budget set yet. Would you like to set your starting budget now?")
            .setPositiveButton("Yes") { _, _ ->
                // Show top-up dialog to set initial balance
                showTopUpDialog(balanceKey, rootView)
            }
            .setNegativeButton("No", null)
            .show()
    }


    private fun showTopUpDialog(balanceKey: String, rootView: View) {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_top_up, null)

        val currentBalanceText = view.findViewById<TextView>(R.id.currentBalance)
        val topUpAmountField = view.findViewById<EditText>(R.id.topUpAmount)
        val saveButton = view.findViewById<Button>(R.id.saveTopUpButton)
        val closeButton = view.findViewById<Button>(R.id.closeButton)

        // Use budgetAppPrefs here
        val prefs = getSharedPreferences("budgetAppPrefs", Context.MODE_PRIVATE)
        val currentBalance = prefs.getFloat(balanceKey, 0f)
        currentBalanceText.text = " %.2f".format(currentBalance) +" ZAR"
        initializeUserBalance(currentBalance)
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()

        saveButton.setOnClickListener {
            val topUp = topUpAmountField.text.toString().toFloatOrNull()
            if (topUp == null || topUp <= 0f) {
                topUpAmountField.error = "Please enter a valid amount"
                return@setOnClickListener
            }
            val newBalance = currentBalance + topUp
            prefs.edit().putFloat(balanceKey, newBalance).apply()
addToUserBalance(topUp)
            Snackbar.make(rootView, "Main budget updated to R %.2f".format(newBalance), Snackbar.LENGTH_LONG).show()

            // Update dashboard balance display too
            findViewById<TextView>(R.id.balanceAmount).text = "%.2f".format(newBalance)+" ZAR"

            val balance = getUserBalance()
            findViewById<TextView>(R.id.mainBudgetAmount).text = ""+balance+" ZAR"

            dialog.dismiss()
        }

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun create_category(view: View) {

        startActivity(Intent(this, category::class.java))


    }

    fun SHowtotal(view: View) {

        val intent = Intent(this, spendings::class.java)
        startActivity(intent)
        finish()

    }

    fun adds(view: View) {

        val intent = Intent(this, addings::class.java)
        startActivity(intent)
        finish()
    }

    fun viewing(view: View) {

        val intent = Intent(this, viewing_categories::class.java)
        startActivity(intent)
        finish()

    }

    fun vs_view(view: View) {

        val intent = Intent(this, viewing_vs::class.java)
        startActivity(intent)
        finish()
    }

    fun spendings(view: View) {

        val intent = Intent(this, viewing_sp::class.java)
        startActivity(intent)
        finish()

    }

    fun Gmaings(view: View) {

        val intent = Intent(this, gaming::class.java)
        startActivity(intent)
        finish()

    }

    fun logout(view: View) {
        val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().apply()

        val intent = Intent(this, login::class.java)
        startActivity(intent)
        finish()
    }


}
