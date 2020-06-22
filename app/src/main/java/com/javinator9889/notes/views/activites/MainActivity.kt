package com.javinator9889.notes.views.activites

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.IdRes
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.javinator9889.notes.R
import com.javinator9889.notes.data.handlers.MainActivityHandler
import com.javinator9889.notes.listeners.OnItemClickedListener
import com.javinator9889.notes.utils.views.clearFocusCloseKeyboard
import com.javinator9889.notes.views.activites.base.TopBarSheetFragmentActivity
import com.javinator9889.notes.views.items.NoteItem
import com.mikepenz.fastadapter.IAdapter
import kotlinx.android.synthetic.main.notes.*


class MainActivity : TopBarSheetFragmentActivity(), OnItemClickedListener {
    private val handler = MainActivityHandler(this)
    @IdRes
    override val bottomSheetId: Int = R.id.contentLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filterButton.setOnClickListener {
            if (toggleBottomSheet() == STATE_EXPANDED) searchEditText.requestFocus()
            else searchEditText.clearFocusCloseKeyboard(this)
        }
        handler.setupViews(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        handler.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        when {
            sheetBehavior.state == STATE_HALF_EXPANDED -> sheetBehavior.state = STATE_EXPANDED
            supportFragmentManager.backStackEntryCount > 0 -> handler.loadPreviousFragment()
            else -> super.onBackPressed()
        }
    }

    override fun onItemClick(
        v: View?,
        adapter: IAdapter<NoteItem>,
        item: NoteItem,
        position: Int
    ): Boolean { handler.launchEditFragment(item); return true }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        with(menuInflater) {
            inflate(R.menu.app_menu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.github -> {
                val website = Uri.parse("https://gitlab.javinator9889.com/Javinator9889/notes")
                with(Intent(Intent.ACTION_VIEW, website)) {
                    if (resolveActivity(this@MainActivity.packageManager) != null)
                        startActivity(this)
                    else
                        Toast.makeText(this@MainActivity, R.string.no_browser, Toast.LENGTH_LONG).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}