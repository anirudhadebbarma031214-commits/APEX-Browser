package com.apex.browser;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {

    WebView webView;
    EditText addressBar;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(Color.rgb(8, 11, 18));

        // APEX title
        TextView title = new TextView(this);
        title.setText("APEX");
        title.setTextColor(Color.WHITE);
        title.setTextSize(22);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 20, 0, 15);

        main.addView(title);

        // Address bar
        LinearLayout bar = new LinearLayout(this);
        bar.setPadding(12, 5, 12, 8);

        addressBar = new EditText(this);
        addressBar.setHint("Search or enter website");
        addressBar.setTextColor(Color.WHITE);
        addressBar.setHintTextColor(Color.GRAY);
        addressBar.setSingleLine(true);
        addressBar.setBackgroundColor(Color.rgb(25, 30, 42));

        Button go = new Button(this);
        go.setText("GO");

        bar.addView(addressBar,
                new LinearLayout.LayoutParams(0, 55, 1));

        bar.addView(go,
                new LinearLayout.LayoutParams(90, 55));

        main.addView(bar);

        // Loading bar
        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);
        main.addView(progressBar);

        // Browser
        webView = new WebView(this);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        main.addView(webView,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                ));

        setContentView(main);

        go.setOnClickListener(v -> openWebsite());

        addressBar.setOnEditorActionListener((v, actionId, event) -> {
            openWebsite();
            return true;
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                addressBar.setText(url);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                addressBar.setText(url);
                progressBar.setVisibility(View.GONE);
            }
        });

        webView.loadUrl("https://www.google.com");
    }

    private void openWebsite() {

        String input = addressBar.getText().toString().trim();

        if (input.isEmpty()) {
            return;
        }

        if (input.startsWith("http://") || input.startsWith("https://")) {
            webView.loadUrl(input);
        } else if (input.contains(".")) {
            webView.loadUrl("https://" + input);
        } else {
            String search =
                    "https://www.google.com/search?q=" +
                    android.net.Uri.encode(input);

            webView.loadUrl(search);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
