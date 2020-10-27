package com.baobab.user.baobabflyer.server.util;

import android.content.Context;
import android.content.res.Resources;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.baobab.user.baobabflyer.R;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public class SslWebViewClient extends WebViewClient {

    @Override
    public void onReceivedSslError(WebView webView, SslErrorHandler handler, SslError error){
        SslCertificate sslCertificateServer = error.getCertificate();
        Certificate pinnedCert = getCertificateForRawResource(R.raw.ssl, webView.getContext().getApplicationContext());
        Certificate serverCert = convertSSLCertificateToCertificate(sslCertificateServer);

        if(pinnedCert.equals(serverCert)) {
            handler.proceed();
        } else {
            super.onReceivedSslError(webView, handler, error);
        }
    }

    public boolean shouldOverrideUrlLoading(WebView webView, String url){
        webView.loadUrl(url);
        return true;
    }

    public static Certificate getCertificateForRawResource(int resourceId, Context context) {
        CertificateFactory cf;
        Certificate ca = null;
        Resources resources = context.getResources();
        InputStream caInput = resources.openRawResource(resourceId);

        try {
            cf = CertificateFactory.getInstance("X.509");
            ca = cf.generateCertificate(caInput);
        } catch (CertificateException e) {
            Log.e("ssl", "exception", e);
        } finally {
            try {
                caInput.close();
            } catch (IOException e) {
                Log.e("ssl" , "exception", e);
            }
        }

        return ca;
    }

    public static Certificate convertSSLCertificateToCertificate(SslCertificate sslCertificate) {
        Certificate certificate = null;
        Bundle bundle = sslCertificate.saveState(sslCertificate);
        byte[] bytes = bundle.getByteArray("x509-certificate");

        if (bytes != null) {
            try {
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                Certificate cert = certFactory.generateCertificate(new ByteArrayInputStream(bytes));
                certificate = cert;
            } catch (CertificateException e) {
                Log.e("ssl", "exception", e);
            }
        }

        return certificate;
    }
}
