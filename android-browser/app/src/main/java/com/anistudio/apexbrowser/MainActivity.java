package com.anistudio.apexbrowser;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private final int bg = Color.rgb(8, 9, 18);
    private final int panel = Color.rgb(20, 23, 39);
    private final int cyan = Color.rgb(37, 244, 238);
    private final int pink = Color.rgb(255, 45, 117);
    private EditText address;
    private WebView web;
    private TextView status;
    private boolean adBlock = true;

    private final List<String> blockedHosts = Arrays.asList(
        "doubleclick.net", "googlesyndication.com", "googleadservices.com",
        "adservice.google.com", "adnxs.com", "taboola.com", "outbrain.com",
        "adroll.com", "ads.yahoo.com", "pagead2.googlesyndication.com"
    );

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(bg);
        root.setPadding(dp(12), dp(10), dp(12), dp(5));

        TextView brand = new TextView(this);
        brand.setText("◈ APEX BROWSER");
        brand.setTextColor(Color.WHITE);
        brand.setTextSize(21);
        brand.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        brand.setPadding(dp(4), dp(4), 0, dp(8));
        root.addView(brand);

        TextView subtitle = new TextView(this);
        subtitle.setText("GX MODE  •  PRIVATE-STYLE BROWSING");
        subtitle.setTextColor(cyan);
        subtitle.setTextSize(10);
        subtitle.setPadding(dp(5), 0, 0, dp(10));
        root.addView(subtitle);

        address = new EditText(this);
        address.setSingleLine(true);
        address.setTextColor(Color.WHITE);
        address.setHintTextColor(Color.LTGRAY);
        address.setHint("Search or enter website");
        address.setTextSize(15);
        address.setPadding(dp(12), 0, dp(8), 0);
        address.setImeOptions(EditorInfo.IME_ACTION_GO);
        address.setBackground(round(panel, cyan, 1, 14));
        root.addView(address, new LinearLayout.LayoutParams(-1, dp(48)));

        LinearLayout controls = new LinearLayout(this);
        controls.setGravity(Gravity.CENTER);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setPadding(0, dp(8), 0, dp(8));
        addButton(controls, "‹", cyan, v -> { if (web.canGoBack()) web.goBack(); });
        addButton(controls, "›", cyan, v -> { if (web.canGoForward()) web.goForward(); });
        addButton(controls, "↻", cyan, v -> web.reload());
        addButton(controls, "GO", pink, v -> loadAddress());
        addButton(controls, adBlock ? "AD ON" : "AD OFF", adBlock ? cyan : pink, v -> {
            adBlock = !adBlock;
            Toast.makeText(this, adBlock ? "Basic ad blocking enabled" : "Ad blocking disabled", Toast.LENGTH_SHORT).show();
            recreate();
        });
        root.addView(controls, new LinearLayout.LayoutParams(-1, dp(48)));

        web = new WebView(this);
        web.setBackgroundColor(Color.WHITE);
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSupportMultipleWindows(false);
        web.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String scheme = request.getUrl().getScheme();
                if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) return true;
                address.setText(request.getUrl().toString());
                return false;
            }
            @Override public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                address.setText(url);
                status.setText("LOADING  •  " + host(url));
            }
            @Override public void onPageFinished(WebView view, String url) {
                address.setText(url);
                status.setText("READY  •  " + host(url) + (adBlock ? "  •  BASIC AD BLOCK ON" : ""));
            }
            @Override public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (adBlock) {
                    String host = request.getUrl().getHost();
                    if (host != null) {
                        String h = host.toLowerCase(Locale.ROOT);
                        for (String blocked : blockedHosts) {
                            if (h.equals(blocked) || h.endsWith("." + blocked)) {
                                return new WebResourceResponse("text/plain", "UTF-8", new ByteArrayInputStream(new byte[0]));
                            }
                        }
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }
        });
        root.addView(web, new LinearLayout.LayoutParams(-1, 0, 1f));

        status = new TextView(this);
        status.setText("READY  •  SEARCH OR ENTER A WEBSITE");
        status.setTextColor(cyan);
        status.setTextSize(10);
        status.setPadding(dp(4), dp(8), dp(4), dp(5));
        root.addView(status);

        setContentView(root);
        address.setOnEditorActionListener((v, action, event) -> {
            if (action == EditorInfo.IME_ACTION_GO || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                loadAddress(); return true;
            }
            return false;
        });
        web.loadDataWithBaseURL(null, homePage(), "text/html", "UTF-8", null);
    }

    private String homePage() {
        return "<!doctype html><html><head><meta name='viewport' content='width=device-width, initial-scale=1'><style>" +
          "body{margin:0;background:#080912;color:#f4f7ff;font:16px Arial;padding:26px}h1{font-size:30px}b{color:#25f4ee}.card{background:#141727;border:1px solid #343149;border-radius:16px;padding:18px;margin:18px 0}a{color:#25f4ee;text-decoration:none;display:inline-block;margin:8px}" +
          "small{color:#9399b2}.glow{color:#ff2d75}</style></head><body><h1>◈ APEX <span class='glow'>BROWSER</span></h1><b>YOUR WEB. YOUR RULES.</b><div class='card'><h2>Quick launch</h2><a href='https://www.google.com'>Google</a><a href='https://www.youtube.com'>YouTube</a><a href='https://wikipedia.org'>Wikipedia</a><a href='https://github.com'>GitHub</a><p><small>Type a website or search in the address bar above.</small></p></div><div class='card'><b>Basic ad filtering</b><p><small>Blocks a small list of known ad-serving hosts. This is not a complete ad blocker or antivirus.</small></p></div></body></html>";
    }

    private void loadAddress() {
        String raw = address.getText().toString().trim();
        if (raw.isEmpty()) return;
        String url;
        if (raw.matches("(?i)^https?://.*")) url = raw;
        else if (raw.matches("(?i)^[a-z0-9.-]+\\.[a-z]{2,}(:[0-9]+)?([/?#].*)?$")) url = "https://" + raw;
        else {
            try { url = "https://www.google.com/search?q=" + URLEncoder.encode(raw, "UTF-8"); }
            catch (Exception e) { url = "https://www.google.com"; }
        }
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            Toast.makeText(this, "Only HTTP and HTTPS websites are supported", Toast.LENGTH_SHORT).show(); return;
        }
        web.loadUrl(url);
    }

    private String host(String url) {
        try { return android.net.Uri.parse(url).getHost() == null ? url : android.net.Uri.parse(url).getHost(); }
        catch (Exception e) { return "WEB"; }
    }

    private void addButton(LinearLayout row, String label, int color, View.OnClickListener listener) {
        Button b = new Button(this);
        b.setText(label); b.setTextColor(color); b.setTextSize(11);
        b.setAllCaps(false);
        b.setBackground(round(panel, color, 1, 10));
        b.setPadding(dp(4), 0, dp(4), 0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(42), 1f);
        lp.setMargins(dp(2), 0, dp(2), 0);
        row.addView(b, lp);
        b.setOnClickListener(listener);
    }

    private GradientDrawable round(int color, int stroke, int width, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(radius)); d.setStroke(dp(width), stroke);
        return d;
    }
    private int dp(int value) { return (int)(value * getResources().getDisplayMetrics().density + 0.5f); }

    @Override public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }
}
