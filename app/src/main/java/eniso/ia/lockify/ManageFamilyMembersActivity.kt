package eniso.ia.lockify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_lock_and_unlock.*
import kotlinx.android.synthetic.main.activity_manage_family_members.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ManageFamilyMembersActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    val personCollectionRef= Firebase.firestore.collection("persons")
    val familyMembersList= mutableListOf<FamilyMember>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_family_members)
        auth = FirebaseAuth.getInstance()

        //recyclerview
        val adapter=FamilyMembersAdapter(familyMembersList)
        rvFamilyMembers.adapter= adapter
        rvFamilyMembers.layoutManager= LinearLayoutManager(this@ManageFamilyMembersActivity)
        showAllFamilyMembers()

/*            FamilyMember("Mouhib sahloul",""),FamilyMember("Mouhib sahloul",""),FamilyMember("Mouhib sahloul",""),
            FamilyMember("Mouhib sahloul",""),
            FamilyMember("Mouhib sahloul",""),FamilyMember("Mouhib sahloul",""))
        val adapter=FamilyMembersAdapter(familyMembersList)
        rvFamilyMembers.adapter= adapter
        rvFamilyMembers.layoutManager= LinearLayoutManager(this)
        btnAddFamilyMember.setOnClickListener {
            val name=etFamilyMember.text.toString()
            val familyMember=FamilyMember(name,"")
            familyMembersList.add(familyMember)
            adapter.notifyItemInserted(familyMembersList.size-1)        }*/

        btnAddFamilyMember.setOnClickListener {
            val email=edAddFamilyMember.text.toString()
            findOnePersonNameByEmail(email)
        }
    }


    private fun addNewFamilyMember()= CoroutineScope(Dispatchers.IO).launch{
        try{
            val familyMembersEmails= Firebase.firestore.collection("persons").document(auth.currentUser.uid)

            val newFamilyMember=edAddFamilyMember.text.toString()
            familyMembersEmails.update("familyMembersMails", FieldValue.arrayUnion(newFamilyMember)).await()

            withContext(Dispatchers.Main){
                Toast.makeText(this@ManageFamilyMembersActivity, "Successfully Added new member", Toast.LENGTH_SHORT).show()
                makePersonToFamilyMember(newFamilyMember)
               // showAllFamilyMembers()


            }
        } catch (e: Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@ManageFamilyMembersActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAllFamilyMembers()= CoroutineScope(Dispatchers.IO).launch{

        try {
            val currentUser = FirebaseAuth.getInstance().currentUser.uid
            val querySnapshot = personCollectionRef.get().await()
            var familyMembersEmails: MutableList<String> = mutableListOf()
            for(document in querySnapshot.documents) {
                if(document.id==currentUser) {
                    val person = document.toObject<Person>()
                    if (person != null) {
                        familyMembersEmails=person.familyMembersMails
                        break
                    }
                }
                //sb.append("$person\n")
            }
            withContext(Dispatchers.Main) {
                familyMembersEmails
                findPersonNameByEmail(familyMembersEmails)

               /* //val familyMembersList= mutableListOf<FamilyMember>()
                for( mail in familyMembersEmails)
                {

                    familyMembersList.add(FamilyMember(mail,""))
                }
                val adapter=FamilyMembersAdapter(familyMembersList)
                rvFamilyMembers.adapter= adapter
                rvFamilyMembers.layoutManager= LinearLayoutManager(this@ManageFamilyMembersActivity)
                //tvMails.text = mails.toString()*/
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ManageFamilyMembersActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun makePersonToFamilyMember ( newFamilyMember: String)= CoroutineScope(Dispatchers.IO).launch{
        val personQuery=personCollectionRef
            .whereEqualTo("email",newFamilyMember)
            .get()
            .await()
        if(personQuery.documents.isNotEmpty())
        {
            for(document in personQuery)
            {
                try{
                    personCollectionRef.document(document.id).update("familyMember",true)
                }catch (e: Exception)
                {
                    withContext(Dispatchers.Main)
                    {
                        Toast.makeText(this@ManageFamilyMembersActivity,e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        else
        {
            withContext(Dispatchers.Main)
            {
                Toast.makeText(this@ManageFamilyMembersActivity,"no person matched the query", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun findPersonNameByEmail ( newFamilyMemberEmails: MutableList<String>)= CoroutineScope(Dispatchers.IO).launch{

        val personQuery=personCollectionRef
            .whereIn("email",newFamilyMemberEmails )
            .get()
            .await()
        val v=personQuery.documents.isNotEmpty()
        if(personQuery.documents.isNotEmpty())
        {
            Log.d("lol", personQuery.documents.isNotEmpty().toString())
            for(document in personQuery) {
                try {


                    val person = document.toObject<Person>()
                    withContext(Dispatchers.Main) {
                        familyMembersList.add(
                            FamilyMember(
                                person.firstName + " " + person.lastName,
                                person.email
                            )
                        )
                    }


                } catch (e: Exception) {
                    withContext(Dispatchers.Main)
                    {
                        Toast.makeText(
                            this@ManageFamilyMembersActivity,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }}

        }
        else
        {
            withContext(Dispatchers.Main)
            {
                Toast.makeText(this@ManageFamilyMembersActivity,"no person matched the query", Toast.LENGTH_SHORT).show()
            }
        }
        withContext(Dispatchers.Main)
        {
            val adapter = FamilyMembersAdapter(familyMembersList)
            rvFamilyMembers.adapter = adapter
            rvFamilyMembers.layoutManager =
                LinearLayoutManager(this@ManageFamilyMembersActivity)
        }

    }
    private fun findOnePersonNameByEmail ( newFamilyMemberEmail: String)= CoroutineScope(Dispatchers.IO).launch{

        val personQuery=personCollectionRef
            .whereEqualTo("email",newFamilyMemberEmail )
            .get()
            .await()
        val v=personQuery.documents.isNotEmpty()
        if(personQuery.documents.isNotEmpty())
        {
            Log.d("lol", personQuery.documents.isNotEmpty().toString())
            for(document in personQuery) {
                try {


                    val person = document.toObject<Person>()
                    withContext(Dispatchers.Main) {
                        familyMembersList.add(
                            FamilyMember(
                                person.firstName + " " + person.lastName,
                                person.email
                            )
                        )
                    }


                } catch (e: Exception) {
                    withContext(Dispatchers.Main)
                    {
                        Toast.makeText(
                            this@ManageFamilyMembersActivity,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }}

        }
        else
        {
            withContext(Dispatchers.Main)
            {
                Toast.makeText(this@ManageFamilyMembersActivity,"no person matched the query", Toast.LENGTH_SHORT).show()
            }
        }
        withContext(Dispatchers.Main)
        {
            val adapter = FamilyMembersAdapter(familyMembersList)
            rvFamilyMembers.adapter = adapter
            adapter.notifyItemInserted(familyMembersList.size-1)
            Toast.makeText(this@ManageFamilyMembersActivity,"yeyeyeeye", Toast.LENGTH_SHORT).show()
            addNewFamilyMember()
        }

    }
}