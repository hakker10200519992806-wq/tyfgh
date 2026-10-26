package com.kalimaktab.kitob;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Ikonka bosilganda: assets/kali_maktab.pdf faylini (faqat birinchi marta)
 * ilova keshiga nusxalaydi, so'ng uni tizimning standart PDF ko'ruvchisida ochadi.
 */
public class MainActivity extends AppCompatActivity {

    private static final String FAYL_NOMI = "kali_maktab.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            File outFile = kitobniKeshgaNusxalash();
            kitobniOchish(outFile);
        } catch (Exception e) {
            Toast.makeText(this, "Xato: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        finish();
    }

    private File kitobniKeshgaNusxalash() throws Exception {
        File outFile = new File(getCacheDir(), FAYL_NOMI);
        long assetUzunlik;
        try (InputStream probe = getAssets().open(FAYL_NOMI)) {
            assetUzunlik = probe.available();
        }
        if (outFile.exists() && outFile.length() == assetUzunlik) {
            return outFile; // keshda bor, qayta nusxalash shart emas
        }
        try (InputStream in = getAssets().open(FAYL_NOMI);
             OutputStream out = new FileOutputStream(outFile)) {
            byte[] buf = new byte[1 << 16];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
        return outFile;
    }

    private void kitobniOchish(File pdfFayl) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", pdfFayl);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this,
                    "Telefoningizda PDF ko'ruvchi ilova topilmadi. Birortasini o'rnating.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
