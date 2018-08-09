package jugnoo.com.learningcustomvviews

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.TypedValue
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn.setOnClickListener{


            statusView.currentCount =  statusView.currentCount-1
        }

        btn2.setOnClickListener{
            statusView.currentCount =  statusView.currentCount+1
        }



    }
}
