package jugnoo.com.learningcustomvviews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Parminder Saini on 31/07/18.
 */
public class MainActivityJava extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusView statusView = new StatusView(this);
        statusView.setStatusCount(5);
    }
}
