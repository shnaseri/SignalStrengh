package com.shnaseri.strenghmap.loaders

import android.content.Context
import android.util.Log
import androidx.loader.content.AsyncTaskLoader

abstract class DatabaseLoader(context: Context) :
    AsyncTaskLoader<List<*>?>(context) {
    private var mList: List<*>? = null
    abstract fun loadList(): List<*>
    override fun loadInBackground(): List<*> {
        Log.d(TAG, "loadInBackground() ")
        val list = loadList()
        if (list.isNotEmpty()) {
            // check content window is filled
            list.size
        }
        return list
    }

    init {
        Log.d(TAG, "DatabaseLoader() ")
    }

    override fun deliverResult(data: List<*>?) {
        super.deliverResult(data)
        mList = data
        Log.d(TAG, "deliverResult() ")
    }

    override fun onStartLoading() {
        super.onStartLoading()
        if (mList != null) {
            deliverResult(mList)
        }
        if (takeContentChanged() || mList == null) {
            forceLoad()
        }
    }

    override fun onStopLoading() {
        super.onStopLoading()
        cancelLoad()
    }

    companion object {
        private const val TAG = "mobilenetworkstracker"
    }
}
