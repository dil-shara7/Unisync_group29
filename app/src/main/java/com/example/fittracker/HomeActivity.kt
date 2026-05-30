package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.fittracker.databinding.ActivityHomeBinding

class HomeActivity : UniSyncActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyFeatureCardCopy()
        wireFeatureCardClicks()
        binding.homeAvatar.setOnClickListener { goTo(ProfileActivity::class.java) }
    }

    private fun applyFeatureCardCopy() {
        bindCard(
            binding.cardSchedule,
            R.string.home_card_schedule_title,
            R.string.home_card_schedule_sub,
            R.drawable.ic_card_schedule,
        )
        bindCard(
            binding.cardNotes,
            R.string.home_card_notes_title,
            R.string.home_card_notes_sub,
            R.drawable.ic_card_notes,
        )
        bindCard(
            binding.cardMarketplace,
            R.string.home_card_marketplace_title,
            R.string.home_card_marketplace_sub,
            R.drawable.ic_card_marketplace,
        )
        bindCard(
            binding.cardLostFound,
            R.string.home_card_lostfound_title,
            R.string.home_card_lostfound_sub,
            R.drawable.ic_card_lost_found,
        )
    }

    private fun bindCard(card: View, titleRes: Int, subtitleRes: Int, iconRes: Int) {
        card.findViewById<TextView>(R.id.feature_card_title).setText(titleRes)
        card.findViewById<TextView>(R.id.feature_card_subtitle).setText(subtitleRes)
        card.findViewById<ImageView>(R.id.feature_card_icon).setImageResource(iconRes)
    }

    private fun wireFeatureCardClicks() {
        binding.cardSchedule.setOnClickListener { goTo(ScheduleManagerActivity::class.java) }
        binding.cardNotes.setOnClickListener { goTo(NotesHubActivity::class.java) }
        binding.cardMarketplace.setOnClickListener { goTo(MarketplaceActivity::class.java) }
        binding.cardLostFound.setOnClickListener { goTo(LostFoundActivity::class.java) }
        binding.cardNextClass.setOnClickListener { goTo(ScheduleManagerActivity::class.java) }
        binding.cardHotDeal.setOnClickListener { goTo(MarketplaceActivity::class.java) }
    }

    private fun goTo(cls: Class<out AppCompatActivity>) {
        startActivity(Intent(this, cls))
    }
}
