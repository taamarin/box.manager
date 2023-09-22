package de.markusressel.kodeeditor.library.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import com.otaliastudios.zoom.ZoomApi
import com.otaliastudios.zoom.ZoomLayout
import de.markusressel.kodeeditor.library.R
import de.markusressel.kodeeditor.library.extensions.getColor
import de.markusressel.kodeeditor.library.extensions.setViewBackgroundWithoutResettingPadding
import de.markusressel.kodehighlighter.core.LanguageRuleBook
import de.markusressel.kodehighlighter.core.util.EditTextHighlighter
import de.markusressel.kodehighlighter.core.util.StatefulSpannableHighlighter

/**
 * Code Editor that allows pinch-to-zoom
 */
open class CodeEditorView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ZoomLayout(context, attrs, defStyleAttr), SelectionChangedListener {

    /**
     * The actual text editor content
     */
    lateinit var codeEditText: CodeEditText

    /**
     * A text view for the non-editable state
     */
    lateinit var codeTextView: CodeTextView

    /**
     * The currently active syntax highlighter (if any)
     */
    var languageRuleBook: LanguageRuleBook?
        get() = codeEditText.highlighter?.languageRuleBook
        set(value) {
            if (value != null) {
                codeEditText.highlighter = EditTextHighlighter(codeEditText, value)
                codeTextView.highlighter = StatefulSpannableHighlighter(value, value.defaultColorScheme)
            } else {
                codeEditText.highlighter = null
                codeTextView.highlighter = null
            }
        }

    /**
     * Listener for selection changes
     */
    var selectionChangedListener: SelectionChangedListener? = null

    /**
     * The current text
     */
    var text: String
        get() = codeEditText.text?.toString() ?: ""
        set(value) {
            codeEditText.setText(value)
            codeTextView.text = value
        }

    /** The start index of the current selection */
    val selectionStart: Int
        get() {
            val activeView: TextView = if (editable) codeEditText else codeTextView
            return activeView.selectionStart
        }

    /** The end index of the current selection */
    val selectionEnd: Int
        get() {
            val activeView: TextView = if (editable) codeEditText else codeTextView
            return activeView.selectionEnd
        }

    /** True when a range is selected */
    val hasSelection: Boolean
        get() {
            val activeView: TextView = if (editable) codeEditText else codeTextView
            return activeView.hasSelection()
        }

    /**
     * Set the text in the editor
     *
     * @param text string resource of the new text to set
     */
    @Suppress("unused")
    fun setText(@StringRes text: Int) {
        this.text = context.getString(text)
    }

    /**
     * Controls whether the text is editable
     */
    var editable: Boolean
        get() = codeEditText.visibility == View.VISIBLE
        set(value) {
            if (value) {
                codeEditText.visibility = View.VISIBLE
                codeTextView.visibility = View.GONE
            } else {
                codeTextView.text = codeEditText.text
                codeEditText.visibility = View.GONE
                codeTextView.visibility = View.VISIBLE
            }
        }

    init {
        setHasClickableChildren(true)
        isFocusableInTouchMode = true

        inflateViews(LayoutInflater.from(context))
        readParameters(attrs, defStyleAttr)

        setListeners()
    }

    private fun readParameters(attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CodeEditorView, defStyleAttr, 0)

        val editTextBackgroundColor = a.getColor(context,
                defaultColor = Color.WHITE,
                styleableRes = R.styleable.CodeEditorView_ke_editor_backgroundColor,
                attr = intArrayOf(R.attr.ke_editor_backgroundColor,
                        android.R.attr.windowBackground))
        codeEditText.setBackgroundColor(editTextBackgroundColor)

        val maxRealZoom = a.getFloat(R.styleable.CodeEditorView_ke_editor_maxZoom, DEFAULT_MAX_ZOOM)
        setMaxZoom(maxRealZoom, ZoomApi.TYPE_REAL_ZOOM)

        a.recycle()
    }

    private fun inflateViews(inflater: LayoutInflater) {
        inflater.inflate(R.layout.view_code_editor__inner_layout, this)

        codeEditText = findViewById(R.id.cev_editor_codeEditText)
        codeEditText.setViewBackgroundWithoutResettingPadding(null)
        codeEditText.post {
            codeEditText.setSelection(0)
        }
        codeEditText.selectionChangedListener = this

        codeTextView = findViewById(R.id.cev_editor_codeTextView)
        codeTextView.setViewBackgroundWithoutResettingPadding(null)
        codeTextView.selectionChangedListener = this
    }

    private var firstInit = true

    private fun setListeners() {
        addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (firstInit) {
                firstInit = false

                setMinimumDimensions()
            }
        }
    }

    /**
     * Applies minimum dimensions for the [CodeEditText] and [CodeTextView] so that they always
     * fill up the parent.
     */
    private fun setMinimumDimensions() {
        val containerWidth = width - (paddingLeft + paddingRight)
        val containerHeight = height - (paddingTop + paddingBottom)

        val codeEditTextLayoutParams = (codeEditText.layoutParams as MarginLayoutParams)
        val minimumWidth = containerWidth + (codeEditTextLayoutParams.leftMargin + codeEditTextLayoutParams.rightMargin)
        val minimumHeight = containerHeight - (codeEditTextLayoutParams.topMargin + codeEditTextLayoutParams.bottomMargin)

        codeEditText.minWidth = minimumWidth
        codeTextView.minWidth = minimumWidth

        codeEditText.minHeight = minimumHeight
        codeTextView.minHeight = minimumHeight
    }

    /**
     * @return the current count of lines of code in the editor.
     */
    fun getLineCount(): Long {
        val currentText = codeEditText.text
        return if (currentText != null) {
            currentText.count { it == '\n' } + 1L
        } else {
            0L
        }
    }

    /**
     * Called when the selection changes.
     * Override this if you are interested in such events.
     */
    @CallSuper
    override fun onSelectionChanged(start: Int, end: Int, hasSelection: Boolean) {
        selectionChangedListener?.onSelectionChanged(start, end, hasSelection)
    }

    companion object {
        const val TAG = "CodeEditorView"

        const val DEFAULT_MAX_ZOOM = 10F
    }

}
