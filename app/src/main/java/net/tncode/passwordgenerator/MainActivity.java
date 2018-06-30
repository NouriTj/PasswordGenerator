package net.tncode.passwordgenerator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.InterstitialAd;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import org.apache.commons.lang3.RandomStringUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    Button generate, copyToClipBord, share;
    TextView generatedPassword, lengthPassword;
    LinearLayout linearLayout;
    RadioGroup strengthPassword;
    DiscreteSeekBar seekBar;
    InterstitialAd interstitialAd;
    AdView bannerAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/captureit.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_home_logo);

        generate = (Button) findViewById(R.id.btn_generate);
        copyToClipBord = (Button) findViewById(R.id.btn_copy);
        share = (Button) findViewById(R.id.btn_share);
        generatedPassword = (TextView) findViewById(R.id.tv_generated_password);
        lengthPassword = (TextView) findViewById(R.id.tv_length);
        linearLayout = (LinearLayout) findViewById(R.id.generated_password);
        strengthPassword = (RadioGroup) findViewById(R.id.rg_strength_password);
        seekBar = (DiscreteSeekBar) findViewById(R.id.seekDiscreteSeekBar);
        bannerAdView = (AdView) findViewById(R.id.adView);

        initAds();
        lengthPassword.setText(String.valueOf(seekBar.getProgress()));

        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int progress, boolean b) {
                lengthPassword.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {

            }
        });

        copyToClipBord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showInterstitial();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(generatedPassword.getText(), generatedPassword.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, getString(R.string.text_copied), Toast.LENGTH_SHORT).show();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String message =  new StringBuilder().append(generatedPassword.getText())
                        .append(getString(R.string.text_share)).toString();
                shareAction(message);
            }
        });

        generate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showInterstitial();
                switch (strengthPassword.getCheckedRadioButtonId()) {
                    case R.id.rb_strong:
                        generatedPassword.setText(RandomStringUtils.randomAscii(seekBar.getProgress()));
                        break;
                    case R.id.rb_medium:
                        generatedPassword.setText(RandomStringUtils.randomAlphanumeric(seekBar.getProgress()));
                        break;
                    case R.id.rb_weak:
                        generatedPassword.setText(RandomStringUtils.randomAlphabetic(seekBar.getProgress()));
                        break;
                }
                share.setEnabled(true);
                share.setAlpha(1);
                copyToClipBord.setEnabled(true);
                copyToClipBord.setAlpha(1);
            }
        });

        strengthPassword.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                showInterstitial();
            }
        });
    }
    private void shareAction(String text){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share)));
    }
    private void initAds(){
        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_app_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAdView.loadAd(adRequest);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitiel_ad_unit_id));
    }
    private void showInterstitial() {
        if (!interstitialAd.isLoaded()) {
            AdRequest interstitialAdRequest = new AdRequest.Builder().build();
            interstitialAd.loadAd(interstitialAdRequest);
        }
        interstitialAd.show();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareAction(getString(R.string.text_share_app));
                return true;

            case R.id.action_about:
                Intent aboutIntent = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(aboutIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.menu, menu );
        return true;
    }
}
