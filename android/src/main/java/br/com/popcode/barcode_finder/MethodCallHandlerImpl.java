package br.com.popcode.barcode_finder;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.HashMap;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MethodCallHandlerImpl implements MethodChannel.MethodCallHandler {

    private final Context context;

    MethodCallHandlerImpl(Context context) {
        this.context = context;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, final @NonNull MethodChannel.Result result) {
        HashMap arguments = (HashMap) call.arguments;
        String filePath = (String) arguments.get("filePath");
        String barcodeFormat = (String) arguments.get("barcodeFormat");
        if (call.method.equals("scan_pdf")) {
            scanFile(result, filePath, EntryType.PDF, barcodeFormat);
        } else if (call.method.equals("scan_image")) {
            scanFile(result, filePath, EntryType.IMAGE, barcodeFormat);
        } else {
            result.notImplemented();
        }
    }

    private void scanFile(final @NonNull MethodChannel.Result result, String filePath, EntryType entryType, String barcodeFormat) {
        File file = new File(filePath);
        Uri uri = Uri.fromFile(file);
        ReadBarcodeFromFile readBarcodeFromFile = new ReadBarcodeFromFile(new OnBarcodeReceivedListener() {
            @Override
            public void onBarcodeFound(String code) {
                result.success(code);
            }

            @Override
            public void onBarcodeNotFound() {
                result.error("not-found", "No barcode found on the file", "");
            }

            @Override
            public void onOutOfMemory() {
                result.error("out-of-memory", "Out of memory", "");
            }
        }, context, uri, entryType, barcodeFormat);
        readBarcodeFromFile.execute();
    }
}
