package eniso.ia.lockify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_lock_and_unlock.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class LockAndUnlockFragment: Fragment(R.layout.activity_lock_and_unlock) {
    lateinit var auth: FirebaseAuth
   val personCollectionRef= Firebase.firestore.collection("persons")
    private lateinit var mContext: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    var doorState=true
    val CHANNEL_ID="channelId"
    val CHANNEL_NAME="channelName"
    val NOTIFICATION_ID=0

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        btnLock.setOnClickListener{
            Toast.makeText(getActivity(), "Door is now locked!", Toast.LENGTH_SHORT).show()
            doorState=true
        }





        createNotificationChannel()
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // a pending intent allows other app to execute apiece of code from our app
        val intent= Intent(mContext, BottomNavMenuActivity::class.java)
        val pendingIntent= TaskStackBuilder.create(mContext).run{
            addNextIntentWithParentStack(intent)
            // flag when the pending intent exist we want to update the intent with new data
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = NotificationCompat.Builder(mContext, CHANNEL_ID).setContentTitle("Lock the door !")
                .setAutoCancel(true)
                .setContentText("Apparently you didn't lock the door after you opened it ")
                .setSmallIcon(R.drawable.ic_baseline_lock_24)
                // how important this specific notification to other notifications you created
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                //.setSound(alarmSound)
                .build()

        val notificationManager = NotificationManagerCompat.from(mContext)



        btnUnlock.setOnClickListener {
            doorState=false
            Toast.makeText(getActivity(), "Door is now unlocked!", Toast.LENGTH_SHORT).show()

                val handler = Handler();
                handler.postDelayed(Runnable {
                    if (!doorState) {
                        notificationManager.notify(NOTIFICATION_ID, notification)
                    }

                }, 1000)

            }



        btnMembers.setOnClickListener {
            val intent= Intent(mContext, ManageFamilyMembersActivity::class.java)
            startActivity(intent)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    fun createNotificationChannel()
    {
        //check if the phone is running on android oreo or greater
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channel= NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                lightColor= Color.GREEN
                enableLights(true)
                enableVibration(true)
            }
            val manager = activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

        }
    }






}