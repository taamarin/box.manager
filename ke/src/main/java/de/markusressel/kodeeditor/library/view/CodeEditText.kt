package de.markusressel.kodeeditor.library.view

import android.content.Context
import android.os.Build
import android.text.Layout
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText
import de.markusressel.kodehighlighter.core.util.EditTextHighlighter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.widget.textChanges
import java.util.concurrent.TimeUnit

/**
 * EditText modified for longer texts and support for syntax highlighting
 */
class CodeEditText
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
    : AppCompatEditText(context, attrs, defStyleAttr) {

    /**
     * The current syntax highlighter
     */
    var highlighter: EditTextHighlighter? = null
        set(value) {
            // clear any old style
            field?.clearAppliedStyles()

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
        reInit()
    }

    private fun reInit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
        }

        initSyntaxHighlighter()
        isClickable = true
        isFocusableInTouchMode = true
    }

    @OptIn(FlowPreview::class)
    private fun initSyntaxHighlighter() {
        highlightingJob?.cancel("Reinitializing")

        highlighter?.let {
            refreshSyntaxHighlighting()
            highlightingJob = textChanges()
                    .debounce(highlightingTimeout.second.toMillis(highlightingTimeout.first))
                    .onEach {
                        refreshSyntaxHighlighting()
                    }
                    .catch {
                        Log.e(CodeTextView.TAG, "Error while refreshing syntax highlighting", it)
                    }
                    .launchIn(CoroutineScope(Job() + Dispatchers.Main))
        }

    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        refreshSyntaxHighlighting()
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
        highlighter?.refreshHighlighting()
                ?: Log.w(TAG, "No syntax highlighter is set!")
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        selectionChangedListener?.onSelectionChanged(selStart, selEnd, hasSelection())
    }

    companion object {
        const val TAG = "CodeEditText"
    }

}