package sunny.player;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import sunny.player.base.BaseActivity;
import sunny.player.exitapp.ExitAppTipActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.main_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpExitAppPage();
            }
        });
    }

    private void jumpExitAppPage(){
        Intent intent = new Intent(this, ExitAppTipActivity.class);
        startActivity(intent);
    }
}
