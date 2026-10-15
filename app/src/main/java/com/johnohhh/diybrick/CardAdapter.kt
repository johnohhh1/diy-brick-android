package com.johnohhh.diybrick

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.johnohhh.diybrick.databinding.ItemCardBinding

class CardAdapter(
    private val cards: MutableList<Card>,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    inner class CardViewHolder(val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.binding.tvCardUid.text = card.uid.uppercase()
        holder.binding.tvCardAction.text = card.action.label
        holder.binding.btnDelete.setOnClickListener {
            onDelete(card.uid)
        }
    }

    override fun getItemCount(): Int = cards.size

    fun updateData(newData: List<Card>) {
        cards.clear()
        cards.addAll(newData)
        notifyDataSetChanged()
    }
}
