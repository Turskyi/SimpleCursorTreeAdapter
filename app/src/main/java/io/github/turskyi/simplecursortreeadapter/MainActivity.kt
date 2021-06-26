package io.github.turskyi.simplecursortreeadapter

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.ExpandableListView
import android.widget.SimpleCursorTreeAdapter
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    var elvMain: ExpandableListView? = null
    var db: DB? = null

    /** Called when the activity is first created.  */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // connect to DB
        db = DB(this)
        db!!.open()

        // preparing data by groups for the adapter
        val cursor: Cursor = db!!.companyData
        startManagingCursor(cursor)
        // data mapping and View for groups
        val groupFrom = arrayOf(DB.COMPANY_COLUMN_NAME)
        val groupTo = intArrayOf(android.R.id.text1)
        // mapping data and View for elements
        val childFrom = arrayOf(DB.PHONE_COLUMN_NAME)
        val childTo = intArrayOf(android.R.id.text1)

        // create an adapter and set up a list
        val sctAdapter: SimpleCursorTreeAdapter = MyAdapter(
            this, cursor,
            android.R.layout.simple_expandable_list_item_1, groupFrom,
            groupTo, android.R.layout.simple_list_item_1, childFrom,
            childTo
        )
        elvMain = findViewById<View>(R.id.elvMain) as ExpandableListView
        elvMain!!.setAdapter(sctAdapter)
    }

    override fun onDestroy() {
        super.onDestroy()
        db!!.close()
    }

    internal inner class MyAdapter(
        context: Context?, cursor: Cursor?, groupLayout: Int,
        groupFrom: Array<String>?, groupTo: IntArray?, childLayout: Int,
        childFrom: Array<String>?, childTo: IntArray?
    ) :
        SimpleCursorTreeAdapter(
            context, cursor, groupLayout, groupFrom, groupTo,
            childLayout, childFrom, childTo
        ) {
        override fun getChildrenCursor(groupCursor: Cursor): Cursor {
            // get the cursor by elements for a specific group
            val idColumn: Int = groupCursor.getColumnIndex(DB.COMPANY_COLUMN_ID)
            return db!!.getPhoneData(groupCursor.getLong(idColumn))
        }
    }
}