package eniso.ia.lockify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_family_member.view.*

class FamilyMembersAdapter(
    var familyMembers:List<FamilyMember>):RecyclerView.Adapter<FamilyMembersAdapter.FamilyMembersViewHolder>(){

    inner class FamilyMembersViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyMembersViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_family_member, parent,false)

        return FamilyMembersViewHolder(view)



    }

    override fun onBindViewHolder(holder: FamilyMembersViewHolder, position: Int) {
        holder.itemView.apply {
            tvName.text=familyMembers[position].Name
        }
    }

    override fun getItemCount(): Int {
        return familyMembers.size
    }
}