package jugnoo.com.learningcustomvviews

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn.setOnClickListener{

           statusView.textColorStatus = ContextCompat.getColor(this,android.R.color.holo_red_dark);
           statusView.textSizeStatus =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,20.0f,resources.displayMetrics)
        }

    }
}
