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

class ManageFamilyMembersActivity : AppCompatActivity(),RecycleViewOnItemClick {
    lateinit var auth: FirebaseAuth
    val personCollectionRef = Firebase.firestore.collection("persons")
    val familyMembersList = mutableListOf<FamilyMember>()
    private lateinit var adapter :FamilyMembersAdapter
    //val adapter = FamilyMembersAdapter(familyMembersList,this@ManageFamilyMembersActivity)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_family_members)
        auth = FirebaseAuth.getInstance()

        //recyclerview
        //val adapter = FamilyMembersAdapter(familyMembersList,this)
        adapter= FamilyMembersAdapter(familyMembersList,this@ManageFamilyMembersActivity)

        rvFamilyMembers.layoutManager = LinearLayoutManager(this@ManageFamilyMembersActivity)
        rvFamilyMembers.adapter = adapter

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
            var personFound=false
            val email = edAddFamilyMember.text.toString()
            for (member in familyMembersList)
            {
                if(member.email==email) {
                    Toast.makeText(this@ManageFamilyMembersActivity, "this person already exist", Toast.LENGTH_SHORT)
                            .show()
                    personFound=true
                    break
                }
            }
            if(!personFound) {
                findOnePersonNameByEmail(email)
            }

        }

        ivGoBackFamily.setOnClickListener {
            finish()
        }
    }


    private fun addNewFamilyMember() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val familyMembersEmails =
                Firebase.firestore.collection("persons").document(auth.currentUser.uid)

            val newFamilyMember = edAddFamilyMember.text.toString()
            familyMembersEmails.update("familyMembersMails", FieldValue.arrayUnion(newFamilyMember))
                .await()

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@ManageFamilyMembersActivity,
                    "Successfully Added new member",
                    Toast.LENGTH_SHORT
                ).show()
                makePersonToFamilyMember(newFamilyMember)
                // showAllFamilyMembers()


            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ManageFamilyMembersActivity, e.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showAllFamilyMembers() = CoroutineScope(Dispatchers.IO).launch {

        try {
            val currentUser = FirebaseAuth.getInstance().currentUser.uid
            val querySnapshot = personCollectionRef.get().await()
            var familyMembersEmails: MutableList<String> = mutableListOf()
            for (document in querySnapshot.documents) {
                if (document.id == currentUser) {
                    val person = document.toObject<Person>()
                    if (person != null) {
                        familyMembersEmails = person.familyMembersMails
                        break
                    }
                }
                //sb.append("$person\n")
            }
            withContext(Dispatchers.Main) {
               if (familyMembersEmails.size!=0)
               {
                findPersonNameByEmail(familyMembersEmails)
               }

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
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ManageFamilyMembersActivity, e.message, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun makePersonToFamilyMember(newFamilyMember: String) =
        CoroutineScope(Dispatchers.IO).launch {
            val personQuery = personCollectionRef
                .whereEqualTo("email", newFamilyMember)
                .get()
                .await()
            if (personQuery.documents.isNotEmpty()) {
                for (document in personQuery) {
                    try {
                        personCollectionRef.document(document.id).update("familyMember", true)
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main)
                        {
                            Toast.makeText(
                                this@ManageFamilyMembersActivity,
                                e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(
                        this@ManageFamilyMembersActivity,
                        "no person matched the query",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

    private fun findPersonNameByEmail(newFamilyMemberEmails: MutableList<String>) =
        CoroutineScope(Dispatchers.IO).launch {

            val personQuery = personCollectionRef
                .whereIn("email", newFamilyMemberEmails)
                .get()
                .await()
            val v = personQuery.documents.isNotEmpty()
            if (personQuery.documents.isNotEmpty()) {
                Log.d("lol", personQuery.documents.isNotEmpty().toString())
                for (document in personQuery) {
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
                    }
                }

            } else {
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(
                        this@ManageFamilyMembersActivity,
                        "no person matched the query",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            withContext(Dispatchers.Main)
            {
                //val adapter = FamilyMembersAdapter(familyMembersList,this@ManageFamilyMembersActivity)
               // rvFamilyMembers.adapter = adapter
                //rvFamilyMembers.layoutManager = LinearLayoutManager(this@ManageFamilyMembersActivity)
                adapter.notifyDataSetChanged()
            }

        }

    private fun findOnePersonNameByEmail(newFamilyMemberEmail: String) =
        CoroutineScope(Dispatchers.IO).launch {

            val personQuery = personCollectionRef
                .whereEqualTo("email", newFamilyMemberEmail)
                .get()
                .await()
            val v = personQuery.documents.isNotEmpty()
            if (personQuery.documents.isNotEmpty()) {
                Log.d("lol", personQuery.documents.isNotEmpty().toString())
                for (document in personQuery) {
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
                    }
                }

            } else {
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(
                        this@ManageFamilyMembersActivity,
                        "no person matched the query",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            withContext(Dispatchers.Main)
            {
               // val adapter = FamilyMembersAdapter(familyMembersList,this@ManageFamilyMembersActivity)
                //rvFamilyMembers.adapter = adapter
               //rvFamilyMembers.layoutManager = LinearLayoutManager(this@ManageFamilyMembersActivity)
                adapter.notifyItemInserted(familyMembersList.size - 1)
                Toast.makeText(this@ManageFamilyMembersActivity, "yeyeyeeye", Toast.LENGTH_SHORT)
                    .show()
                addNewFamilyMember()
            }

        }



    override fun onItemClick(familyMember: FamilyMember,p :Int) {
        Toast.makeText(this,"You clicked on item ",Toast.LENGTH_LONG).show()
        removeFamilyMember(familyMember.email,p)


    }

    private fun removeFamilyMember(FamilyMemberEmail: String,p :Int) =
        CoroutineScope(Dispatchers.IO).launch {
            val personQuery = personCollectionRef
                .whereEqualTo("email", FamilyMemberEmail)
                .get()
                .await()
            if (personQuery.documents.isNotEmpty()) {
                for (document in personQuery) {
                    try {
                        personCollectionRef.document(document.id).update("familyMember", false)
                        RemoveOnePersonNameByEmail(FamilyMemberEmail,p)
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main)
                        {
                            Toast.makeText(
                                this@ManageFamilyMembersActivity,
                                e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(
                        this@ManageFamilyMembersActivity,
                        "no person matched the query",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

    /*private fun deleteFamilyMemberFromEmails(FamilyMemberEmail: String) = CoroutineScope(Dispatchers.IO).launch {

        try {
            val currentUser = FirebaseAuth.getInstance().currentUser.uid
            personCollectionRef.document(currentUser).update("familyMembersEmails",FieldValue.arrayRemove(FamilyMemberEmail)).await()

            withContext(Dispatchers.Main) {
                Toast.makeText(this@ManageFamilyMembersActivity,"Family Member has been removed", Toast.LENGTH_LONG).show()


            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ManageFamilyMembersActivity, e.message, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }*/
    private fun RemoveOnePersonNameByEmail(FamilyMemberEmail: String,p: Int) =
        CoroutineScope(Dispatchers.IO).launch {


            try {
                val currentUser = FirebaseAuth.getInstance().currentUser.uid
                val documentSnapshot =personCollectionRef.document(currentUser).get().await()
                var familyMembersEmails: MutableList<String> = mutableListOf()
                val person = documentSnapshot.toObject<Person>()
                if (person != null) {
                            familyMembersEmails = person.familyMembersMails

                        }

                withContext(Dispatchers.Main) {
                    familyMembersEmails.remove(FamilyMemberEmail)
                    removeFamilyMemberFromTheList(familyMembersEmails,p)
                }


            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ManageFamilyMembersActivity, e.message, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    private fun removeFamilyMemberFromTheList(familyMembersEmails: MutableList<String> ,p :Int)=
        CoroutineScope(Dispatchers.IO).launch {


            try {
                val currentUser = FirebaseAuth.getInstance().currentUser.uid
                val documentSnapshot =personCollectionRef.document(currentUser).update("familyMembersMails",familyMembersEmails).await()


                withContext(Dispatchers.Main) {
                   // val adapter = FamilyMembersAdapter(familyMembersList,this@ManageFamilyMembersActivity)
                   //rvFamilyMembers.adapter = adapter
                   // rvFamilyMembers.layoutManager = LinearLayoutManager(this@ManageFamilyMembersActivity)
                    familyMembersList.removeAt(p)
                    adapter.notifyItemRemoved(p)
                    Toast.makeText(this@ManageFamilyMembersActivity, "family member has been deleted", Toast.LENGTH_LONG).show()

                }


            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ManageFamilyMembersActivity, e.message, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
}