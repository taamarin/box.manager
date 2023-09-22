package de.markusressel.kodeeditor.library.view

import android.content.Context
import android.os.Build
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import de.markusressel.kodehighlighter.core.util.StatefulSpannableHighlighter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.widget.textChanges
import java.util.concurrent.TimeUnit

/**
 * TextView modified for longer texts and support for syntax highlighting
 */
class CodeTextView
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
    : AppCompatTextView(context, attrs, defStyleAttr) {

    /**
     * The current syntax highlighter
     */
    var highlighter: StatefulSpannableHighlighter? = null
        set(value) {
            // clear any old style
            field?.clearAppliedStyles(text as Spannable)

            // set new highlighter
            field = value

            // and initialize it
            initSyntaxHighlighter()
        }

    /**
     * Listener for selection changes
     */
    var selectionChangedListener: SelectionChangedListener? = null

    private var highlightingTimeout = 50L to TimeUnit.MILLISECONDS
    private var highlightingJob: Job? = null

    init {
        CoroutineScope(Job() + Dispatchers.Main).launch {
            reInit()
        }
    }

    private fun reInit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
        }

        initSyntaxHighlighter()
    }

    @OptIn(FlowPreview::class)
    private fun initSyntaxHighlighter() {
        highlightingJob?.cancel("Reinitializing")

        if (highlighter != null) {
            CoroutineScope(Dispatchers.Main).launch {
                refreshSyntaxHighlighting()
            }

            highlightingJob = textChanges()
                    .debounce(highlightingTimeout.second.toMillis(highlightingTimeout.first))
                    .onEach {
                        refreshSyntaxHighlighting()
                    }
                    .catch {
                        Log.e(TAG, "Error while refreshing syntax highlighting", it)
                    }
                    .launchIn(CoroutineScope(Job() + Dispatchers.Main))
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(SpannableString.valueOf(text), BufferType.SPANNABLE)
        CoroutineScope(Job() + Dispatchers.Default).launch {
            refreshSyntaxHighlighting()
        }
    }

    /**
     * Set the timeout before new text is highlighted after the user has stopped typing.
     *
     * @param timeout arbitrary value
     * @param timeUnit the time unit to use
     */
    @Suppress("unused")
    fun setHighlightingTimeout(timeout: Long, timeUnit: TimeUnit) {
        highlightingTimeout = timeout to timeUnit
        reInit()
    }

    /**
     * Get the current syntax highlighter timeout in milliseconds.
     *
     * @return timeout in milliseconds
     */
    @Suppress("unused")
    fun getHighlightingTimeout(): Long {
        return highlightingTimeout.second.toMillis(highlightingTimeout.first)
    }

    /**
     * Force a refresh of the syntax highlighting
     */
    @Synchronized
    fun refreshSyntaxHighlighting() {
        if (highlighter == null) {
            Log.w(TAG, "No syntax highlighter is set!")
        }

        highlighter?.apply {
            CoroutineScope(Dispatchers.Main).launch {
                highlight(text as Spannable)
            }
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        selectionChangedListener?.onSelectionChanged(selStart, selEnd, hasSelection())
    }

    companion object {
        const val TAG = "CodeTextView"
    }

}