package ru.espepe.bubuka.player.parts;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.image.SmartImage;
import com.loopj.android.image.SmartImageView;

import java.io.File;

import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.dao.StorageFile;
import ru.espepe.bubuka.player.helper.MainHelper;

/**
 * Created by wolong on 28/08/14.
 */
public class ImageGridItemViewHolder {
    private StorageFile storageFile;

    public ImageGridItemViewHolder(StorageFile storageFile) {
        this.storageFile = storageFile;
    }

    public View getView(Context context, View convertView) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.image_grid_item, null);
        }


        SmartImageView imageView = (SmartImageView) convertView.findViewById(R.id.image_grid_item_image);
        //final int width = imageView.getMeasuredWidth();
        imageView.setImage(new SmartImage() {
            @Override
            public Bitmap getBitmap(Context context) {
                try {
                    if(storageFile.getType().equals("photo")) {
                        final File file = new File(new File(BubukaApplication.getInstance().getBubukaFilesDir(), "photo"), storageFile.getIdentity() + "_" + storageFile.getVersion());
                        return MainHelper.loadImage(context, file, context.getResources().getDimensionPixelSize(R.dimen.image_grid_item_size));
                    } else if(storageFile.getType().equals("video")) {
                        final File file = new File(new File(BubukaApplication.getInstance().getBubukaFilesDir(), "video"), storageFile.getIdentity() + "_" + storageFile.getVersion());
                        return ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
                    }
                } catch (Exception e) {

                }

                return null;
            }
        });


        return convertView;
    }
}
