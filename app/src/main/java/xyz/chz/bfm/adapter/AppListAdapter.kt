package xyz.chz.bfm.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.chz.bfm.R
import xyz.chz.bfm.data.AppInfo
import xyz.chz.bfm.databinding.ItemApplistBinding

class AppListAdapter(
    val activity: Activity,
    val apps: List<AppInfo>,
    blacklist: MutableSet<String>?
) :
    RecyclerView.Adapter<AppListAdapter.BaseViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    val blacklist = if (blacklist == null) HashSet() else HashSet(blacklist)

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is AppViewHolder) {
            val appInfo = apps[position - 1]
            holder.bind(appInfo)
        }
    }

    override fun getItemCount() = apps.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val ctx = parent.context
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = View(ctx)
                view.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ctx.resources.getDimensionPixelSize(R.dimen.list_header_height) * 0
                )
                BaseViewHolder(view)
            }

            else -> AppViewHolder(
                ItemApplistBinding.inflate(
                    LayoutInflater.from(ctx),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int) =
        if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM

    open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class AppViewHolder(private val item: ItemApplistBinding) :
        BaseViewHolder(item.root),
        View.OnClickListener {
        private lateinit var appInfo: AppInfo
        private val inBlacklist: Boolean get() = blacklist.contains(appInfo.packageName)

        fun bind(appInfo: AppInfo) {
            this.appInfo = appInfo
            with(item) {
                icon.setImageDrawable(appInfo.appIcon)
                checkBox.isChecked = inBlacklist
                item.packageName.text = appInfo.packageName
                if (appInfo.isSystemApp) {
                    item.name.text = String.format("** %1s", appInfo.appName)
                } else {
                    item.name.text = appInfo.appName
                }
            }
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (inBlacklist) {
                blacklist.remove(appInfo.packageName)
                item.checkBox.isChecked = false
            } else {
                blacklist.add(appInfo.packageName)
                item.checkBox.isChecked = true
            }
        }
    }
}