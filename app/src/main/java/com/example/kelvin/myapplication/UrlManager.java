package com.example.kelvin.myapplication;



import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class UrlManager {
    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=signralnoor;AccountKey=6PknB/u6O6IVH+GJB54UDBH/t0cLrpQxsWD28QPQVQkK6WJcM6GZ9OVzDhKS3TqMKAnODy0I5AHccnc3uynZFg==;EndpointSuffix=core.windows.net";


    public static CloudBlobClient getClient() throws Exception {
        CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
        return blobClient;
    }


    public static Map<String,String> ListVideos() throws Exception{
        CloudBlobClient client = getClient();
        CloudBlobContainer container = client.getContainerReference("videos");

        Map<String,String> urlMappings = new HashMap<String, String>();
        Iterable<ListBlobItem> blobs = container.listBlobs();

        for(ListBlobItem blob: blobs)
        {
            CloudBlockBlob cloudBlockBlob = (CloudBlockBlob) blob;
            String name = cloudBlockBlob.getName();
            name = name.substring(0,name.length() - 4);
            urlMappings.put(name,blob.getUri().toString());
        }

        return urlMappings;
    }
}