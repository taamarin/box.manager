package de.markusressel.kodeeditor.library.view

import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.Layout
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.otaliastudios.zoom.*
import de.markusressel.kodeeditor.library.R
import de.markusressel.kodeeditor.library.extensions.createSnapshot
import de.markusressel.kodeeditor.library.extensions.dpToPx
import de.markusressel.kodeeditor.library.extensions.getColor
import de.markusressel.kodehighlighter.core.LanguageRuleBook
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.widget.textChanges
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


/**
 * Code Editor that allows pinch-to-zoom, line numbers etc.
 */
open class CodeEditorLayout
@JvmOverloads
constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * The ZoomLayout containing the [CodeEditText].
     */
    lateinit var codeEditorView: CodeEditorView

    /**
     * Indicates whether the engine has fully initialized
     **/
    private val isEngineInitialized: Boolean
        get() = codeEditorView.engine.computeVerticalScrollRange() > 0
                && codeEditorView.engine.computeHorizontalScrollRange() > 0

    /**
     * The view displaying line numbers.
     * This is also a [ZoomLayout] so the line numbers can be scaled and panned according to the
     * code editor's zoom and pan.
     */
    internal lateinit var lineNumberZoomLayout: ZoomLayout

    /**
     * The [TextView] with the actual line numbers as a multiline text.
     */
    internal lateinit var lineNumberTextView: TextView

    /**
     * Set how the lines number are generated based on total
     * number of lines
     */
    var lineNumberGenerator: (Long) -> List<String> = { lines ->
        (1..lines).map { "$it$LINE_NUMBER_SUFFIX" }
    }

    /**
     * The container layout for the minimap.
     */
    internal lateinit var minimapContainerLayout: ViewGroup

    /**
     * The [ZoomLayout] used for the minimap.
     */
    internal lateinit var minimapZoomLayout: ZoomImageView

    /**
     * The rectangle on the minimap indicating the currently visible area.
     */
    internal lateinit var minimapIndicator: View

    /**
     * The (optional) divider between [lineNumberZoomLayout] and [codeEditorView]
     */
    internal lateinit var dividerView: View

    /**
     * Controls whether to follow cursor movements or not.
     */
    var isMoveWithCursorEnabled = true
    private var internalMoveWithCursorEnabled = false

    /**
     * The currently set text
     */
    var text: String
        set(value) {
            codeEditorView.text = value
        }
        get() = codeEditorView.text

    /**
     * The currently active syntax highlighter (if any)
     */
    var languageRuleBook: LanguageRuleBook?
        get() = codeEditorView.languageRuleBook
        set(value) {
            codeEditorView.languageRuleBook = value
        }

    /**
     * Set the text in the editor
     *
     * @param text string resource of the new text
     */
    fun setText(@StringRes text: Int) {
        this.text = context.getString(text)
    }

    /**
     * Controls wheter the text is editable or not.
     */
    var editable: Boolean
        set(value) {
            codeEditorView.editable = value
            updateMinimap()
        }
        get() = codeEditorView.editable

    /**
     * Controls whether the divider between line numbers and code editor is visible.
     */
    var showDivider: Boolean
        set(value) {
            dividerView.visibility = if (value) View.VISIBLE else View.GONE
        }
        get() = dividerView.visibility == View.VISIBLE

    /**
     * Indicates if the minimap should be shown or not.
     */
    var showMinimap = DEFAULT_SHOW_MINIMAP
        set(value) {
            field = value
            minimapContainerLayout.visibility = if (value) View.VISIBLE else View.GONE
            if (value) updateMinimap()
        }

    /**
     * The width & height limit of the minimap
     */
    var minimapMaxDimension = DEFAULT_MINIMAP_MAX_DIMENSION_DP.dpToPx(context)
        set(value) {
            field = value
            updateMinimap()
        }

    /**
     * The width of the border around the minimap
     */
    var minimapBorderWidth: Number = DEFAULT_MINIMAP_BORDER_SIZE_DP.dpToPx(context)
        set(value) {
            field = value
            updateMinimapBorder()
        }

    /**
     * The color of the minimap border
     */
    @ColorInt
    var minimapBorderColor: Int = 0
        set(value) {
            field = value

            updateMinimapBorderColor(minimapBorderWidth.toFloat().roundToInt())
        }

    /**
     * The color of the minimap indicator
     */
    @ColorInt
    var minimapIndicatorColor: Int = 0
        set(value) {
            field = value

            minimapIndicator.background = GradientDrawable().apply {
                setStroke(2.dpToPx(context).roundToInt(), field)
            }
        }

    /**
     * The positioning gravity of the minimap
     */
    var minimapGravity: Int = Gravity.TOP or Gravity.END
        set(value) {
            field = value
            (minimapContainerLayout.layoutParams as LayoutParams).gravity = field
            minimapContainerLayout.requestLayout()
        }

    private var currentDrawnLineCount = -1L

    /**
     * Text size in SP
     */
    private var textSizeSp: Float = DEFAULT_TEXT_SIZE_SP

    /**
     * Text size in PX
     */
    private var textSizePx: Float
        get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSizeSp, context.resources.displayMetrics)
        set(value) {
            textSizeSp = value / resources.displayMetrics.scaledDensity
        }

    @ColorInt
    private var editorBackgroundColor: Int = 0

    init {
        inflateViews(LayoutInflater.from(context))
        readParameters(attrs, defStyleAttr)
        setListeners()
    }

    private fun inflateViews(layoutInflater: LayoutInflater) {
        layoutInflater.inflate(R.layout.layout_code_editor__main_layout, this)

        lineNumberZoomLayout = findViewById(R.id.cel_linenumbers_zoomLayout)
        lineNumberTextView = findViewById(R.id.cel_linenumbers_textview)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            lineNumberTextView.hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
        }

        dividerView = findViewById(R.id.cel_divider)
        codeEditorView = findViewById(R.id.cel_codeEditorView)

        minimapContainerLayout = findViewById(R.id.cel_minimap_container)
        minimapZoomLayout = minimapContainerLayout.findViewById(R.id.cel_minimap)
        minimapIndicator = minimapContainerLayout.findViewById(R.id.cel_minimap_indicator)
    }

    private fun readParameters(attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CodeEditorLayout, defStyleAttr, 0)

        val lineNumberTextColor = a.getColor(context,
                defaultColor = Color.BLACK,
                styleableRes = R.styleable.CodeEditorLayout_ke_lineNumbers_textColor,
                attr = intArrayOf(R.attr.ke_lineNumbers_textColor,
                        android.R.attr.textColorPrimary))
        lineNumberTextView.setTextColor(lineNumberTextColor)

        val lineNumberBackgroundColor = a.getColor(context,
                defaultColor = Color.WHITE,
                styleableRes = R.styleable.CodeEditorLayout_ke_lineNumbers_backgroundColor,
                attr = intArrayOf(R.attr.ke_lineNumbers_backgroundColor,
                        android.R.attr.windowBackground))
        lineNumberZoomLayout.setBackgroundColor(lineNumberBackgroundColor)


        showDivider = a.getBoolean(R.styleable.CodeEditorLayout_ke_divider_enabled, DEFAULT_SHOW_DIVIDER)

        val dividerColor = a.getColor(context,
                defaultColor = Color.BLACK,
                styleableRes = R.styleable.CodeEditorLayout_ke_divider_color,
                attr = intArrayOf(R.attr.ke_divider_color,
                        android.R.attr.textColorPrimary))
        dividerView.setBackgroundColor(dividerColor)

        editorBackgroundColor = a.getColor(context,
                defaultColor = Color.WHITE,
                styleableRes = R.styleable.CodeEditorLayout_ke_editor_backgroundColor,
                attr = intArrayOf(R.attr.ke_editor_backgroundColor,
                        android.R.attr.windowBackground))
        codeEditorView.setBackgroundColor(editorBackgroundColor)
        isMoveWithCursorEnabled = a.getBoolean(R.styleable.CodeEditorLayout_ke_editor_followCursor, true)

        val codeEditorMaxZoom = a.getFloat(R.styleable.CodeEditorLayout_ke_editor_maxZoom, CodeEditorView.DEFAULT_MAX_ZOOM)
        lineNumberZoomLayout.setMaxZoom(codeEditorMaxZoom, ZoomApi.TYPE_REAL_ZOOM)
        codeEditorView.setMaxZoom(codeEditorMaxZoom, ZoomApi.TYPE_REAL_ZOOM)

        showMinimap = a.getBoolean(R.styleable.CodeEditorLayout_ke_minimap_enabled, DEFAULT_SHOW_MINIMAP)
        minimapMaxDimension = a.getDimensionPixelSize(R.styleable.CodeEditorLayout_ke_minimap_maxDimension, DEFAULT_MINIMAP_MAX_DIMENSION_DP).toFloat()
        minimapBorderColor = a.getColor(context,
                defaultColor = Color.BLACK,
                styleableRes = R.styleable.CodeEditorLayout_ke_minimap_borderColor,
                attr = intArrayOf(R.attr.ke_minimap_borderColor))

        minimapIndicatorColor = a.getColor(context,
                defaultColor = Color.RED,
                styleableRes = R.styleable.CodeEditorLayout_ke_minimap_indicatorColor,
                attr = intArrayOf(R.attr.ke_minimap_indicatorColor))

        a.recycle()
    }

    private fun setListeners() {
        // add listener to code editor to keep linenumbers position and zoom in sync
        codeEditorView.engine.addListener(object : ZoomEngine.Listener {
            override fun onIdle(engine: ZoomEngine) {}

            override fun onUpdate(engine: ZoomEngine, matrix: Matrix) {
                if (!isEngineInitialized) return

                val editorRect = calculateVisibleCodeArea()
                updateLineNumbers(editorRect, updateLineCount = false)
                updateMinimapIndicator(editorRect)
            }
        })

        codeEditorView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            if (!isEngineInitialized) return@addOnLayoutChangeListener
            // linenumbers always have to be the exact same size as the content
            lineNumberTextView.height = codeEditorView.engine.computeVerticalScrollRange()
            updateLineNumbers()
            updateMinimap()
        }

        @Suppress("ClickableViewAccessibility")
        minimapZoomLayout.setOnTouchListener { v, event ->
            if (!showMinimap) return@setOnTouchListener false

            when (event.action) {
                MotionEvent.ACTION_DOWN or MotionEvent.ACTION_MOVE -> {
                    val viewX = event.x - v.left
                    val viewY = event.y - v.top
                    val offsetX = minimapIndicator.width / 2F
                    val offsetY = minimapIndicator.height / 2F
                    val percentageX = (viewX - offsetX) / v.width
                    val percentageY = (viewY - offsetY) / v.height

                    moveEditorToPercentage(percentageX, percentageY)
                    true
                }
                else -> false
            }
        }

        setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_MOVE -> {
                    internalMoveWithCursorEnabled = false
                }
            }
            false
        }

        codeEditorView.codeEditText.setOnClickListener {
            internalMoveWithCursorEnabled = true
        }

        codeEditorView.selectionChangedListener = object : SelectionChangedListener {
            override fun onSelectionChanged(start: Int, end: Int, hasSelection: Boolean) {
                if (!isMoveWithCursorEnabled) return

                internalMoveWithCursorEnabled = true
                try {
                    moveToCursorIfNecessary()
                } catch (e: Throwable) {
                    Log.e(CodeEditorView.TAG, "Error moving screen with cursor", e)
                }
            }
        }

        codeEditorView.codeEditText.textChanges()
                .debounce(50)
                .onEach {
                    try {
                        updateLineNumbers()
                    } catch (e: Throwable) {
                        Log.e(CodeEditorView.TAG, "Error updating line numbers", e)
                    }
                }
                .catch {
                    Log.e(CodeEditorView.TAG, "Unrecoverable error while updating line numbers", it)
                }.launchIn(CoroutineScope(Job() + Dispatchers.Main))
    }

    /**
     * Helper function to move the editor content to a percentage based position
     *
     * @param percentageX x-axis percentage
     * @param percentageY y-axis percentage
     */
    private fun moveEditorToPercentage(percentageX: Float, percentageY: Float) {
        val targetX = -codeEditorView.engine.computeHorizontalScrollRange() / codeEditorView.engine.zoom * percentageX
        val targetY = -codeEditorView.engine.computeVerticalScrollRange() / codeEditorView.engine.zoom * percentageY

        codeEditorView.moveTo(codeEditorView.zoom, targetX, targetY, false)
    }

    /**
     * Updates the minimap
     */
    private fun updateMinimap() {
        if (!showMinimap) return
        if (!isEngineInitialized) return

        updateMinimapImage()
        updateMinimapIndicator()
    }

    /**
     * Renders the current text and applies it to the minimap
     */
    private fun updateMinimapImage() {
        if (!showMinimap) return

        val targetView: View = if (editable) codeEditorView.codeEditText else codeEditorView.codeTextView
        targetView.apply {
            post {
                createSnapshot(
                        dimensionLimit = minimapMaxDimension,
                        backgroundColor = editorBackgroundColor
                )?.let {
                    minimapZoomLayout.setImageBitmap(it)
                }
            }
        }
    }

    private fun updateMinimapBorder() {
        if (!showMinimap) return

        val valueAsInt = minimapBorderWidth.toFloat().roundToInt()
        minimapContainerLayout.post {
            minimapContainerLayout.setPadding(valueAsInt, valueAsInt, valueAsInt, valueAsInt)
            updateMinimapBorderColor(valueAsInt)
        }
    }

    private fun updateMinimapBorderColor(width: Int) {
        minimapContainerLayout.post {
            minimapContainerLayout.background = GradientDrawable().apply {
                setStroke(width, minimapBorderColor)
            }
        }
    }

    /**
     * Updates the minimap indicator position and size
     *
     * @param editorRect the dimensions of the [codeEditorView]
     */
    private fun updateMinimapIndicator(editorRect: Rect = calculateVisibleCodeArea()) {
        if (!showMinimap) return

        val engine = codeEditorView.engine

        // update minimap indicator position and size
        (minimapIndicator.layoutParams as MarginLayoutParams).apply {
            topMargin = ((minimapZoomLayout.height *
                    (engine.computeVerticalScrollOffset().toFloat() / engine.computeVerticalScrollRange()))).roundToInt()
            leftMargin = ((minimapZoomLayout.width *
                    (engine.computeHorizontalScrollOffset().toFloat() / engine.computeHorizontalScrollRange()))).roundToInt()

            width = (minimapZoomLayout.width * (editorRect.width().toFloat() / engine.computeHorizontalScrollRange())).roundToInt()
            height = (minimapZoomLayout.height * (editorRect.height().toFloat() / engine.computeVerticalScrollRange())).roundToInt()
            minimapIndicator.layoutParams = this
        }
    }

    /**
     * Synchronizes zoom & position of [lineNumberTextView] with the [codeEditorView],
     * and updates the line number text.
     *
     * @param updateLineCount true updates line numbers (this is quite expensive), false doesn't
     */
    private fun updateLineNumbers(editorRect: Rect = calculateVisibleCodeArea(),
                                  updateLineCount: Boolean = true) {
        if (updateLineCount) {
            updateLineNumberText()
        }

        // adjust width of line numbers based on zoom
        val engine = codeEditorView.engine

        val scaledWidth = lineNumberTextView.width * engine.realZoom
        val maxWidth = editorRect.width() / 3F
        val targetWidth = min(scaledWidth, maxWidth).roundToInt()
        lineNumberZoomLayout.layoutParams.apply {
            width = targetWidth
            lineNumberZoomLayout.layoutParams = this
        }

        // synchronize zoom and vertical pan to match code editor
        lineNumberZoomLayout.moveTo(
                engine.zoom,
                -engine.computeHorizontalScrollRange().toFloat(),
                engine.panY,
                false)
    }

    /**
     * Updates the text of the [lineNumberTextView] to match the line count in the [codeEditorView]
     *
     * @param lineCount the amount of lines to show
     */
    private fun updateLineNumberText(lineCount: Long = codeEditorView.getLineCount()) {
        val linesToDraw = max(MIN_LINES_DRAWN, lineCount)
        if (linesToDraw == currentDrawnLineCount) {
            return
        }

        currentDrawnLineCount = linesToDraw
        lineNumberTextView.text = createLineNumberText(linesToDraw)
    }

    /**
     * Creates the text that is used on [lineNumberTextView] to show line numbers.
     *
     * @param lines the amount of lines
     * @return the text to show for the given amount of lines
     */
    private fun createLineNumberText(lines: Long): String {
        return lineNumberGenerator(lines).joinToString(separator = "\n")
    }

    /**
     * Moves the screen so that the cursor is visible.
     */
    private fun moveToCursorIfNecessary() {
        val cursorPosition = getCursorScreenPosition() ?: return
        val targetArea = calculateVisibleCodeArea()
        val padding = (32 * codeEditorView.realZoom).toInt()
        targetArea.inset(padding, padding)
        targetArea.offset(0, -padding)

        if (!targetArea.contains(cursorPosition.x.roundToInt(), cursorPosition.y.roundToInt())) {
            val targetLocation = calculateTargetPoint(cursorPosition, targetArea)
            codeEditorView.moveTo(codeEditorView.zoom, targetLocation.x, targetLocation.y, false)
        }
    }

    /**
     * Calculates the new top-left point to show the cursor on screen.
     *
     * @param cursorPosition the position of the cursor
     * @param targetArea the target area the cursor position should be in
     */
    private fun calculateTargetPoint(cursorPosition: PointF, targetArea: Rect): AbsolutePoint {
        val newX = when {
            cursorPosition.x < targetArea.left -> {
                codeEditorView.panX + (targetArea.left - cursorPosition.x) / codeEditorView.realZoom
            }
            cursorPosition.x > targetArea.right -> {
                codeEditorView.panX + (targetArea.right - cursorPosition.x) / codeEditorView.realZoom
            }
            else -> codeEditorView.panX
        }

        val newY = when {
            cursorPosition.y < targetArea.top -> {
                codeEditorView.panY + (targetArea.top - cursorPosition.y) / codeEditorView.realZoom
            }
            cursorPosition.y > targetArea.bottom -> {
                codeEditorView.panY + (targetArea.bottom - cursorPosition.y) / codeEditorView.realZoom
            }
            else -> codeEditorView.panY
        }

        return AbsolutePoint(newX, newY)
    }

    /**
     * @return the currently visible area of the [codeEditorView]
     */
    private fun calculateVisibleCodeArea() = Rect().apply {
        codeEditorView.getLocalVisibleRect(this)
    }

    /**
     * @return the position of the cursor in relation to the [codeEditorView] content.
     */
    private fun getCursorScreenPosition(): PointF? {
        val pos = codeEditorView.codeEditText.selectionStart
        val layout = codeEditorView.codeEditText.layout ?: return null

        val line = layout.getLineForOffset(pos)
        val baseline = layout.getLineBaseline(line)
        val ascent = layout.getLineAscent(line)
        val x = layout.getPrimaryHorizontal(pos)
        val y = (baseline + ascent).toFloat()

        return PointF((x + codeEditorView.panX) * codeEditorView.realZoom,
                (y + codeEditorView.panY) * codeEditorView.realZoom)
    }

    companion object {
        const val MIN_LINES_DRAWN = 1L
        const val DEFAULT_TEXT_SIZE_SP = 12F
        const val LINE_NUMBER_SUFFIX = ":"

        const val DEFAULT_SHOW_DIVIDER = true
        const val DEFAULT_SHOW_MINIMAP = true
        const val DEFAULT_MINIMAP_MAX_DIMENSION_DP = 150
        const val DEFAULT_MINIMAP_BORDER_SIZE_DP = 2
    }

}
