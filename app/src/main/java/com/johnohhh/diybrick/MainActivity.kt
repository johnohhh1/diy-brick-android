package com.johnohhh.diybrick

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnohhh.diybrick.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var actionStore: ActionStore
    private lateinit var cardAdapter: CardAdapter

    private var pendingCardUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionStore = ActionStore(this)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setupRecyclerView()
        setupListeners()
        handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        if (::nfcAdapter.isInitialized) {
            if (!nfcAdapter.isEnabled) {
                showNfcDisabledDialog()
                return
            }
            val pendingIntent = PendingIntent.getActivity(
                this, 0,
                Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE
            )
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null)
        }
        refreshCardList()
    }

    override fun onPause() {
        super.onPause()
        if (::nfcAdapter.isInitialized) {
            nfcAdapter.disableForegroundDispatch(this)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action in listOf(
                NfcAdapter.ACTION_NDEF_DISCOVERED,
                NfcAdapter.ACTION_TECH_DISCOVERED,
                NfcAdapter.ACTION_TAG_DISCOVERED
            )
        ) {
            @Suppress("DEPRECATION")
        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            tag?.let { processTag(it) }
        }
    }

    private fun processTag(tag: Tag) {
        val uid = tag.id.toHex()
        val techList = tag.techList.joinToString("\n") { it.substringAfterLast('.') }

        binding.tvLastScan.text = getString(
            R.string.last_scan_format,
            uid.uppercase(Locale.ROOT),
            techList
        )

        val action = actionStore.getAction(uid)
        if (action != null) {
            binding.tvStatus.text = getString(R.string.known_card_exec, action.label)
            ActionExecutor.perform(this, action)
        } else {
            binding.tvStatus.setText(R.string.unknown_card)
            pendingCardUid = uid
            showRegisterDialog(uid)
        }
    }

    private fun showRegisterDialog(uid: String) {
        val actions = ActionType.entries.map { it.label }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Register Card $uid")
            .setItems(actions) { _, which ->
                val selected = ActionType.entries[which]
                actionStore.saveAction(uid, CardAction(selected.name, selected.label))
                refreshCardList()
                binding.tvStatus.text = getString(R.string.card_registered, selected.label)
                pendingCardUid = null
            }
            .setNegativeButton("Cancel") { _, _ -> pendingCardUid = null }
            .setOnCancelListener { pendingCardUid = null }
            .show()
    }

    private fun setupRecyclerView() {
        cardAdapter = CardAdapter(mutableListOf()) { uid ->
            actionStore.deleteAction(uid)
            refreshCardList()
        }
        binding.rvCards.layoutManager = LinearLayoutManager(this)
        binding.rvCards.adapter = cardAdapter
    }

    private fun setupListeners() {
        binding.btnAddTestCard.setOnClickListener {
            // For testing without a physical card
            showRegisterDialog("TEST123456")
        }
        binding.btnClearAll.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Clear all cards?")
                .setPositiveButton("Yes") { _, _ ->
                    actionStore.clearAll()
                    refreshCardList()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun refreshCardList() {
        val cards = actionStore.getAllActions().map { Card(it.key, it.value) }
        cardAdapter.updateData(cards)
    }

    private fun showNfcDisabledDialog() {
        AlertDialog.Builder(this)
            .setTitle("NFC Disabled")
            .setMessage("Enable NFC in settings to use DIY Brick.")
            .setPositiveButton("Open Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
            }
            .setCancelable(false)
            .show()
    }

    private fun ByteArray.toHex(): String =
        joinToString("") { "%02x".format(it) }
}
