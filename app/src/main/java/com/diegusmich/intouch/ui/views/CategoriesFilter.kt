package com.diegusmich.intouch.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Category
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CategoriesFilter(ctx: Context, attrs: AttributeSet) : ChipGroup(ctx, attrs) {

    private var _categories = mutableListOf<Category>()
    private val inflater = LayoutInflater.from(ctx)

    init {
        this.chipSpacingHorizontal = 15
        this.chipSpacingVertical = 10
    }

    fun checkedFilters(): List<String> {
        return checkedChipIds.map { _categories[it].id.toString() }
    }

    fun applyCheckedAll(checked: Boolean) {
        if (checked) {
            for (i in 0 until (childCount + 1))
                (getChildAt(i) as Chip).isChecked = true
        } else
            clearCheck()
    }

    private fun attachChip(idChip: Int, catName: String, checked: Boolean = false) {
        val chip = (inflater.inflate(R.layout.category_chip, this, false) as Chip).apply {
            id = idChip
            text = catName
            isCheckable = true
            isChecked = checked
        }
        this.addView(chip)
    }

    fun onCheckedChange(listener: (CategoriesFilter) -> Unit) {
        setOnCheckedStateChangeListener { _, _ ->
            listener(this)
        }
    }

    fun bindData(dataFilters: List<Category>, enableChecked: (Category) -> Boolean = { false }) =
        CoroutineScope(Dispatchers.Main).launch {
            delay(400)
            dataFilters.forEachIndexed { index, category ->
                _categories.add(index, category)
                this@CategoriesFilter.attachChip(
                    index,
                    category.name!!,
                    enableChecked(category)
                )
            }
        }
}