package com.laihua.framework.ui.adapter.kotlinAdapter

import android.content.Context
import android.support.annotation.IdRes
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.ViewGroup
import com.laihua.framework.utils.DataUtils

/**
 *@version:
 *@FileDescription:
 *@Author:jing
 *@Since:2018/6/7
 *@ChangeList:
 */
class RAdapter<D> private constructor(var ctx: Context,
                                      var classes: ArrayList<Class<out AbsItemViewK<D>>>) : RecyclerView.Adapter<RAdapter.ViewHolderK<D>>() {

    var data: ArrayList<D> = ArrayList()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    private val itemMgr by lazy {
        ItemMgr<D>()
    }

    init {
        putItemClasses(this.classes)
    }

    var keyTags: SparseArray<Any> = SparseArray()

    fun setTag(@IdRes key: Int, tag: Any): RAdapter<D> {
        keyTags.put(key, tag)
        return this
    }

    fun getTag(@IdRes key: Int): Any? {
        return keyTags.get(key)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolderK<D> {
        val itemView = itemMgr.createItemView(viewType)
        itemView.rAdapter = this
        return itemView.createViewHolder(ctx, parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolderK<D>?, position: Int) {
        holder?.item?.showItem(position, data.getOrNull(position))
    }

    override fun onBindViewHolder(holder: ViewHolderK<D>, position: Int, payloads: MutableList<Any>) {
        if (DataUtils.isEmptyList(payloads)) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            holder?.item?.showItem(position, data.getOrNull(position), payloads)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (itemMgr.isSingleType()) {
            return super.getItemViewType(position)
        } else {
            return itemMgr.getItemType(data.getOrNull(position)?.hashCode())
        }
    }

    fun putItemClass(clazz: Class<AbsItemViewK<D>>) {
        itemMgr.putItemClass(clazz)
    }

    fun putItemClasses(classes: ArrayList<Class<out AbsItemViewK<D>>>) {
        if (classes.isNotEmpty()) {
            classes.forEach {
                itemMgr.putItemClass(it)
            }
        }
    }

    fun setDataWithDiff(dataList: ArrayList<D>) {
        val diffCallBack = DiffCallback()
        diffCallBack.setData(data, dataList)
        data = dataList
        val calculateDiff = DiffUtil.calculateDiff(diffCallBack)
        calculateDiff.dispatchUpdatesTo(this)
    }

    private class DiffCallback : DiffUtil.Callback() {

        private var mOldData: List<*>? = null
        private var mNewData: List<*>? = null

        fun setData(oldData: List<*>, newData: List<*>) {
            mOldData = oldData
            mNewData = newData
        }

        override fun getOldListSize(): Int {
            mOldData?.apply {
                return size
            }
            return 0
        }

        override fun getNewListSize(): Int {
            mNewData?.apply {
                return size
            }
            return 0
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldData?.get(oldItemPosition) === mNewData?.get(newItemPosition)
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldData?.get(oldItemPosition) == mNewData?.get(newItemPosition)
        }
    }

    class ViewHolderK<D>(val item: AbsItemViewK<D>) : RecyclerView.ViewHolder(item.rootView) {

    }

    companion object {

        fun <D> create(ctx: Context, classes: ArrayList<Class<out AbsItemViewK<D>>>): RAdapter<D> =
                RAdapter<D>(ctx, classes)
    }
}
