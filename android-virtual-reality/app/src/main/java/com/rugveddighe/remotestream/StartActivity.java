package com.rugveddighe.remotestream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sveder.remotestream.R;

/**
 * Created by rugved.
 */
public class StartActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button enter=(Button)findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(StartActivity.this,ConfigActivity.class);
                startActivity(intent);
            }
        });
    }

}
