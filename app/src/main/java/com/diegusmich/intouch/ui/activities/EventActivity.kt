package com.diegusmich.intouch.ui.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Event
import com.diegusmich.intouch.databinding.ActivityEventBinding
import com.diegusmich.intouch.service.CloudImageService
import com.diegusmich.intouch.ui.viewmodels.EventViewModel
import com.diegusmich.intouch.utils.TimeUtil
import com.google.firebase.firestore.GeoPoint
import org.ocpsoft.prettytime.PrettyTime
import java.util.Date
import kotlin.math.abs


class EventActivity : AppCompatActivity() {

    private var _binding: ActivityEventBinding? = null
    private val binding get() = _binding!!

    private var eventIdArg: String? = null

    private val viewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventIdArg = intent.extras?.getString(EVENT_ARG)

        binding.swipeRefreshLayout.setProgressViewOffset(true, 50, 250)

        binding.collapsingAppBarLayout.addOnOffsetChangedListener { appBarLayout, offset ->
            if (abs(offset) >= appBarLayout.totalScrollRange - 50)
                binding.collapsingMaterialToolbar.setNavigationIconTint(Color.BLACK)
            else
                binding.collapsingMaterialToolbar.setNavigationIconTint(Color.WHITE)

            binding.swipeRefreshLayout.isEnabled = (offset == 0)
        }

        binding.collapsingMaterialToolbar.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        binding.eventStartAt.setOnClickListener {
            viewModel.event.value?.let {
                addEventToCalendar(it)
            }
        }

        binding.eventLocation.setOnClickListener {
            viewModel.event.value?.let {
                openLocationOnMaps(it)
            }
        }

        binding.eventUserInfo.setOnClickListener {
            viewModel.event.value?.let {
                startActivity(Intent(this, UserActivity::class.java).apply {
                    putExtra(UserActivity.USER_ARG, it.userInfo.id)
                })
            }
        }


        binding.eventNestedScrollView.setOnScrollChangeListener{ _, _, _, _, _ ->
            binding.swipeRefreshLayout.clearAnimation()
        }

        binding.collapsingMaterialToolbar.inflateMenu(R.menu.event_menu)
        observeData()
        viewModel.onLoadEvent(eventIdArg)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.event_menu, menu)
        return true
    }

    private fun observeData(){
        viewModel.LOADING.observe(this) {
            binding.swipeRefreshLayout.isRefreshingDelayed(this, it)
        }

        viewModel.event.observe(this) {
            if (it == null)
                return@observe

            binding.eventTitle.text = it.name
            binding.eventDescription.text = it.description
            binding.eventLocation.text =
                getString(R.string.event_location_formatted, it.address, it.city)
            binding.eventCoverImageView.load(CloudImageService.EVENTS.imageRef(it.cover))
            binding.eventUserInfo.setUserInfo(it.userInfo)
            binding.eventCategoriesTag.text = it.categoryInfo.name
            binding.eventCategoriesTag.visibility = View.VISIBLE
            binding.eventButtonJoin.isEnabled = it.available > 0

            showIsRestrictedTag(it.restricted)
            showAvailabilityWarning(it.available)
            putEventDateInfo(it.startAt, it.endAt)

        }

        viewModel.ERROR.observe(this) {
            viewModel.ERROR.observe(this) {
                if (it != null)
                    Toast.makeText(this, getString(it), Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    private fun showIsRestrictedTag(restricted: Boolean) {
        if (restricted) {
            binding.eventRestrictedTag.visibility = View.VISIBLE
            binding.eventOpenTag.visibility = View.GONE
        } else {
            binding.eventRestrictedTag.visibility = View.GONE
            binding.eventOpenTag.visibility = View.VISIBLE
        }
    }

    private fun putEventDateInfo(startAt: Date, endAt: Date?) {
        binding.eventStartAt.text = TimeUtil.toLocaleString(startAt, TimeUtil.DAY_OF_YEAR_HH_MM)
        if (endAt != null) {
            binding.eventEndAt.text = TimeUtil.toLocaleString(endAt, TimeUtil.DAY_OF_YEAR_HH_MM)
            binding.eventEndAt.visibility = View.VISIBLE
        } else
            binding.eventEndAt.visibility = View.GONE
    }

    private fun showAvailabilityWarning(availability: Int) {
        if (availability < 15) {
            val drawableId: Drawable?

            if (availability == 0) {
                drawableId =
                    AppCompatResources.getDrawable(this, R.drawable.baseline_do_not_disturb_on_24)
                binding.eventAvailabilityTextWarning.text = getString(R.string.event_not_available)
            } else {
                drawableId = AppCompatResources.getDrawable(this, R.drawable.baseline_warning_24)
                binding.eventAvailabilityTextWarning.text = resources.getQuantityString(
                    R.plurals.event_availability_warning,
                    availability,
                    availability
                )
            }
            binding.eventAvailabilityTextWarning.setCompoundDrawablesWithIntrinsicBounds(
                drawableId,
                null,
                null,
                null
            )
            binding.eventAvailabilityTextWarning.visibility = View.VISIBLE
        } else
            binding.eventAvailabilityTextWarning.visibility = View.GONE
    }

    private fun addEventToCalendar(event: Event.Full) {
        val calendarIntent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, event.name)
            putExtra(CalendarContract.Events.EVENT_LOCATION, "${event.address}, ${event.city}")
            putExtra(CalendarContract.Events.DESCRIPTION, event.description)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.startAt.time)
            event.endAt?.let {
                this@apply.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, it.time)
            }
        }

        startActivity(calendarIntent)
    }

    private fun openLocationOnMaps(event : Event.Full) {
        val mapsIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("geo:${event.geo.latitude},${event.geo.longitude}?q=${event.address}, ${event.city}")
        }

        if (mapsIntent.resolveActivity(packageManager) != null) {
            startActivity(mapsIntent)
        } else {
            val mapsUrl = "https://www.google.com/maps?q=" + Uri.parse("${event.address}, ${event.city}")
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl)))
        }
    }

    companion object {
        const val EVENT_ARG = "eventId"
    }
}