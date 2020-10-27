package com.baobab.user.baobabflyer.server.util;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class CreateQR {

    public Bitmap createQR(String content){
        Bitmap bitmap = null;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try{
            bitmap = toBitmap(qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 250, 250));
            return bitmap;
        }catch (WriterException e){
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap toBitmap(BitMatrix matrix){
        int h = matrix.getHeight();
        int w = matrix.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        for(int x=0;x<w;x++){
            for(int y=0;y<h;y++){
                bitmap.setPixel(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return bitmap;
    }
}
