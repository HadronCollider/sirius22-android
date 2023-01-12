package com.example.siriusproject


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
class project : AppCompatActivity() {

    private lateinit var adapter: FragmentAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private val tabNames: Array<String> = arrayOf(
        "модель",
        "фотографии"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.toolbar_activity_project)

        adapter = FragmentAdapter(this)
        viewPager = findViewById(R.id.tabs)
        viewPager.adapter = adapter
        val ft = supportFragmentManager.beginTransaction()

        tabLayout = findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabNames[position]
            when (position) {
                0 -> {
                    val fragment = Model()
                    ft.replace(R.id.tabs, fragment)
                }
                else -> {
                    val fragment = Model()
                    ft.replace(R.id.tabs, fragment)
                }
            }
        }.attach()


    }

}