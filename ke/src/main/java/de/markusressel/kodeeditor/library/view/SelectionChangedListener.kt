package de.markusressel.kodeeditor.library.view

/**
 * Interface for a listener of selection changes
 */
interface SelectionChangedListener {

    /**
     * Called when the selection changes
     *
     * @param start selection start index
     * @param end selection end index
     * @param hasSelection true when a range is selected (start != end)
     */
    fun onSelectionChanged(start: Int, end: Int, hasSelection: Boolean)

}