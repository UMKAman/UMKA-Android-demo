// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package com.umka.umka.classes;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess, unused")
public final class CropImage {

    private CropImage() {
    }



    public static Intent getPickImageChooserIntent(@NonNull Context context) {
        return getPickImageChooserIntent(context, "Выберите приложение", false);
    }

    public static Intent getPickImageChooserIntent(@NonNull Context context, CharSequence title, boolean includeDocuments) {

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        allIntents.addAll(getCameraIntents(context, packageManager));

        List<Intent> galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_GET_CONTENT, includeDocuments);
        if (galleryIntents.size() == 0) {
            // if no intents found for get-content try pick intent action (Huawei P9).
            galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_PICK, includeDocuments);
        }
        allIntents.addAll(galleryIntents);

        Intent target;
        if (allIntents.isEmpty()) {
            target = new Intent();
        } else {
            target = allIntents.get(allIntents.size() - 1);
            allIntents.remove(allIntents.size() - 1);
        }

        // Create a chooser from the main  intent
        Intent chooserIntent = Intent.createChooser(target, title);

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    public static Intent getCameraIntent(@NonNull Context context, Uri outputFileUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (outputFileUri == null) {
            outputFileUri = getCaptureImageOutputUri(context);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        return intent;
    }

    /**
     * Get all Camera intents for capturing image using device camera apps.
     */
    public static List<Intent> getCameraIntents(@NonNull Context context, @NonNull PackageManager packageManager) {

        List<Intent> allIntents = new ArrayList<>();

        // Determine Uri of camera image to  save.
        Uri outputFileUri = getCaptureImageOutputUri(context);

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        return allIntents;
    }

    /**
     * Get all Gallery intents for getting image from one of the apps of the device that handle images.
     */
    public static List<Intent> getGalleryIntents(@NonNull PackageManager packageManager, String action, boolean includeDocuments) {
        List<Intent> intents = new ArrayList<>();
        Intent galleryIntent = action == Intent.ACTION_GET_CONTENT ? new Intent(action)
                : new Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            intents.add(intent);
        }

        // remove documents intent
        if (!includeDocuments) {
            for (Intent intent : intents) {
                if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                    intents.remove(intent);
                    break;
                }
            }
        }
        return intents;
    }

    public static Uri getCaptureImageOutputUri(@NonNull Context context) {
        Uri outputFileUri = null;
        File getImage = context.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    public static Uri getPickImageResultUri(@NonNull Context context, @Nullable Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera || data.getData() == null ? getCaptureImageOutputUri(context) : data.getData();
    }
}