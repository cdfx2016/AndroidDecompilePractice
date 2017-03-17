package cn.finalteam.galleryfinal.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.Media;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.R;
import cn.finalteam.galleryfinal.model.PhotoFolderInfo;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PhotoTools {
    public static List<PhotoFolderInfo> getAllPhotoFolder(Context context, List<PhotoInfo> selectPhotoMap) {
        List<PhotoFolderInfo> allFolderList = new ArrayList();
        String[] projectionPhotos = new String[]{"_id", "bucket_id", "bucket_display_name", "_data", "datetaken", "orientation", "_data"};
        ArrayList<PhotoFolderInfo> allPhotoFolderList = new ArrayList();
        HashMap<Integer, PhotoFolderInfo> bucketMap = new HashMap();
        Cursor cursor = null;
        PhotoFolderInfo allPhotoFolderInfo = new PhotoFolderInfo();
        allPhotoFolderInfo.setFolderId(0);
        allPhotoFolderInfo.setFolderName(context.getResources().getString(R.string.all_photo));
        allPhotoFolderInfo.setPhotoList(new ArrayList());
        allPhotoFolderList.add(0, allPhotoFolderInfo);
        List<String> selectedList = GalleryFinal.getFunctionConfig().getSelectedList();
        List<String> filterList = GalleryFinal.getFunctionConfig().getFilterList();
        try {
            cursor = Media.query(context.getContentResolver(), Media.EXTERNAL_CONTENT_URI, projectionPhotos, "", null, "datetaken DESC");
            if (cursor != null) {
                int bucketNameColumn = cursor.getColumnIndex("bucket_display_name");
                int bucketIdColumn = cursor.getColumnIndex("bucket_id");
                while (cursor.moveToNext()) {
                    int bucketId = cursor.getInt(bucketIdColumn);
                    String bucketName = cursor.getString(bucketNameColumn);
                    int dataColumn = cursor.getColumnIndex("_data");
                    int imageId = cursor.getInt(cursor.getColumnIndex("_id"));
                    String path = cursor.getString(dataColumn);
                    File file = new File(path);
                    if ((filterList == null || !filterList.contains(path)) && file.exists() && file.length() > 0) {
                        PhotoInfo photoInfo = new PhotoInfo();
                        photoInfo.setPhotoId(imageId);
                        photoInfo.setPhotoPath(path);
                        if (allPhotoFolderInfo.getCoverPhoto() == null) {
                            allPhotoFolderInfo.setCoverPhoto(photoInfo);
                        }
                        allPhotoFolderInfo.getPhotoList().add(photoInfo);
                        PhotoFolderInfo photoFolderInfo = (PhotoFolderInfo) bucketMap.get(Integer.valueOf(bucketId));
                        if (photoFolderInfo == null) {
                            photoFolderInfo = new PhotoFolderInfo();
                            photoFolderInfo.setPhotoList(new ArrayList());
                            photoFolderInfo.setFolderId(bucketId);
                            photoFolderInfo.setFolderName(bucketName);
                            photoFolderInfo.setCoverPhoto(photoInfo);
                            bucketMap.put(Integer.valueOf(bucketId), photoFolderInfo);
                            allPhotoFolderList.add(photoFolderInfo);
                        }
                        photoFolderInfo.getPhotoList().add(photoInfo);
                        if (selectedList != null && selectedList.size() > 0 && selectedList.contains(path)) {
                            selectPhotoMap.add(photoInfo);
                        }
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception ex) {
            ILogger.e(ex);
            allFolderList.addAll(allPhotoFolderList);
            if (selectedList != null) {
                selectedList.clear();
            }
            return allFolderList;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        allFolderList.addAll(allPhotoFolderList);
        if (selectedList != null) {
            selectedList.clear();
        }
        return allFolderList;
    }
}
