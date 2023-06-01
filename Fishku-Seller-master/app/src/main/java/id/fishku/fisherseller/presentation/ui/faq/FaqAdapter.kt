package id.fishku.fisherseller.presentation.ui.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import id.fishku.fisherseller.R

class FaqAdapter(private val faqlist:ArrayList<DataFaq> ): RecyclerView.Adapter<FaqAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_faq,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FaqAdapter.ViewHolder, position: Int) {
        val currentList = faqlist[position]
        holder.question.text = currentList.question
        holder.answer.text = currentList.answer

        val isVisible:Boolean = currentList.visibility
        holder.constrainLayout.visibility = if (isVisible) View.VISIBLE else View.GONE

        holder.question.setOnClickListener{
            currentList.visibility = !currentList.visibility
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return faqlist.size
    }

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

        val question: TextView = itemView.findViewById(R.id.tv_question_faq)
        val answer:TextView = itemView.findViewById(R.id.tv_answer_faq)
        val constrainLayout:ConstraintLayout = itemView.findViewById(R.id.expanded_layout)
    }
}