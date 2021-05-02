package eniso.ia.lockify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintHelper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_family_member.view.*
import kotlinx.coroutines.CoroutineScope

class FamilyMembersAdapter(
    var familyMembers:List<FamilyMember>, val click: RecycleViewOnItemClick
):RecyclerView.Adapter<FamilyMembersAdapter.FamilyMembersViewHolder>(){

    inner class FamilyMembersViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var root=itemView.findViewById<ConstraintLayout>(R.id.root)


        // add onclick listener
        /*init {
            itemView.setOnClickListener {
                val position :Int=adapterPosition
                Toast.makeText(itemView.context,"You clicked on item ${position+1}",Toast.LENGTH_LONG).show()
            }
        }*/

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyMembersViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_family_member, parent,false)

        return FamilyMembersViewHolder(view)


    }

    override fun onBindViewHolder(holder: FamilyMembersViewHolder, position: Int) {
        holder.itemView.apply {
            tvName.text=familyMembers[position].Name



        }
        holder.itemView.btnDeleteMember.setOnClickListener {
            click.onItemClick(familyMembers[position],position)
        }

    }

    override fun getItemCount(): Int {
        return familyMembers.size
    }
}